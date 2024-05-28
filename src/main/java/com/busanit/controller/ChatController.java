package com.busanit.controller;

import com.busanit.entity.chat.Message;
import com.busanit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    public void sendPrivateMessage(Message message) {
        messagingTemplate.convertAndSendToUser(message.getRecipient(), "/queue/private", message);
    }


}