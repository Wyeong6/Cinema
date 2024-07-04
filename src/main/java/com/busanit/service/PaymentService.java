package com.busanit.service;

import com.busanit.domain.PaymentDTO;
import com.busanit.entity.Payment;
import com.busanit.entity.Point;
import com.busanit.entity.Snack;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final SnackService snackService;
    private final MovieService movieService;

    @Value("${imp_rest_api_key}")
    private String imp_rest_api_key;
    @Value("${imp_rest_api_secret}")
    private String imp_rest_api_secret;

    // 결제 DB 저장
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    // 결제 완료 내역
    public PaymentDTO get(String imp_uid) {
        Payment payment = paymentRepository.findById(paymentRepository.findByImpUid(imp_uid)).orElseThrow(() -> new NullPointerException("payment null"));

        return PaymentDTO.toDTO(payment);
    }

    // 최근 3개월간 영화관람 count
    public long getMovieCount(Long memberId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);
        return paymentRepository.countByMovieMembership(memberId, startDate, endDate);
    }

    // 영화 결제 내역
    public Slice<PaymentDTO> getMoviePaymentInfo(Long member_id, Pageable pageable) {
        Slice<Payment> paymentSlice = paymentRepository.findByMember_IdAndProductTypeContaining(member_id, "MO", pageable);

        return PaymentDTO.toDTOMovieSlice(paymentSlice, movieService);
    }

    // 스낵, 장바구니 결제 내역
    public Slice<PaymentDTO> getSnackPaymentInfo(Long member_id, Pageable pageable) {
        Slice<Payment> paymentSlice = paymentRepository.findByMember_IdAndProductTypeContaining(member_id, "S", pageable);

        return PaymentDTO.toDTOSnackSlice(paymentSlice, snackService);
    }

    // 영화 상세 보기
    public PaymentDTO getMoviePaymentDetail(String paymentId) {
        Payment payment = paymentRepository.findById(Long.valueOf(paymentId)).orElseThrow(() -> new NullPointerException("payment null"));

        return PaymentDTO.toDTOMovie(payment, movieService);
    }

    // 스낵 상세 보기
    public PaymentDTO getSnackPaymentDetail(String paymentId) {
        Payment payment = paymentRepository.findById(Long.valueOf(paymentId)).orElseThrow(() -> new NullPointerException("payment null"));

        return PaymentDTO.toDTOSnack(payment, snackService);
    }

    // 결제 상태 수정
    public void updatePaymentStatus(String impUid, Long memberId) {
        paymentRepository.updatePaymentStatus(impUid, memberId);
    }

    // 결제 토큰 받기
    public String getImportToken() {
        String result = null;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.iamport.kr/users/getToken");
        // 맵을 직접 변환하는 대신 리스트를 사용하여 매개변수를 추가
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("imp_key", imp_rest_api_key));
        params.add(new BasicNameValuePair("imp_secret", imp_rest_api_secret));

//        // 맵 변환 방법
//        Map<String,String> m =new HashMap<String,String>();
//        m.put("imp_key", imp_rest_api_key);
//        m.put("imp_secret", imp_rest_api_secret);
//        try { post.setEntity(new UrlEncodedFormEntity(convertParameter(m)));

        try { post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse res = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            String body = EntityUtils.toString(res.getEntity());
            JsonNode rootNode = mapper.readTree(body);
            JsonNode resNode = rootNode.get("response");
            result = resNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
