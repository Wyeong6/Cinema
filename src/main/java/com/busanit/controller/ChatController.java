package com.busanit.controller;

import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.chat.Message;
import com.busanit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat")
    public String chat(Model model) {
        if (chatService.isAuthenticated()) {
            String userEmail = chatService.getAuthenticatedUserEmail();
            model.addAttribute("userEmail", userEmail);
            return "WebSocket"; // templates 폴더의 WebSocket.html 파일을 렌더링
        } else {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    }

    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload MessageDTO messageDTO) {

        System.out.println("sendPrivateMessage() 메서드 호출됨");
        // 데이터가 잘 들어오는지 확인하기 위해 시스템 아웃 추가
        System.out.println("Received messageDTO: " + messageDTO);

        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private", messageDTO);

        // 메시지를 저장하기 전에 데이터 확인
        System.out.println("Saving message: " + messageDTO);
        chatService.saveMessage(messageDTO);

        // 채팅방을 생성하기 전에 데이터 확인
        System.out.println("Creating chat room with title: " + messageDTO.getMessageTitle());
        chatService.getOrCreateChatRoom(messageDTO.getMessageTitle(), messageDTO.getSender());


    }


}