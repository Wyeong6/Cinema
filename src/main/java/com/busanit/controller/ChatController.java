package com.busanit.controller;

import com.busanit.domain.chat.*;
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
        // 다른 클라이언트에게 브로드캐스팅
        messagingTemplate.convertAndSend("/Topic/paging", pageUpdateDTO);
        System.out.println("브로드캐스팅 완료!");
    }

    //로그인 여부확인 후 페이지이동
    @GetMapping("/chatUser")
    public String chatUser(Model model) {
        String userEmail = chatService.getAuthenticatedUserEmail();
        model.addAttribute("userEmail", userEmail);

        return "/cs/chat";
    }

    //관리자 채팅방입장 알림
    @MessageMapping("/chat/admin/enter")
    public void sendAdminEnter(@Payload EnterNotificationDTO enterNotificationDTO) {


        System.out.println("채팅방나감 컨트롤");
        messagingTemplate.convertAndSendToUser(enterNotificationDTO.getRecipient(), "/queue/private/" + enterNotificationDTO.getChatRoomId(), enterNotificationDTO);
    }

        //메세지 처리
        @MessageMapping("/chat/private")
        public void sendPrivateMessage(@Payload MessageDTO messageDTO) {

            // 채팅 종료할 경우
            if ("inactive".equals(messageDTO.getStatus())) {
                chatService.updateChatRoomStatus(messageDTO.getChatRoomId(), "inactive");
            } else if ("active".equals(messageDTO.getStatus()) && "enter".equals(messageDTO.getType())) {
                // "active" 상태이면서 "enter" 타입인 경우
                chatService.saveMessage(messageDTO); // 메시지 저장
            chatService.updateLastReadTimestamp(messageDTO.getChatRoomId(), messageDTO.getRecipient()); // 마지막 읽은 시간 업데이트
                System.out.println("유저가 입장햇을때 ");

            } else if ("active".equals(messageDTO.getStatus())) {
                // "active" 상태인 경우
                chatService.saveMessage(messageDTO); // 메시지 저장
                System.out.println("유저가 입장안햇을때  ");
            }

            System.out.println("유저가 입장안했을때 메세지 보내기");
            // 메시지 처리 후, 채팅 리스트 업데이트 요청
            PageUpdateDTO pageUpdateDTO = messageDTO.getPaging();
            messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" + messageDTO.getChatRoomId(), messageDTO);
            updateChatList(messageDTO.getSender(), messageDTO.getRecipient(), pageUpdateDTO.getActivePage(), pageUpdateDTO.getInactivePage(), 8);

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
        return chatService.findChatRoomByChatRoomId(chatRoomId);
    }

    //모달창 열려있을 때 메세지 상태변경
    @PostMapping("/chat/updateLastReadTimestamp/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<Page<ChatRoomDTO>> updateLastReadTimestamp(@PathVariable Long chatRoomId) {

        chatService.updateLastReadTimestamp(chatRoomId);
        String userEmail = chatService.getAuthenticatedUserEmail();
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

    //메세지보낼 때 채팅리스트 업데이트
    public void updateChatList(String sender, String recipient, int activePage, int inactivePage, int size) {

        Page<ChatRoomDTO> activeChatRooms = chatService.getActiveChatList(activePage - 1, size, recipient);
        Map<String, Object> activeChatResponse = addPagingChatList("active", activeChatRooms, activePage, sender);
        // 비활성 채팅방 목록 가져오기
        Page<ChatRoomDTO> inactiveChatRooms = chatService.getInactiveChatList(inactivePage - 1, size, recipient);
        Map<String, Object> inactiveChatResponse = addPagingChatList("inactive", inactiveChatRooms, inactivePage, sender);

        // 두 개의 목록을 합쳐서 반환
        Map<String, Object> combinedResponse = new HashMap<>();
        combinedResponse.putAll(activeChatResponse);
        combinedResponse.putAll(inactiveChatResponse);

        // 메시지 전송 로그 추가
        System.out.println("Sending message to user: " + recipient);
        System.out.println("Message content: " + combinedResponse);

        // WebSocket 클라이언트에게 업데이트된 채팅 리스트 전송
        messagingTemplate.convertAndSendToUser(recipient, "/queue/chatList", combinedResponse);

    }
    //페이징
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
}