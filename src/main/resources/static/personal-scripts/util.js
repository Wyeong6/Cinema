// 로그인이 필요한 서비스일때
function requestLogin() {
    Swal.fire({
        title: "로그인이 필요한 서비스 입니다.",
        icon: "warning",
    }).then((result) => {
        if(result.isConfirmed) {
        }
    });
}

// alert 꾸미기
function swtAlertOne(swtTitle) {
    Swal.fire({
        title: swtTitle,
        icon: "warning",
    }).then((result) => {
        if(result.isConfirmed) {
        }
    });
}

function swtAlertTwo(swtTitle, swtText, swtConfirmButtonText, swtHref) {
    Swal.fire({
        title: swtTitle,
        text: swtText,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: swtConfirmButtonText,
        cancelButtonText: "아니오"
    }).then((result) => {
        if(result.isConfirmed) {
            window.location.href = swtHref; // 장바구니 URL("/payment/cartList")로 이동
        }
    });
}