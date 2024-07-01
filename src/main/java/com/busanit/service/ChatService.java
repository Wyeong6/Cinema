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
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(messageDTO.getChatRoomId());
        //메세지 생성
        Message message = Message.createMessage(sender, receiver, messageDTO, chatRoom);
        //연관메서드
        updateEntitiesWithNewMessage(sender, receiver, chatRoom, message);
        //메세지 저장
        messageRepository.save(message);
        //메세지 상태추가
        createReadStatus(chatRoom, sender, receiver);
    }

    // 활성 채팅방 리스트 페이징 조회
    public Page<ChatRoomDTO> getActiveChatList(int page, int size, String memberEmail) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByMembersEmailAndType(memberEmail, "active", pageable);

        return getPagedChatRoomList(memberEmail, chatRoomPage);
    }

    // 비활성 채팅방 리스트 페이징 조회
    public Page<ChatRoomDTO> getInactiveChatList(int page, int size, String memberEmail) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByMembersEmailAndType(memberEmail, "inactive", pageable);

        return getPagedChatRoomList(memberEmail, chatRoomPage);
    }

    // 전체 채팅방 리스트 페이징 조회
    public Page<ChatRoomDTO> getChatList(int page, int size, String memberEmail) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByMemberEmail(memberEmail, pageable);

        return getPagedChatRoomList(memberEmail, chatRoomPage);
    }

    // 사용자 이메일로 채팅방의 상태가 active인 메세지 조회
    public List<ChatRoomDTO> findChatRoomByUserEmail(String recipient) {
        //로그인 한 사람
        String readEmail = getAuthenticatedUserEmail();
        //유저들이 있는 채팅방의 상태가 active인 것
        List<ChatRoom> chatRooms = chatRoomRepository.findByRecipientAndSender(recipient, readEmail);

        return chatRooms.stream()
                .map(chatRoom -> {
                    // 해당 채팅방의 메시지를 가져오면서 읽은 시간을 업데이트
                    updateLastReadTimestamp(chatRoom.getId());
                    return convertToChatRoomDTOWithMessages(chatRoom, readEmail);
                })
                .collect(Collectors.toList());
    }

    //클릭한 채팅방 메세지 읽음 표시
    public List<ChatRoomDTO> findChatRoomByChatRoomId(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
        //메세지 상태추가
        updateLastReadTimestamp(chatRoomId);
        return Collections.singletonList(convertToChatRoomDTO(chatRoom));
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
    public ChatRoom handleUserMessage(String chatRoomTitle, String senderEmail, String recipientEmail) {
        Member sender = findMemberByEmail(senderEmail);
        Member receiver = findMemberByEmail(recipientEmail);

        // sender와 receiver가 동일한 채팅방이 active인 지 확인 후 없으면 생성
        return sender.getChatRooms().stream()
                .filter(chatRoom -> chatRoom.getMembers().contains(receiver) && chatRoom.getType().equals("active"))
                .findFirst()
                .orElseGet(() -> createNewChatRoom(chatRoomTitle, sender, receiver));
    }

    //채팅 상태를 종료로 설정
    public void updateChatRoomStatus(Long chatRoomId, String status) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with ID: " + chatRoomId));

        chatRoom.setType(status);
        chatRoomRepository.save(chatRoom);
    }

    // 사용자의 마지막 읽은 시간을 업데이트하는 메서드 (인증된 사용자의 이메일 사용)
    public void updateLastReadTimestamp(Long chatRoomId) {
        String readEmail = getAuthenticatedUserEmail();
        updateLastReadTimestamp(chatRoomId, readEmail);
    }

    //로그인한 유저 검사
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
    }

    //로그인한 유저의 이메일을 리턴
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return null;
    }

    // 메시지 상태를 생성 또는 업데이트하는 메서드
    private void createReadStatus(ChatRoom chatRoom, Member sender, Member receiver) {
        // 채팅방과 발신자/수신자에 대한 읽음 상태를 가져오거나 새로 생성
        ChatRoomReadStatus senderReadStatus = findOrCreateReadStatus(chatRoom, sender);
        ChatRoomReadStatus receiverReadStatus = findOrCreateReadStatus(chatRoom, receiver);

        // 발신자의 마지막 읽은 시간을 현재 시간으로 설정
        senderReadStatus.setLastReadTimestamp(LocalDateTime.now());

        // 변경사항을 데이터베이스에 저장
        chatRoom.addReadStatus(senderReadStatus);
        chatRoom.addReadStatus(receiverReadStatus);

        chatRoomRepository.save(chatRoom);
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

    // 채팅방 리스트 페이징 조회 (공통 메서드)
    public Page<ChatRoomDTO> getPagedChatRoomList(String memberEmail, Page<ChatRoom> chatRoomPage) {
        List<ChatRoomDTO> chatRoomList = chatRoomPage.getContent().stream()
                .peek(chatRoom -> System.out.println("Processing chatRoom: " + chatRoom))
                .map(this::convertToChatRoomDTO)
                .peek(chatRoomDTO -> {
                    int unreadMessages = calculateUnreadMessages(chatRoomDTO.getId(), memberEmail);
                    chatRoomDTO.setUnreadMessageCount(unreadMessages);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(chatRoomList, chatRoomPage.getPageable(), chatRoomPage.getTotalElements());
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

    // 사용자의 마지막 읽은 시간을 업데이트하는 메서드 (지정된 이메일 사용)
    public void updateLastReadTimestamp(Long chatRoomId, String userEmail) {
        // 채팅방 ID로 채팅방을 찾음
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id " + chatRoomId));

        // 해당 사용자의 읽은 상태를 찾음
        ChatRoomReadStatus userReadStatus = findOrCreateUserReadStatus(chatRoom, userEmail);

        // 읽은 시간을 현재 시간으로 업데이트
        userReadStatus.setLastReadTimestamp(LocalDateTime.now());

        // 변경된 채팅방 객체를 저장
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방과 사용자의 이메일을 이용해 읽은 상태를 찾거나 새로 생성하는 메서드
    private ChatRoomReadStatus findOrCreateUserReadStatus(ChatRoom chatRoom, String userEmail) {
        // 해당 사용자의 읽은 상태를 찾음
        ChatRoomReadStatus userReadStatus = chatRoom.getReadStatuses().stream()
                .filter(readStatus -> userEmail.equals(readStatus.getMember().getEmail()))
                .findFirst()
                .orElse(null);

        // 읽은 상태가 존재하지 않는 경우 새로운 상태를 생성하고 추가
        if (userReadStatus == null) {
            userReadStatus = new ChatRoomReadStatus();
            userReadStatus.setMember(memberRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with email " + userEmail)));
            userReadStatus.setChatRoom(chatRoom);
            chatRoom.getReadStatuses().add(userReadStatus);
        }

        return userReadStatus;
    }

    // 이메일로 회원 조회
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email: " + email));
    }

    // 새 채팅방 생성
    private ChatRoom createNewChatRoom(String title, Member sender, Member receiver) {
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

    // ChatRoomDTO 변환
    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        String userEmail = chatRoom.getMembers().stream()
                .filter(member -> member.getRole() == Role.USER)
                .findFirst()
                .map(Member::getEmail)
                .orElse("No user found");

        String userName = chatRoom.getMembers().stream()
                .filter(member -> member.getRole() == Role.USER)
                .findFirst()
                .map(Member::getName)
                .orElse("No user found");

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
}
