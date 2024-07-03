export function deleteConfirm() {
    return new Promise((resolve) => {
        Swal.fire({
            title: "삭제 하시겠습니까?",
            text: "삭제 하면 되돌릴수 없습니다!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "삭제",
            cancelButtonText: "취소"
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire({
                    title: "삭제되었습니다",
                    text: "정상적으로 삭제되었습니다!",
                    icon: "success"
                });
                resolve(true); // 삭제 확인되었음을 resolve 합니다.
            } else {
                resolve(false); // 삭제소 취되었음을 resolve 합니다.
            }
        });
    });
}


export function SuccessAlert(title) {
    Swal.fire({
        position: "center",
        icon: "success",
        title: title,
        showConfirmButton: false,
        timer: 1500
    });
}


export function errorAlert(title) {
    Swal.fire({
        position: "center",
        icon: "error",
        title: title,
        showConfirmButton: false,
        timer: 1500
    });
}
export function warningAlert(title) {
    Swal.fire({
        position: "center",
        icon: "error",
        title: title,
        showConfirmButton: false,
        timer: 1500
    });
}


// 기존 script에서 사용하기위해 전역변수로 등록
// 하지만 이러면 module화를 하는 의미가 퇴색됨,
// admin_movie_register에 사용하기위해 어쩔수없이 전역화 함.
window.SuccessAlert = SuccessAlert;
window.deleteConfirm = deleteConfirm;
window.errorAlert = errorAlert;
window.warningAlert = warningAlert;
