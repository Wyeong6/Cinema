package com.busanit.controller;

import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.domain.chat.TypingIndicatorDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    //로그인 여부확인 후 페이지이동
    @GetMapping("/chatUser")
    public String chatUser(Model model) {
        if (chatService.isAuthenticated()) {
            String userEmail = chatService.getAuthenticatedUserEmail();
            model.addAttribute("userEmail", userEmail);
            return "client"; // templates 폴더의 client.html 파일을 렌더링
        } else {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    }

    //관리자에게 메세지 보내기
    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload MessageDTO messageDTO) {

        if ("endChat".equals(messageDTO.getChatRoomTitle())) { // 채팅 종료 메시지인 경우

        }
        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" + messageDTO.getSender(), messageDTO);


        chatService.saveMessage(messageDTO);

    }

//    @PostMapping("/chat/createChatRoom")
//    @ResponseBody
//    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
//
//        ChatRoom createdRoom = chatService.createChatRoom(chatRoomDTO.getChatRoomTitle(), chatRoomDTO.getUserEmail(), chatRoomDTO.getAdminEmail());
//
//        return ResponseEntity.ok().body("{\"message\": \"Chat room created successfully.\"}");    }

    //이전 메세지 내용가져오기
    @GetMapping("/chat/private/{recipient}")
    @ResponseBody
    public List<ChatRoomDTO> fetchMessages(@PathVariable String recipient) {
        System.out.println("이전메세지가져옴");
        return chatService.findChatRoomByUserEmail(recipient);
    }

    @GetMapping("/chat/getRecipientEmail")
    @ResponseBody
    public List<String> getRecipient() {
        return chatService.findCHatRoomByRecipient();
    }
    // 클라이언트로부터 전송된 타이핑 인디케이터 처리
    @MessageMapping("/chat/typing")
    public void handleTypingIndicator(@Payload TypingIndicatorDTO typingIndicatorDTO) {

        messagingTemplate.convertAndSendToUser(typingIndicatorDTO.getRecipient(), "/queue/private/" + typingIndicatorDTO.getSender(), typingIndicatorDTO);
    }
}