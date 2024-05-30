package com.busanit.service;


import com.busanit.constant.Role;
import com.busanit.domain.EventDTO;
import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import com.busanit.entity.chat.QChatRoom;
import com.busanit.repository.ChatRoomRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

import static com.busanit.domain.chat.ChatRoomDTO.toChatRoomDTO;
import static com.busanit.domain.chat.MessageDTO.toMessageDTO;


@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    // 메시지를 저장하는 메소드
    public void saveMessage(MessageDTO messageDTO) {
        // 발신자 이메일로 회원을 찾음
        Member sender = memberRepository.findByEmail(messageDTO.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender email: " + messageDTO.getSender()));

        // 수신자 이메일로 회원을 찾음
        Member receiver = memberRepository.findByEmail(messageDTO.getRecipient())
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipient email: " + messageDTO.getRecipient()));

        // 메시지 제목, 발신자 이메일, 수신자 이메일을 기반으로 채팅방을 가져오거나 생성
        ChatRoom chatRoom = getOrCreateChatRoom(messageDTO.getMessageTitle(), messageDTO.getSender(), messageDTO.getRecipient());

        // 새로운 Message 객체 생성 및 설정
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setMessageTitle(messageDTO.getMessageTitle());
        message.setChatRoom(chatRoom);

        // 발신자와 수신자의 메시지 목록에 메시지 추가
        sender.addSentMessage(message);
        receiver.addReceivedMessage(message);
        chatRoom.addMessage(message);

        // 메시지를 저장소에 저장
        messageRepository.save(message);
    }

    // 채팅방 목록을 페이징하여 가져오는 메소드
    public Page<ChatRoomDTO> getChatList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAll(pageable);

        List<ChatRoomDTO> chatRoomList = chatRoomPage.getContent().stream()
                .map(this::convertToChatRoomDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(chatRoomList, pageable, chatRoomPage.getTotalElements());
    }

    // ChatRoom 엔티티를 ChatRoomDTO로 변환하는 메소드
    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
        Optional<Member> userMemberOpt = chatRoom.getMembers().stream()
                .filter(member -> member.getRole() == Role.USER)
                .findFirst();

        String userEmail = userMemberOpt.map(Member::getEmail).orElse("유저가 존재하지 않습니다.");
        String userName = userMemberOpt.map(Member::getName).orElse("유저가 존재하지 않습니다.");

        return toChatRoomDTO(chatRoom, userEmail, userName);
    }

    // 새로운 채팅방을 생성하거나 기존 채팅방을 가져오는 메소드
    public ChatRoom getOrCreateChatRoom(String messageTitle, String senderEmail, String recipientEmail) {
        if ("admin@admin.com".equals(senderEmail)) {
            return handleAdminMessage(recipientEmail);
        } else {
            return handleUserMessage(messageTitle, senderEmail);
        }
    }

    // 관리자 메시지를 처리하는 메소드, 수신자의 첫 번째 채팅방을 반환
    private ChatRoom handleAdminMessage(String recipientEmail) {
        Member recipient = findMemberByEmail(recipientEmail);
        return recipient.getChatRooms().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
    }

    // 사용자 메시지를 처리하는 메소드, 새 채팅방을 생성하거나 기존 채팅방 반환
    private ChatRoom handleUserMessage(String messageTitle, String senderEmail) {
        Member sender = findMemberByEmail(senderEmail);

        return sender.getChatRooms().stream().findFirst()
                .orElseGet(() -> createNewChatRoom(messageTitle, sender));
    }

    // 이메일로 회원을 찾는 메소드
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(email + "에 해당하는 사용자를 찾을 수 없습니다."));
    }

    // 새로운 채팅방을 생성하는 메소드
    private ChatRoom createNewChatRoom(String title, Member sender) {
        ChatRoom newChatRoom = ChatRoom.builder()
                .title(title)
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .build();
        newChatRoom.addMembers(Collections.singletonList(sender));
        return chatRoomRepository.save(newChatRoom);
    }

    // 사용자 이메일을 기반으로 채팅방을 찾는 메소드
    public List<ChatRoomDTO> findChatRoomByUserEmail(String userEmail) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMembersEmail(userEmail);

        return chatRooms.stream()
                .map(chatRoom -> convertToChatRoomDTOWithMessages(chatRoom, userEmail))
                .collect(Collectors.toList());
    }

    // 채팅방과 메시지를 포함한 ChatRoomDTO를 생성하는 메소드
    private ChatRoomDTO convertToChatRoomDTOWithMessages(ChatRoom chatRoom, String userEmail) {
        List<Message> messages = messageRepository.findByChatRoomId(chatRoom.getId());

        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::toMessageDTO)
                .collect(Collectors.toList());

        String userName = memberRepository.findNameByEmail(userEmail).orElse("이름 없음");

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

    //메세지 확인 여부
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID: " + messageId));
        message.setRead(true);
        messageRepository.save(message);
    }
}
