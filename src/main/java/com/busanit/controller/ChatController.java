package com.busanit.controller;

import com.busanit.domain.chat.*;
import com.busanit.entity.chat.ChatRoom;
import com.busanit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"activePage", "inactivePage"})
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentMap<String, ConcurrentMap<String, Boolean>> chatRoomPresenceMap = new ConcurrentHashMap<>();

    @MessageMapping("/chat/updatePage")
    public void updatePage(@Payload PageUpdateDTO pageUpdateDTO) {

        // 다른 클라이언트에게 브로드캐스팅
        messagingTemplate.convertAndSend("/Topic/paging", pageUpdateDTO);
    }

    //로그인 여부확인 후 페이지이동
    @GetMapping("/chatUser")
    public String chatUser(Model model) {
        String userEmail = chatService.getAuthenticatedUserEmail();
        model.addAttribute("userEmail", userEmail);

        return "/cs/chat";
    }
    //채팅방 입장상태 반환
    @GetMapping("/chatRoomPresence")
    public ResponseEntity<Map<String, Boolean>> getAdminPresence(@RequestParam String chatRoomId) {
        Map<String, Boolean> userMap = chatRoomPresenceMap.getOrDefault(chatRoomId, new ConcurrentHashMap<>());
        return ResponseEntity.ok(userMap);
    }

    //채팅방 입장시
    @MessageMapping("/chat/admin/enter")
    public void sendAdminEnter(@Payload EnterNotificationDTO enterNotificationDTO) {
        chatRoomPresenceMap
                .computeIfAbsent(enterNotificationDTO.getChatRoomId(), k -> new ConcurrentHashMap<>())
                .put(enterNotificationDTO.getSender(), true);

        messagingTemplate.convertAndSendToUser(
                enterNotificationDTO.getRecipient(),
                "/queue/private/" + enterNotificationDTO.getChatRoomId(),
                enterNotificationDTO
        );
    }

    //채팅방 퇴장시
    @MessageMapping("/chat/admin/exit")
    public void sendAdminExit(@Payload EnterNotificationDTO enterNotificationDTO) {
        chatRoomPresenceMap
                .computeIfPresent(enterNotificationDTO.getChatRoomId(), (k, v) -> {
                    v.put(enterNotificationDTO.getSender(), false);
                    return v;
                });

        messagingTemplate.convertAndSendToUser(
                enterNotificationDTO.getRecipient(),
                "/queue/private/" + enterNotificationDTO.getChatRoomId(),
                enterNotificationDTO
        );
    }
        //메세지 처리
        @MessageMapping("/chat/private")
        public void sendPrivateMessage(@Payload MessageDTO messageDTO) {

            // inactive 상태일 때
            if ("inactive".equals(messageDTO.getStatus())) {
                chatService.updateChatRoomStatus(messageDTO.getChatRoomId(), "inactive");
            } else if ("true".equals(messageDTO.getType())) {// 채팅방 입장한 경우
                chatService.saveMessage(messageDTO);
                chatService.updateLastReadTimestamp(messageDTO.getChatRoomId(), messageDTO.getRecipient()); // 마지막 읽은 시간 업데이트
            } else { // active 상태일 때
                chatService.saveMessage(messageDTO);
            }

            messagingTemplate.convertAndSendToUser(messageDTO.getRecipient(), "/queue/private/" + messageDTO.getChatRoomId(), messageDTO);

            // 메시지 처리 후, 채팅 리스트 업데이트 요청
            PageUpdateDTO pageUpdateDTO = messageDTO.getPaging();
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

        //활성화 채팅방 목록 가져오기
        Page<ChatRoomDTO> activeChatRooms = chatService.getActiveChatList(activePage - 1, size, recipient);
        Map<String, Object> activeChatResponse = addPagingChatList("active", activeChatRooms, activePage, sender);
        // 비활성 채팅방 목록 가져오기
        Page<ChatRoomDTO> inactiveChatRooms = chatService.getInactiveChatList(inactivePage - 1, size, recipient);
        Map<String, Object> inactiveChatResponse = addPagingChatList("inactive", inactiveChatRooms, inactivePage, sender);

        // 두 개의 목록을 합쳐서 반환
        Map<String, Object> combinedResponse = new HashMap<>();
        combinedResponse.putAll(activeChatResponse);
        combinedResponse.putAll(inactiveChatResponse);

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