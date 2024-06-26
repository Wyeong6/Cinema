


// 미답변 문의 갯수를 가져와서 표시하는 함수
export function fetchUnansweredInquiryCount() {
    $.ajax({
        type: "GET",
        url: "/admin/unansweredInquiryCount",
        success: function(response) {
            console.log("Response received:", response);
            var count = response; // 서버에서 받은 미답변 문의 갯수
            console.log("count:", count);
            updateUnansweredCount(count); // 미답변 문의 갯수 업데이트 함수 호출
        },
        error: function(xhr, status, error) {
            console.error("AJAX 요청 실패:", error);
        }
    });
}

// 미답변 문의 갯수를 업데이트하는 함수
export function updateUnansweredCount(count) {
    var formattedCount = '(' + count + ')';
    $('#activeCount').text(formattedCount);
}