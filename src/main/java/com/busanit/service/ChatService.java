package com.busanit.service;

import com.busanit.constant.Role;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
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

    // 메시지 저장
    public void saveMessage(MessageDTO messageDTO) {
        Member sender = findMemberByEmail(messageDTO.getSender());
        Member receiver = findMemberByEmail(messageDTO.getRecipient());
        ChatRoom chatRoom = getOrCreateChatRoom(messageDTO.getMessageTitle(), messageDTO.getSender(), messageDTO.getRecipient());

        Message message = createMessage(sender, receiver, messageDTO, chatRoom);
        updateEntitiesWithNewMessage(sender, receiver, chatRoom, message);

        messageRepository.save(message);
    }

    // 채팅방 리스트 페이징 조회
    public Page<ChatRoomDTO> getChatList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAll(pageable);

        List<ChatRoomDTO> chatRoomList = chatRoomPage.getContent().stream()
                .map(this::convertToChatRoomDTO)
                .peek(chatRoomDTO -> { // map 대신 peek을 사용하여 DTO 변환 후 처리
                    int unreadMessages = calculateUnreadMessages(chatRoomDTO.getId(), chatRoomDTO.getLastReadTimestamp());
                    chatRoomDTO.setUnreadMessageCount(unreadMessages);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(chatRoomList, pageable, chatRoomPage.getTotalElements());
    }

    // 사용자 이메일로 채팅방 메세지 조회
    public List<ChatRoomDTO> findChatRoomByUserEmail(String userEmail) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersEmail(userEmail);
        return chatRooms.stream()
                .map(chatRoom -> {
                    // 해당 채팅방의 메시지를 가져오면서 읽은 시간을 업데이트
                    updateLastReadTimestamp(chatRoom.getId());
                    return convertToChatRoomDTOWithMessages(chatRoom, userEmail);
                })
                .collect(Collectors.toList());
    }

    // 채팅방 생성 또는 조회
    public ChatRoom getOrCreateChatRoom(String messageTitle, String senderEmail, String recipientEmail) {
        if ("admin@admin.com".equals(senderEmail)) {
            return handleAdminMessage(recipientEmail);
        } else {
            return handleUserMessage(messageTitle, senderEmail);
        }
    }

    // 이메일로 회원 조회
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email: " + email));
    }

    // 관리자 메시지 처리
    private ChatRoom handleAdminMessage(String recipientEmail) {
        Member recipient = findMemberByEmail(recipientEmail);
        return recipient.getChatRooms().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No chat room found."));
    }

    // 사용자 메시지 처리
    private ChatRoom handleUserMessage(String messageTitle, String senderEmail) {
        Member sender = findMemberByEmail(senderEmail);
        return sender.getChatRooms().stream().findFirst()
                .orElseGet(() -> createNewChatRoom(messageTitle, sender));
    }

    // 새 채팅방 생성
    private ChatRoom createNewChatRoom(String title, Member sender) {
        ChatRoom newChatRoom = ChatRoom.builder()
                .title(title)
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .build();
        newChatRoom.addMembers(Collections.singletonList(sender));
        return chatRoomRepository.save(newChatRoom);
    }

    // 메시지 생성
    private Message createMessage(Member sender, Member receiver, MessageDTO messageDTO, ChatRoom chatRoom) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setMessageTitle(messageDTO.getMessageTitle());
        message.setChatRoom(chatRoom);
        return message;
    }

    // 새 메시지로 엔티티 업데이트
    private void updateEntitiesWithNewMessage(Member sender, Member receiver, ChatRoom chatRoom, Message message) {
        sender.addSentMessage(message);
        receiver.addReceivedMessage(message);
        chatRoom.addMessage(message);
    }

    // 읽지 않은 메시지 계산
    private int calculateUnreadMessages(Long chatRoomId, LocalDateTime lastReadTimestamp) {
        if (lastReadTimestamp == null) {
            // lastReadTimestamp가 null일 경우, 채팅방의 전체 메시지 수를 반환
            return messageRepository.countByChatRoomId(chatRoomId);
        } else {
            // lastReadTimestamp 이후의 메시지 수를 계산하여 반환
            return messageRepository.countByChatRoomIdAndRegDateAfter(chatRoomId, lastReadTimestamp);
        }
    }

    public void updateLastReadTimestamp(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found with id " + chatRoomId));
        chatRoom.setLastReadTimestamp(LocalDateTime.now());
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
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return null;
    }

}
