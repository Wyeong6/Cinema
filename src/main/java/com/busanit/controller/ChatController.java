package com.busanit.controller;

import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.domain.chat.TypingIndicatorDTO;
import com.busanit.entity.Member;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.service.ChatService;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MovieService movieService;

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

        if ("inactive".equals(messageDTO.getStatus())) { // 채팅 종료할 경우
            chatService.updateChatRoomStatus(messageDTO.getChatRoomId(), "inactive");
        }else if("active".equals(messageDTO.getStatus())){
            chatService.saveMessage(messageDTO);
        }
        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" + messageDTO.getChatRoomId(), messageDTO);
    }
    //카테고리 선택 시 채팅룸 생성
    @PostMapping("/chat/createChatRoom")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {

        ChatRoom createChatRoom = chatService.getOrCreateChatRoom(chatRoomDTO.getChatRoomTitle(), chatRoomDTO.getUserEmail(), chatRoomDTO.getAdminEmail());

        Map<String, Long> response = new HashMap<>();
        response.put("chatRoomId", createChatRoom.getId());

        return ResponseEntity.ok().body(response);

    }

    //채팅중인 메세지 가져오기
    @GetMapping("/chat/active/{recipient}")
    @ResponseBody
    public List<ChatRoomDTO> getActiveChat(@PathVariable String recipient) {
        System.out.println("이전메세지가져옴");
        return chatService.findChatRoomByUserEmail(recipient);
    }

    //클릭한 채팅룸 메세지 가져오기
    @GetMapping("/chat/clickChat/{chatRoomId}")
    @ResponseBody
    public List<ChatRoomDTO> getClickChat(@PathVariable Long chatRoomId) {
        System.out.println("클릭한이전메세지가져옴");
        return chatService.findChatRoomByChatRoomId(chatRoomId);
    }

    @GetMapping("/chat/getRecipientEmail")
    @ResponseBody
    public List<String> getRecipient() {
        return chatService.findCHatRoomByRecipient();
    }
    //타이핑 인디케이터 처리
    @MessageMapping("/chat/typing")
    public void handleTypingIndicator(@Payload TypingIndicatorDTO typingIndicatorDTO) {

        messagingTemplate.convertAndSendToUser(typingIndicatorDTO.getRecipient(), "/queue/private/" + typingIndicatorDTO.getChatRoomId(), typingIndicatorDTO);
    }


    @MessageMapping("/chat/chatList") // 클라이언트에서 메시지 보낼 때 사용할 주제
    @SendTo("/queue/chatList") // 클라이언트가 구독할 주제
    public Map<String, Object> updateChatList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        System.out.println("리스트업데이트시작");
        String memberEmail = movieService.getUserEmail();
        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, memberEmail);

        int totalPages = chatRoom.getTotalPages();
        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(totalPages, page + 4);

        Map<String, Object> response = new HashMap<>();
        response.put("chatRoom", chatRoom.getContent());
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("memberEmail", memberEmail);

        // WebSocket 클라이언트에게 업데이트된 채팅 리스트 전송
//        messagingTemplate.convertAndSend("/queue/chatList", response);

        System.out.println("리스트업데이트돼라얍");
        return response;
    }

}