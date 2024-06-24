package com.busanit.controller;

import com.busanit.domain.chat.ChatRoomDTO;
import com.busanit.domain.chat.MessageDTO;
import com.busanit.domain.chat.PageUpdateDTO;
import com.busanit.domain.chat.TypingIndicatorDTO;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.util.WebUtils.getSessionAttribute;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"activePage", "inactivePage"})
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @ModelAttribute("activePage")
    public int activePage() {
        return 1; // 기본 active 페이지 번호
    }

    @ModelAttribute("inactivePage")
    public int inactivePage() {
        return 1; // 기본 inactive 페이지 번호
    }

    @MessageMapping("/chat/updatePage")
    public void updatePage(@Payload PageUpdateDTO pageUpdateDTO) {
        // 클라이언트로부터 받은 페이징 정보 처리
        System.out.println("Received paging data: " + pageUpdateDTO);

        // 여기서 필요한 로직 수행 (예: 페이징 정보를 데이터베이스에 저장하거나, 다른 클라이언트에게 브로드캐스팅)

        // 다른 클라이언트에게 브로드캐스팅
        messagingTemplate.convertAndSend("/Topic/paging", pageUpdateDTO);
        System.out.println("브로드캐스팅 완료!");
    }


    //로그인 여부확인 후 페이지이동
    @GetMapping("/chatUser")
    public String chatUser(Model model) {
//        if (chatService.isAuthenticated()) {
        String userEmail = chatService.getAuthenticatedUserEmail();
        model.addAttribute("userEmail", userEmail);
        return "/cs/chat"; // templates 폴더의 chat.html 파일을 렌더링
//        } else {
//            return "redirect:/login"; // 로그인 페이지로 리다이렉트
//        }
    }

    //메세지 처리
    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload MessageDTO messageDTO) {

        // 채팅 종료할 경우
        if ("inactive".equals(messageDTO.getStatus())) {
            chatService.updateChatRoomStatus(messageDTO.getChatRoomId(), "inactive");
        }else if("active".equals(messageDTO.getStatus())){
            chatService.saveMessage(messageDTO);
        }


        // 메시지 처리 후, 채팅 리스트 업데이트 요청
        PageUpdateDTO pageUpdateDTO = messageDTO.getPaging();
        updateChatList(messageDTO.getSender(), messageDTO.getRecipient(), pageUpdateDTO.getActivePage(), pageUpdateDTO.getInactivePage(), 8);

        messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" + messageDTO.getChatRoomId(), messageDTO);
    }
    //카테고리 선택 시 채팅룸 생성
    @PostMapping("/chat/createChatRoom")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {

        ChatRoom createChatRoom = chatService.handleUserMessage(chatRoomDTO.getChatRoomTitle(), chatRoomDTO.getUserEmail(), chatRoomDTO.getAdminEmail());

        Map<String, Long> response = new HashMap<>();
        response.put("chatRoomId", createChatRoom.getId());

        return ResponseEntity.ok().body(response);
    }

    //채팅중인 메세지 반환
    @GetMapping("/chat/active/{recipient}")
    @ResponseBody
    public List<ChatRoomDTO> getActiveChat(@PathVariable String recipient) {
        System.out.println("이전메세지가져옴");
        return chatService.findChatRoomByUserEmail(recipient);
    }

    //클릭한 채팅룸 메세지 반환
    @GetMapping("/chat/clickChat/{chatRoomId}")
    @ResponseBody
    public List<ChatRoomDTO> getClickChat(@PathVariable Long chatRoomId) {
        System.out.println("클릭한이전메세지가져옴");
        return chatService.findChatRoomByChatRoomId(chatRoomId);
    }

    @PostMapping("/chat/updateLastReadTimestamp/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<Page<ChatRoomDTO>> updateLastReadTimestamp(@PathVariable Long chatRoomId) {

        System.out.println("오픈모달창중 읽기");
        chatService.updateLastReadTimestamp(chatRoomId);

        String userEmail = chatService.getAuthenticatedUserEmail();

        // 전체 채팅방 정보 가져오기
        Page<ChatRoomDTO> chatRooms = chatService.getChatList(1, 1, userEmail); // 이 메서드는 전체 채팅방 정보를 가져오는 것으로 가정합니다.

        return ResponseEntity.ok(chatRooms);
    }

    //로그인한 유저와 채팅중인 상대방이메일 반환
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

    public void updateChatList(String sender, String recipient, int activePage, int inactivePage, int size) {

        Page<ChatRoomDTO> activeChatRooms = chatService.getActiveChatList(activePage - 1, size, recipient);
        Map<String, Object> activeChatResponse = addPagingChatList("active", activeChatRooms, activePage, sender);
        // 비활성 채팅방 목록 가져오기
        Page<ChatRoomDTO> inactiveChatRooms = chatService.getInactiveChatList(inactivePage - 1, size, recipient);
        Map<String, Object> inactiveChatResponse = addPagingChatList("inactive", inactiveChatRooms, inactivePage, sender);

//        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, recipient);
        // 두 개의 목록을 합쳐서 반환
        Map<String, Object> combinedResponse = new HashMap<>();
        combinedResponse.putAll(activeChatResponse);
        combinedResponse.putAll(inactiveChatResponse);

        // WebSocket 클라이언트에게 업데이트된 채팅 리스트 전송
        messagingTemplate.convertAndSendToUser(recipient, "/queue/chatList", combinedResponse);

    }

    private Map<String, Object> addPagingChatList(String type, Page<ChatRoomDTO> chatRooms, int page, String memberEmail) {
        int totalPages = chatRooms.getTotalPages();
        int startPage = Math.max(1, page - 5);
        int endPage = Math.min(totalPages, page + 4);

        Map<String, Object> response = new HashMap<>();
        response.put(type + "ChatRoom", chatRooms.getContent());
        response.put(type + "CurrentPage", page);
        response.put(type + "TotalPages", totalPages);
        response.put(type + "StartPage", startPage);
        response.put(type + "EndPage", endPage);
        response.put(type + "MemberEmail", memberEmail);

        return response;
    }

//    public void updateChatList(String sender, String recipient, int page, int size) {
//
//
//        Page<ChatRoomDTO> chatRoom = chatService.getChatList(page - 1, size, recipient);
//
//        int totalPages = chatRoom.getTotalPages();
//        int startPage = Math.max(1, page - 5);
//        int endPage = Math.min(totalPages, page + 4);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatRoom", chatRoom.getContent());
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("startPage", startPage);
//        response.put("endPage", endPage);
//        response.put("memberEmail", sender);
//
//        // WebSocket 클라이언트에게 업데이트된 채팅 리스트 전송
//        messagingTemplate.convertAndSendToUser(recipient, "/queue/chatList", response);
//
//    }

}