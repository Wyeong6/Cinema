package com.busanit.controller;

import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.entity.chat.Message;
import com.busanit.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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
        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" +messageDTO.getSender() , messageDTO);

        chatService.saveMessage(messageDTO);

    }
    //관리자 챗메세지
    @GetMapping("/chatAdmin")
    public String chatAdmin(Model model) {
            String adminEmail = chatService.getAuthenticatedUserEmail();
            model.addAttribute("adminEmail", adminEmail);

            return "service";
    }

    //유저에게 메세지 보내기
    @MessageMapping("/chat/admin")
    public void sendAdminMessage(@Payload MessageDTO messageDTO) {
        chatService.saveMessage(messageDTO);

        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private", messageDTO);
    }

    //이전 메세지 내용가져오기
    @GetMapping("/chat/private/{userEmail}")
    @ResponseBody
    public List<ChatRoomDTO> fetchMessages(@PathVariable String userEmail) {

        return chatService.findChatRoomByUserEmail(userEmail);
    }

}