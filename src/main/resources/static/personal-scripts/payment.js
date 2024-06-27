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
            // const totalPriceElement = document.getElementById("total_price");
            // const self = this;
            // if(self.price == null || self.price == '') {
            //     alert("금액을 입력하세요.")
            // } else if(self.contractformcheck == false) {
            //     alert("이용약관에 동의 해주세요.")
            // } else {
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
                // let result = "";
                if(rsp.success) {
                    // var msg = "결제가 완료되었습니다.";
                    // msg += "고유ID : " + rsp.imp_uid;
                    // msg += "상점 거래ID : " + rsp.merchant_uid;
                    // msg += "결제 금액 : " + rsp.paid_amount;
                    // msg += "카드 승인번호 : " + rsp.apply_num;
                    $.ajax({
                        type: "POST",
                        url: "/payment/complete",
                        data: $.param({
                            "imp_uid": rsp.imp_uid,
                            "merchant_uid": rsp.merchant_uid,
                            "amount": rsp.paid_amount,
                            "apply_num": rsp.apply_num,
                            "payment_status": rsp.payment_status,
                            "buyer_email": rsp.buyer_email
                        }),
                        success: function(response_complete) {
                            alert(response_complete.merchant_uid);
                            // 결제가 완료된 후 리디렉션할 페이지
                            window.location.href = '/payment/paymentSuccessful';
                        },
                        error: function() {
                            alert("서버 통신에 실패했습니다.");
                        }
                    });

                    // result = "0";
                    //
                    // const form = new FormData();
                    // form.append("impuid", rsp.imp_uid)
                    // form.append("merchantuid", rsp.merchant_uid)
                    // form.append("paidamount", rsp.amount)
                    // form.append("applynum", rsp.apply_num)
                    // form.append("email", rsp.buyer_email)
                    //
                    // console.log(form)

                    // self.$axios.post("http://localhost:8080/payment/complete" + rsp.email, form)
                    //     .then(function(res) {
                    //         if(res.status === 200) {
                    //             console.log(res)
                    //             self.close();
                    //             window.location.reload(true);
                    //         }
                    //     });
                } else {
                    var msg = "결제에 실패하였습니다.";
                    msg += "에러내용 : " + rsp.error_msg;
                    // result = "1";
                    // self.contractformcheck = false;
                    // self.formattedPrice = "";
                    // self.price = 0;
                    // self.isPlaceholderVisible = true;

                    $.ajax({
                        type: "POST",
                        url: "/payment/paymentFailed",
                        data: JSON.stringify({
                            err_msg: rsp.error_msg,
                        }),
                        contentType: 'application/json',
                        success: function(response_failed) {
                            alert(msg);
                        },
                        error: function() {
                            alert('서버 통신에 실패했습니다.');
                        }
                    });

                    // alert(msg);
                }
                // if(result === "0") {
                //     alert("성공")
                // }
                // self.contractformcheck = false
                // alert(msg);
                // document.location.href = "/" // alert창 확인 후 이동할 url
            });
        },
        error: function() {
            alert('오류가 발생했습니다.');
        }
    });
}