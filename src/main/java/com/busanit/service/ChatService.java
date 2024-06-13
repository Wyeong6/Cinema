package com.busanit.service;

import com.busanit.constant.Role;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.ChatRoomReadStatus;
import com.busanit.entity.chat.Message;
import com.busanit.repository.ChatRoomReadStatusRepository;
import com.busanit.repository.ChatRoomRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.busanit.domain.chat.ChatRoomDTO.toChatRoomDTO;

@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomReadStatusRepository chatRoomReadStatusRepository;

    // 메시지 저장
    public void saveMessage(MessageDTO messageDTO) {
        Member sender = findMemberByEmail(messageDTO.getSender());
        Member receiver = findMemberByEmail(messageDTO.getRecipient());
//      ChatRoom chatRoom = getOrCreateChatRoom(messageDTO.getChatRoomTitle(), messageDTO.getSender(), messageDTO.getRecipient());
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(messageDTO.getChatRoomId());
        //메세지 생성
        Message message = Message.createMessage(sender, receiver, messageDTO, chatRoom);
        //연관메서드
        updateEntitiesWithNewMessage(sender, receiver, chatRoom, message);
        //메세지 저장
        messageRepository.save(message);
//       //메세지 상태추가
        createReadStatus(chatRoom, sender, receiver);
    }

    // 메시지 상태를 생성 또는 업데이트하는 메서드
    private void createReadStatus(ChatRoom chatRoom, Member sender, Member receiver) {
        // 채팅방과 발신자/수신자에 대한 읽음 상태를 가져오거나 새로 생성
        ChatRoomReadStatus senderReadStatus = findOrCreateReadStatus(chatRoom, sender);
        ChatRoomReadStatus receiverReadStatus = findOrCreateReadStatus(chatRoom, receiver);

        // 발신자의 마지막 읽은 시간을 현재 시간으로 설정
        senderReadStatus.setLastReadTimestamp(LocalDateTime.now());

        //유저가 다른 카테고리 선택시,수신자가 다른 사용자일 경우, 수신자의 읽음 상태를 업데이트
        if (!receiverReadStatus.getMember().equals(receiver)) {
            receiverReadStatus = findOrCreateReadStatus(chatRoom, receiver);
        }

        // 변경사항을 데이터베이스에 저장
        chatRoom.addReadStatus(senderReadStatus);
        chatRoom.addReadStatus(receiverReadStatus);

    }

    // 채팅방과 멤버에 대한 읽음 상태를 찾거나, 없으면 새로 생성하는 메서드
    private ChatRoomReadStatus findOrCreateReadStatus(ChatRoom chatRoom, Member member) {
        return chatRoomReadStatusRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseGet(() -> {
                    // 읽음 상태가 없을 경우 새로 생성
                    ChatRoomReadStatus newStatus = new ChatRoomReadStatus();
                    newStatus.setChatRoom(chatRoom);
                    newStatus.setMember(member);
                    return newStatus;
                });
    }

    // 채팅방 리스트 페이징 조회
    public Page<ChatRoomDTO> getChatList(int page, int size, String memberEmail) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByMemberEmail(memberEmail, pageable);
        // chatRoomPage의 상태 확인
        System.out.println("chatRoomPage: " + chatRoomPage);

        List<ChatRoomDTO> chatRoomList = chatRoomPage.getContent().stream()
                .peek(chatRoom -> System.out.println("Processing chatRoom: " + chatRoom)) // 각 chatRoom 상태 확인
                .map(this::convertToChatRoomDTO)
                .peek(chatRoomDTO -> { // map 대신 peek을 사용하여 DTO 변환 후 처리
                    System.out.println("Before calculateUnreadMessages - chatRoomDTO: " + chatRoomDTO); // convertToChatRoomDTO 결과 확인

                    // 마지막 메시지의 받는 사람 가져오기
                    String lastMessageRecipient = getLastMessageRecipient(chatRoomDTO);
                    System.out.println("Before calculateUnreadMessages - getRecipient(): " + lastMessageRecipient);

                    // 읽지 않은 메시지 수 계산
                    int unreadMessages = calculateUnreadMessages(chatRoomDTO.getId(), lastMessageRecipient);
                    chatRoomDTO.setUnreadMessageCount(unreadMessages);
                    System.out.println("After calculateUnreadMessages - unreadMessages: " + unreadMessages); // unreadMessages 값 확인
                })
                .collect(Collectors.toList());

        System.out.println("chatRoomList: " + chatRoomList); // 최종 chatRoomList 상태 확인

        return new PageImpl<>(chatRoomList, pageable, chatRoomPage.getTotalElements());
    }

    // ChatRoomDTO에서 마지막 메시지의 받는 사람 가져오기
    private String getLastMessageRecipient(ChatRoomDTO chatRoomDTO) {
        List<MessageDTO> messages = chatRoomDTO.getMessages();
        if (messages != null && !messages.isEmpty()) {
            MessageDTO lastMessage = messages.get(messages.size() - 1);
            return lastMessage.getRecipient();
        }
        return null; // 만약 메시지가 없을 경우 null 반환 혹은 예외처리 추가
    }

    // 사용자 이메일로 채팅방 메세지 조회
    public List<ChatRoomDTO> findChatRoomByUserEmail(String recipient) {
        //로그인 한 사람
        String readEmail = getAuthenticatedUserEmail();
        //유저들이 있는 채팅방의 상태가 active인 것
        List<ChatRoom> chatRooms = chatRoomRepository.findByRecipientAndSender(recipient, readEmail);
        System.out.println("Found chat rooms: " + chatRooms.size());

        return chatRooms.stream()
                .map(chatRoom -> {
                    System.out.println("Updating last read timestamp for chat room: " + chatRoom.getId());

                    // 해당 채팅방의 메시지를 가져오면서 읽은 시간을 업데이트
                    updateLastReadTimestamp(chatRoom.getId());
                    System.out.println("updateLastReadTimestamp 가져오고나서  ");
                    return convertToChatRoomDTOWithMessages(chatRoom, readEmail);
                })
                .collect(Collectors.toList());
    }

    // 채팅방 생성 또는 조회
    public ChatRoom getOrCreateChatRoom(String chatRoomTitle, String senderEmail, String recipientEmail) {
        return handleUserMessage(chatRoomTitle, senderEmail, recipientEmail);

    }

    public List<ChatRoomDTO> findChatRoomByChatRoomId(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);

        Optional<Message> firstMessageOptional = chatRoom.getMessages().stream().findFirst();
        Member sender = firstMessageOptional.map(Message::getSender).orElse(null);
        Member receiver = firstMessageOptional.map(Message::getReceiver).orElse(null);

        //메세지 상태추가
//        createReadStatus(chatRoom, sender, receiver);
        updateLastReadTimestamp(chatRoomId);

        return Collections.singletonList(convertToChatRoomDTO(chatRoom));
    }

    // 이메일로 회원 조회
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email: " + email));
    }

    //로그인한 유저의 채팅중인 채팅룸
    public List<String> findCHatRoomByRecipient() {
        String loginUser = getAuthenticatedUserEmail();
        List<ChatRoom> activeChatRooms = chatRoomRepository.findActiveChatRoomsByMemberEmail(loginUser);
        List<Member> recipients = messageRepository.findReceiversByChatRooms(activeChatRooms);


        // 로그인된 사용자의 이메일을 제외한 이메일 목록을 반환
        return recipients.stream()
                .map(Member::getEmail) // Member 엔티티에 getEmail() 메소드가 존재한다고 가정
                .filter(email -> !email.equals(loginUser)) // 로그인된 사용자의 이메일 제외
                .collect(Collectors.toList());
    }

    // 사용자 메시지 처리
    private ChatRoom handleUserMessage(String chatRoomTitle, String senderEmail, String recipientEmail) {
        Member sender = findMemberByEmail(senderEmail);
        Member receiver = findMemberByEmail(recipientEmail);

        // sender와 receiver가 동일한 채팅방이 active인 지 확인 후 없으면 생성
        return sender.getChatRooms().stream()
                .filter(chatRoom -> chatRoom.getMembers().contains(receiver) && chatRoom.getType().equals("active") )
                .findFirst()
                .orElseGet(() -> createNewChatRoom(chatRoomTitle, sender, receiver));
    }

    // 새 채팅방 생성
    private ChatRoom createNewChatRoom(String title, Member sender, Member receiver) {
        System.out.println("새채팅방생성 title" + title);
        List<Member> members = Arrays.asList(sender, receiver);

        ChatRoom newChatRoom = ChatRoom.builder()
                .title(title)
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .type("active")
                .build();

        newChatRoom.addMembers(members);
        return chatRoomRepository.save(newChatRoom);
    }

    // 새 메시지로 엔티티 업데이트
    private void updateEntitiesWithNewMessage(Member sender, Member receiver, ChatRoom chatRoom, Message message) {
        sender.addSentMessage(message);
        receiver.addReceivedMessage(message);
        chatRoom.addMessage(message);
    }
    //상태 업데이트
    public void updateChatRoomStatus(Long chatRoomId, String status) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if (optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            chatRoom.setType(status);
            chatRoomRepository.save(chatRoom);
        } else {
            // 채팅 방이 없는 경우 예외 처리
            throw new IllegalArgumentException("Chat room not found with ID: " + chatRoomId);
        }
    }

    // 읽지 않은 메시지 수를 계산하는 메서드
    private int calculateUnreadMessages(Long chatRoomId, String memberEmail) {
        // 채팅방과 멤버에 대한 읽음 상태를 가져옴
        Optional<ChatRoomReadStatus> chatRoomReadStatus = chatRoomReadStatusRepository.findByChatRoomIdAndMemberEmail(chatRoomId, memberEmail);
        LocalDateTime lastReadTimestamp = chatRoomReadStatus.map(ChatRoomReadStatus::getLastReadTimestamp).orElse(null);

        if (lastReadTimestamp == null) {
            // 만약 lastReadTimestamp가 null일 경우, 채팅방의 전체 메시지 수를 반환
            return messageRepository.countByChatRoomId(chatRoomId);
        } else {
            // lastReadTimestamp 이후의 메시지 수를 계산하여 반환
            return messageRepository.countByChatRoomIdAndRegDateAfter(chatRoomId, lastReadTimestamp);
        }
    }

    // 사용자의 마지막 읽은 시간을 업데이트하는 메서드
    public void updateLastReadTimestamp(Long chatRoomId) {

        String readEmail = getAuthenticatedUserEmail();

        // 채팅방 ID로 채팅방을 찾음
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id " + chatRoomId));

        // 해당 사용자의 읽은 상태를 찾음
        ChatRoomReadStatus userReadStatus = chatRoom.getReadStatuses().stream()
                .filter(readStatus -> readEmail.equals(readStatus.getMember().getEmail()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ReadStatus not found for user " + readEmail));

        // 만약 읽은 상태가 존재하지 않는 경우 새로운 상태를 생성
        if (userReadStatus == null) {
            userReadStatus = new ChatRoomReadStatus();
            userReadStatus.setMember(memberRepository.findByEmail(readEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with email " + readEmail)));
            userReadStatus.setChatRoom(chatRoom);
            chatRoom.getReadStatuses().add(userReadStatus);
        }
        // 읽은 시간을 현재 시간으로 업데이트
        userReadStatus.setLastReadTimestamp(LocalDateTime.now());

        // 변경된 채팅방 객체를 저장
        chatRoomRepository.save(chatRoom);
    }

    // ChatRoomDTO 변환
    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        Optional<Member> userMemberOpt = chatRoom.getMembers().stream()
                .filter(member -> member.getRole() == Role.USER)
                .findFirst();

        String userEmail = userMemberOpt.map(Member::getEmail).orElse("No user found");
        String userName = userMemberOpt.map(Member::getName).orElse("No user found");

        List<MessageDTO> messageDTOs = messageRepository.findByChatRoomId(chatRoom.getId()).stream()
                .map(MessageDTO::toMessageDTO)
                .collect(Collectors.toList());

        return toChatRoomDTO(chatRoom, userEmail, userName, messageDTOs);
    }

    // 메시지 포함한 ChatRoomDTO 변환
    private ChatRoomDTO convertToChatRoomDTOWithMessages(ChatRoom chatRoom, String userEmail) {
        List<Message> messages = messageRepository.findByChatRoomId(chatRoom.getId());
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::toMessageDTO)
                .collect(Collectors.toList());

        String userName = memberRepository.findNameByEmail(userEmail).orElse("No name");

        return toChatRoomDTO(chatRoom, userEmail, userName, messageDTOs);
    }

    //로그인한 유저 검사
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
    }

    //로그인한 유저의 이메일을 리턴
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            System.out.println("Authenticated user email: " + authentication.getName());
            return authentication.getName();
        }
        return null;
    }
}
