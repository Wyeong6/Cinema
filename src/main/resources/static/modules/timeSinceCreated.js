// 시간 경과 표시 및 업데이트 함수
function updateTimeSinceCreated() {
    $('.time-since-created').each(function () {
        var createdAtText = $(this).closest('td').data('createdat').trim(); // 데이터 속성에서 텍스트 가져오기 (공백 제거)
        console.log("createdAtText" + createdAtText)
        var createdAt = new Date(Date.parse(createdAtText));

        if (isNaN(createdAt.getTime())) {
            // 유효하지 않은 날짜 형식인 경우 처리
            $(this).text("유효하지 않은 날짜");
            return;
        }

        var now = new Date(); // 현재 시간
        var elapsedTime = now - createdAt; // 경과 시간(밀리초 단위)

        // 밀리초를 시간으로 변환 (예: 시간 단위로 나누기)
        var seconds = Math.floor(elapsedTime / 1000);
        var minutes = Math.floor(seconds / 60);
        var hours = Math.floor(minutes / 60);
        var days = Math.floor(hours / 24);

        var timeSinceCreated;
        if (days > 0) {
            timeSinceCreated = days + "일 전";
        } else if (hours > 0) {
            timeSinceCreated = hours + "시간 전";
        } else if (minutes > 0) {
            timeSinceCreated = minutes + "분 전";
        } else {
            timeSinceCreated = seconds + "초 전";
        }

        $(this).text(timeSinceCreated);
    });
}

// 페이지 로딩 후 처음 실행
$(document).ready(function () {
    updateTimeSinceCreated();

    // 1분마다 시간 업데이트
    setInterval(updateTimeSinceCreated, 60000); // 1분(60초)마다 업데이트
});

// 함수를 외부에서 사용할 수 있도록 export
export { updateTimeSinceCreated };