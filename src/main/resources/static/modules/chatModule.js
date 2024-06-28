import { updateTimeSinceCreated } from './timeSinceCreated.js';

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
        // loadInitialData = null,
        initialDataParams
    } = options;

    console.log("Connecting to WebSocket server...");
    const socket = new SockJS('/ws'); // WebSocket 엔드포인트 '/ws'로 연결
    const stompClient = Stomp.over(socket); // Stomp 클라이언트 객체 생성

    // 웹소켓 연결
    stompClient.connect({},function (frame) {
        console.log('Connected to WebSocket: ' + frame);
        console.log('Connected to WebSocket: ' + frame);
        // '/user/queue/chatList' 주제를 구독하여 메시지 처리
        stompClient.subscribe('/user/queue/chatList', function (message) {
            const response = JSON.parse(message.body);
            console.log("Received message:", response);

            // 클라이언트가 가지고 있는 페이징 번호를 사용하여 업데이트
            console.log("response.inactiveCurrentPage" + response.inactiveCurrentPage);

            // displayChatListCallback 함수를 호출하여 채팅 목록을 화면에 업데이트
            if (displayChatListCallback) {
                 displayChatListCallback(response);
            }
            // subscribeCallback 함수를 호출하여 구독된 메시지 처리
             subscribeCallback(response);
        });
        // loadInitialData가 함수인 경우 최초 데이터 로드 수행
        if (initialDataParams) {
             loadChatList(...initialDataParams); // 파라미터 전달하여 초기 데이터 로드
        }
    });

    return stompClient; // Stomp 클라이언트 객체 반환
}

// 멤버 이메일 변수 선언
export let adminEmail = '';
//채팅방 페이징변수 선언
export let activePage = 1;
export let inactivePage = 1;


//모달창 열려있을 때 메세지가 오면 해당 메세지 읽은 시간 업데이트 후 리스트반환
export function updateLastReadTimestamp(chatRoomId, activePage, inactivePage) {
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
            // loadChatList를 호출하고 그 결과 프로미스를 반환
            return loadChatList(activePage, inactivePage, 8, true);
        })
        .then(() => {
            // console.log("Chat list and unread count updated successfully.");
        })
        .catch(error => {
            console.error('Error updating last read timestamp:', error);
            throw error; // Promise 체인을 끊어 에러를 상위로 전파
        });
}


/**
 * 채팅 목록을 가져오는 함수
 * @param {number} page1 - 채팅중인 채팅방 페이지
 * @param {number} page2 - 채팅끝난 채팅방 페이지
 * @param {number} size - 페이지당 아이템 수
 * @param {boolean} isUpdateUnreadCountOnly - true면 읽지 않은 메시지 카운트만 업데이트, false면 채팅 목록을 표시하고 카운트 업데이트
 * @returns {Promise} - AJAX 요청의 Promise 객체 반환
 */
export function loadChatList(page1, page2, size, isUpdateUnreadCountOnly) {
    activePage = page1;
    inactivePage = page2;
    $.ajax({
        url: "/admin/getChatList",
        data: {
            activePage: activePage,
            inactivePage: inactivePage,
            size: size
        },
        type: "POST",
        contentType: "application/x-www-form-urlencoded",
        success: function (response) {
            adminEmail = response.activeMemberEmail || response.inactiveMemberEmail;
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
    var activeChatRoomList = response.activeChatRoom || [];
    var totalUnreadCount = activeChatRoomList.reduce((sum, activeChatRoom) => sum + activeChatRoom.unreadMessageCount, 0);
    var $unreadCount = $('#unreadCount');

    // console.log("activeChatRoomList" + activeChatRoomList)
    // console.log("totalUnreadCount" + totalUnreadCount)
    // 안 읽은 메시지 수 업데이트
    $unreadCount.text(totalUnreadCount);

    // 안 읽은 메시지가 있는 경우 표시
    if (totalUnreadCount > 0) {
        $unreadCount.show();
    } else {
        $unreadCount.hide();
    }
}

/**
 * 채팅 목록을 화면에 표시하는 함수
 * @param {object} response - 서버에서 받은 응답 객체
 */
export function displayChatList(response) {

    // HTML에서 요소를 찾음
    var $activeChatListContainer = $(".active-chat-table");
    var $inactiveChatListContainer = $(".inactive-chat-table");

    // 기존 목록을 비우고 새로운 목록 생성
    $activeChatListContainer.empty();
    $inactiveChatListContainer.empty();

    // 활성 채팅 목록을 표시
    displayRoomList(response.activeChatRoom || [], $activeChatListContainer, "#active-no-rooms");

    // 비활성 채팅 목록을 표시
    displayRoomList(response.inactiveChatRoom || [], $inactiveChatListContainer, "#inactive-no-rooms");

    // 경과시간 업데이트
    updateTimeSinceCreated();

    // 페이징 정보 업데이트
    updatePagination(response, 'active');
    updatePagination(response, 'inactive');
}

// 채팅 방 목록을 화면에 표시하는 함수
function displayRoomList(roomList, $container, noRoomsMessageId) {

    if (roomList.length === 0) {
        // 목록이 비어 있으면 "상담문의가 없습니다" 메시지 표시
        $(noRoomsMessageId).show();
    } else {
        // 목록이 비어 있지 않으면 "상담문의가 없습니다" 메시지 숨기기
        $(noRoomsMessageId).hide();


        roomList.forEach(function (chatRoom) {
            var lastMessageContent = "";
            var lastMessageCreatedAt = "";

            // 가장 최근 메시지의 내용을 가져옴
            if (chatRoom.messages && chatRoom.messages.length > 0) {
                var lastMessage = chatRoom.messages[chatRoom.messages.length - 1];
                if (lastMessage) {
                    lastMessageContent = lastMessage.content;
                    lastMessageCreatedAt = lastMessage.createAt;
                }
            }

            // 최대 길이 제한 설정
            var maxLength = 10;
            var truncatedContent = lastMessageContent.length > maxLength ? lastMessageContent.substring(0, maxLength) + '...' : lastMessageContent;

            // 채팅 목록 행 생성
            var chatRoomRow =
                `<tr data-room-id='${chatRoom.id}' onclick='openChatModal(this)'>
                <td>${chatRoom.id}</td>
                <td>${chatRoom.chatRoomTitle}(${chatRoom.type})</td>
                <td>${truncatedContent} (${chatRoom.unreadMessageCount}개 메시지)</td>
                <td>${chatRoom.userEmail}</td>
                <td>${chatRoom.userName}</td>
                <td data-createdAt='${lastMessageCreatedAt}'> <span class="time-since-created"></span></td>
            </tr>`;

            // 생성한 행을 채팅 목록에 추가
            $container.append(chatRoomRow);
        });
    }
}
//페이징 기능
    function updatePagination(response, type) {
        var $pagination = $("#" + type + "-pagination");
        $pagination.empty();

        var startPage = response[type + "StartPage"];
        var endPage = response[type + "EndPage"];

        var paginationHtml = '<div style="text-align: center;">';
        for (var i = startPage; i <= endPage; i++) {
            paginationHtml += '<a href="#" data-page="' + i + '" class="paging-btn" data-type="' + type + '">[' + i + ']</a>';
        }
        paginationHtml += '</div>';

        $pagination.html(paginationHtml);

        // 페이징 버튼 클릭 이벤트 처리
        $pagination.find(".paging-btn").on("click", function (event) {
            event.preventDefault();
            var page = parseInt($(this).data("page"), 10);
            var type = $(this).data("type");

            // 전역 변수 업데이트
            if (type === 'active') {
                activePage = page;
            } else {
                inactivePage = page;
            }

            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {

                var paging = {
                    activePage: activePage,
                    inactivePage: inactivePage
                };

                // 현재 페이지 정보를 서버에 전송
                stompClient.send("/app/chat/updatePage", {}, JSON.stringify(paging));

                // WebSocket 연결 해제
                stompClient.disconnect(function () {
                    console.log('웹소켓 연결 해제');
                });

                loadChatList(activePage, inactivePage, 8, false);
            });
        });
    }

// 전역 객체에 loadChatList 함수 추가
    window.chatUtils = {
        loadChatList: loadChatList,
        updateLastReadTimestamp: updateLastReadTimestamp,
        updateUnreadCount: updateUnreadCount,
        get activePage() {
            return activePage;
        },
        get inactivePage() {
            return inactivePage;
        }
    };

