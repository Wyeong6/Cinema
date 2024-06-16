/**
 * 웹소켓 연결 함수
 * @param {Object} options - 구독 콜백 및 초기 데이터 로드 옵션
 *                           {
 *                               subscribeCallback: Function, // 웹소켓 메시지 구독 시 호출할 콜백 함수
 *                               displayChatListCallback: Function, // 채팅 목록을 화면에 표시할 콜백 함수
 *                               loadInitialData: Function, // 최초 데이터 로드를 위한 함수
 *                               initialDataParams: Array // loadInitialData 함수에 전달할 파라미터 배열
 *                           }
 * @returns {Stomp.Client} - Stomp 클라이언트 객체 반환
 */
export function connectWebSocket(options) {
    const {
        subscribeCallback,
        displayChatListCallback,
        loadInitialData = null,
        initialDataParams
    } = options;

    console.log("Connecting to WebSocket server...");
    const socket = new SockJS('/ws'); // WebSocket 엔드포인트 '/ws'로 연결
    const stompClient = Stomp.over(socket); // Stomp 클라이언트 객체 생성

    // 웹소켓 연결
    stompClient.connect({}, async function (frame) {
        console.log('Connected to WebSocket: ' + frame);

        // '/user/queue/chatList' 주제를 구독하여 메시지 처리
        stompClient.subscribe('/user/queue/chatList', function (message) {
            const response = JSON.parse(message.body);
            console.log("Received message:", response);

            // displayChatListCallback 함수를 호출하여 채팅 목록 화면에 업데이트
            if (displayChatListCallback) {
                displayChatListCallback(response);
            }

            // subscribeCallback 함수를 호출하여 구독된 메시지 처리
            subscribeCallback(response);
        });

        // loadInitialData가 함수인 경우 최초 데이터 로드 수행
        if (loadInitialData) {
            if (initialDataParams) {
                await loadChatList(...initialDataParams); // 파라미터 전달하여 초기 데이터 로드
            } else {
                await loadChatList(); // 파라미터 없이 초기 데이터 로드
            }
        }
    });

    return stompClient; // Stomp 클라이언트 객체 반환
}


export function updateLastReadTimestamp(chatRoomId) {
    return fetch(`/chat/updateLastReadTimestamp/${chatRoomId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            console.log("Last read timestamp updated successfully.");
        })
        .catch(error => {
            console.error('Error updating last read timestamp:', error);
            throw error; // Promise 체인을 끊어 에러를 상위로 전파
        });
}

// 멤버 이메일 변수 선언
export let adminEmail = '';

/**
 * 채팅 목록을 가져오는 함수
 * @param {number} page - 페이지 번호
 * @param {number} size - 페이지당 아이템 수
 * @param {boolean} isUpdateUnreadCountOnly - true면 읽지 않은 메시지 카운트만 업데이트, false면 채팅 목록을 표시하고 카운트 업데이트
 * @returns {Promise} - AJAX 요청의 Promise 객체 반환
 */
export function loadChatList(page, size, isUpdateUnreadCountOnly) {

    console.log("모달창닫은 후 loadChatList");
    $.ajax({
        url: "/admin/getChatList",
        data: {
            page: page,
            size: size,
        },
        type: "POST",
        contentType: "application/json",
        success: function (response) {
            console.log("응답 데이터:", response);
            adminEmail = response.memberEmail;
            console.log("응답 memberEmail:", adminEmail);


            if (isUpdateUnreadCountOnly) {
                updateUnreadCount(response);
            } else {
                updateUnreadCount(response);
                displayChatList(response);
            }
        },
        error: function (error) {
            console.log("Error: ", error);
        }
    });
}

/**
 * 읽지 않은 메시지 카운트를 업데이트하는 함수
 * @param {object} response - 서버에서 받은 응답 객체
 */
export function updateUnreadCount(response) {
    var totalUnreadCount = response.chatRoom.reduce((sum, chatRoom) => sum + chatRoom.unreadMessageCount, 0);
    var unreadMessagesDiv = document.getElementById('unreadMessages');
    var unreadCountSpan = document.getElementById('unreadCount');

    // 안 읽은 메시지 수 업데이트
    unreadCountSpan.textContent = totalUnreadCount;

    // 안 읽은 메시지가 있는 경우 표시
    if (totalUnreadCount > 0) {
        unreadMessagesDiv.style.display = 'block';
    } else {
        unreadMessagesDiv.style.display = 'none';
    }

}


/**
 * 채팅 목록을 화면에 표시하는 함수
 * @param {object} response - 서버에서 받은 응답 객체
 */
export function displayChatList(response) {
    console.log("모달창닫은 후 displayChatList");
    var chatList = response.chatRoom;
    var $chatListContainer = $(".chat-table");

    // 기존 목록을 비우고 새로운 목록 생성
    $chatListContainer.empty();

    chatList.forEach(function (chatRoom) {
        var lastMessageContent = "";

        // 가장 최근 메시지의 내용을 가져옴
        if (chatRoom.messages && chatRoom.messages.length > 0) {
            var lastMessage = chatRoom.messages[chatRoom.messages.length - 1];
            if (lastMessage) {
                lastMessageContent = lastMessage.content;
            }
        }

        var chatRoomRow =
            "<tr data-room-id='" + chatRoom.id + "' onclick='openChatModal(this)'>" +
            "<td>" + chatRoom.id + "</td>" +
            "<td>" + chatRoom.chatRoomTitle + "(" + chatRoom.type + ")" + "</td>" +
            "<td>" + lastMessageContent + " (" + chatRoom.unreadMessageCount + "개 메시지)" + "</td>" +
            "<td>" + chatRoom.userEmail + "</td>" +
            "<td>" + chatRoom.userName + "</td>" +
            "</tr>";
        $chatListContainer.append(chatRoomRow);
    });

    // 페이징 정보 업데이트
    var $pagination = $("#pagination");
    $pagination.empty();

    var paginationHtml = '<div style="text-align: center;">';
    for (var i = response.startPage; i <= response.endPage; i++) {
        paginationHtml += '<a href="#" data-page="' + i + '" class="paging-btn">[' + i + ']</a>';
    }
    paginationHtml += '</div>';

    $pagination.html(paginationHtml);
}


// 전역 객체에 loadChatList 함수 추가
window.chatUtils = {
    loadChatList: loadChatList,
    updateLastReadTimestamp: updateLastReadTimestamp
};
