function requestPay() {
    $.ajax({
        type: 'POST',
        url: '/payment/request',
        data: JSON.stringify({
            orderName: orderName,
            currentPrice: currentPrice,
            reqIDX: reqIDX
        }),
        contentType: 'application/json',
        success: function(response) {
            IMP.init(response.html5InicisKey); // 고객사 식별코드
            IMP.request_pay({
                pg: "html5_inicis", //"{PG사 코드}.{상점 ID}",
                pay_method: "card",
                merchant_uid: response.reqIDX + `-${crypto.randomUUID()}`, // 상점에서 생성한 주문 고유 번호
                name: response.orderName, // 주문명
                amount: response.currentPrice, // 결제 금액
                buyer_email: response.memberEmail,
                buyer_name: response.memberName,
                m_redirect_url: '/payment/complete' // 모바일이나 태블릿은 m_redirect_url 가 없으면 에러나는 경우가 있다고 함
            }, function(rsp) {
                if(rsp.success) {
                    $.ajax({
                        type: "POST",
                        url: "/payment/complete",
                        data: $.param({
                            "merchant_uid": rsp.merchant_uid,
                            "imp_uid": rsp.imp_uid,
                            "apply_num": rsp.apply_num,
                            "buyer_email": rsp.buyer_email,
                            "payment_status": rsp.payment_status,
                            "product_idx": productIdx,
                            "product_name": orderName,
                            "product_type": reqIDX,
                            "content1": content1,
                            "content2": content2,
                            "content3": content3,
                            "content4": content4,
                            "product_count": productCount,
                            "amount": currentPrice,
                            "plusPoint": plusPoint,
                            "minusPoint": minusPoint
                        }),
                        success: function(response_complete) {
                            let params = new URLSearchParams();
                            params.append("imp_uid", response_complete.imp_uid);
                            window.location.href = '/payment/paymentSuccessful?'+ params.toString(); // 결제가 완료된 후 리디렉션할 페이지
                        },
                        error: function() {
                            alert("서버 통신에 실패했습니다.");
                        }
                    });

                } else {
                    var msg = "결제에 실패하였습니다.";
                    msg += "에러내용 : " + rsp.error_msg;

                    // $.ajax({
                    //     type: "POST",
                    //     url: "/payment/paymentFailed",
                    //     data: JSON.stringify({
                    //         err_msg: rsp.error_msg,
                    //     }),
                    //     contentType: 'application/json',
                    //     success: function(response_failed) {
                    //         alert(msg);
                    //     },
                    //     error: function() {
                    //         alert('서버 통신에 실패했습니다.');
                    //     }
                    // });

                    alert(msg);


                    // /* 테스트용 */
                    // $.ajax({
                    //     type: "POST",
                    //     url: "/payment/complete",
                    //     data: $.param({
                    //         "merchant_uid": rsp.merchant_uid,
                    //         "imp_uid": rsp.imp_uid,
                    //         "apply_num": rsp.apply_num,
                    //         "buyer_email": rsp.buyer_email,
                    //         "payment_status": rsp.payment_status,
                    //         "product_name": orderName,
                    //         "product_idx": productIdx,
                    //         "product_type": reqIDX,
                    //         "content1": content1,
                    //         "content2": content2,
                    //         "content3": content3,
                    //         "content4": content4,
                    //         "product_count": productCount,
                    //         "amount": currentPrice,
                    //         "plusPoint": plusPoint,
                    //         "minusPoint": minusPoint
                    //     }),
                    //     success: function(response_complete) {
                    //         let params = new URLSearchParams();
                    //         params.append("imp_uid", response_complete.imp_uid);
                    //         window.location.href = '/payment/paymentSuccessful?'+ params.toString(); // 결제가 완료된 후 리디렉션할 페이지
                    //     },
                    //     error: function() {
                    //         alert("서버 통신에 실패했습니다.");
                    //     }
                    // });
                    // /* 테스트용 끝*/

                }
            });
        },
        error: function() {
            alert('오류가 발생했습니다.');
        }
    });
}