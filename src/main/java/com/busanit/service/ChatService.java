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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.busanit.domain.chat.ChatRoomDTO.toDTO;

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

    public Page<ChatRoomDTO> getChatList(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAll(pageable);

        List<ChatRoomDTO> chatRoomList = chatRoomPage.getContent().stream()
                .map(chatRoom -> {
                    // 유저 역할을 가진 멤버를 한 번에 찾습니다.
                    Optional<Member> userMemberOpt = chatRoom.getMembers().stream()
                            .filter(member -> member.getRole() == Role.USER)
                            .findFirst();

                    // 찾은 멤버에서 이메일과 이름을 가져옵니다. 멤버가 없다면 기본값을 사용합니다.
                    String userEmail = userMemberOpt.map(Member::getEmail).orElse("유저가 존재하지 않습니다.");
                    String userName = userMemberOpt.map(Member::getName).orElse("유저가 존재하지 않습니다.");

                    // ChatRoomDTO를 생성합니다.
                    return toDTO(chatRoom, userEmail, userName);
                })
                .collect(Collectors.toList());
        return new PageImpl<>(chatRoomList, pageable, chatRoomPage.getTotalElements());
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

    //이전 메세지 내역 가져오기
    public List<ChatRoomDTO> findMessagesByUserEmail(String userEmail) {
        // 데이터베이스에서 사용자 이메일에 해당하는 ChatRoom 엔티티를 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserEmail(userEmail);
        String userName = memberRepository.findNameByEmail(userEmail).orElse("이름 없음");


        // 조회된 ChatRoom 엔티티를 ChatRoomDTO로 변환
        List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream()
                .map(chatRoom -> toDTO(chatRoom, userEmail, userName)) // "사용자 이름"은 실제 사용자 이름을 가져오는 로직으로 대체해야 함
                .collect(Collectors.toList());

        return chatRoomDTOs;
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
