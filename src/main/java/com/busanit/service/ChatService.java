package com.busanit.service;


import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import com.busanit.repository.ChatRoomRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    public void saveMessage(MessageDTO messageDTO) {
        Member sender = memberRepository.findByEmail(messageDTO.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender email: " + messageDTO.getSender()));
        Member receiver = memberRepository.findByEmail(messageDTO.getRecipient())
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipient email: " + messageDTO.getRecipient()));

        ChatRoom chatRoom = getOrCreateChatRoom(messageDTO.getMessageTitle() , messageDTO.getSender());

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setMessageTitle(messageDTO.getMessageTitle());
        message.setChatRoom(chatRoom); // Message에 ChatRoom 설정 (필요한 경우)

        sender.addSentMessage(message);
        receiver.addReceivedMessage(message);

        messageRepository.save(message);
    }

    public ChatRoom getOrCreateChatRoom(String messageTitle , String getSender) {
        // 주어진 email로 Member 찾기
        Member member = memberRepository.findByEmail(getSender)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        // 해당 Member가 이미 참여하고 있는 ChatRoom 찾기 (여기서는 단순화를 위해 첫 번째 참여한 방을 선택)
        Optional<ChatRoom> existingChatRoom = member.getChatRooms().stream().findFirst();

        if (existingChatRoom.isPresent()) {
            // 이미 존재하는 ChatRoom 반환
            return existingChatRoom.get();
        } else {
            // 새로운 ChatRoom 생성
            ChatRoom newChatRoom = ChatRoom.builder()
                    .title(messageTitle) // 채팅방 타이틀 설정
                    .members(new ArrayList<>())
                    .build();
            newChatRoom.addMembers(Arrays.asList(member)); // Member를 새 채팅방에 추가
            return chatRoomRepository.save(newChatRoom); // 채팅방 저장
        }
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
