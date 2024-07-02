package com.busanit.controller;

import com.busanit.domain.*;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.entity.Payment;
import com.busanit.entity.Point;
import com.busanit.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.util.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final SnackService snackService;
    private final MovieService movieService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final TheaterNumberService theaterNumberService;
    private final PointService pointService;
    private final MemberService memberService;

    @Value("${html5_inicis_key}")
    private String html5InicisKey;

    @GetMapping("")
    public String payment(
            @RequestParam Long scheduleId,
            @RequestParam String selectedSeats,
            @RequestParam int adultCount,
            @RequestParam int teenagerCount,
            @RequestParam int grandCount,
            @RequestParam double totalAmount,
            @PageableDefault(size = 1, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);
        List<MovieDTO> movieDTOs = movieService.getMovieDetailInfo(scheduleDTO.getMovieId());

        // 현재 로그인한 사용자의 정보 (이메일, idx)
        List<String> memberInfo = new ArrayList<>();
        String userEmail = memberService.currentLoggedInEmail();
        memberInfo.add(userEmail);

        // 사용자의 등급별 적립율 + 포인트 정보
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        memberInfo.add(memberRegFormDTO.getId().toString());
        long userGradeLong = memberService.userGrade();
        double gradeRate = switch ((int)userGradeLong) {
            case 1 -> 0.1;
            case 2 -> 0.05;
            default -> 0.03;
        };
        long currentPoints = 0;
        List<PointDTO> points = pointService.getPointInfo(memberService.findUserIdx(memberService.currentLoggedInEmail()), pageable).getContent();
        if (!points.isEmpty()) {
            currentPoints = points.get(0).getCurrentPoints();
        }


        model.addAttribute("memberInfo", memberInfo); // 사용자 정보 리스트(이메일, idx)
        model.addAttribute("gradeInfo", gradeRate); // 사용자 등급 적립율
        model.addAttribute("pointInfo", currentPoints); // 사용자 보유 포인트
//        model.addAttribute("html5InicisKey", html5InicisKey); // 결제키

        model.addAttribute("scheduleDTO", scheduleDTO);
        model.addAttribute("movieDTOs", movieDTOs);
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("adultCount", adultCount);
        model.addAttribute("teenagerCount", teenagerCount);
        model.addAttribute("grandCount", grandCount);
        model.addAttribute("totalAmount", totalAmount);

        return "payment/payment_window"; // 뷰 이름 리턴
    }

    // 스낵 cart
    @GetMapping("/cartList")
    public String cartList(Model model, @PageableDefault(size = 6) Pageable pageable) {

        // 스낵 추천 리스트(랜덤)
        Page<SnackDTO> snackDTOList = null;
        snackDTOList = snackService.getSnackListRandom(pageable);
        model.addAttribute("snackList", snackDTOList);

        return "payment/cart_list";
    }

    @PostMapping("/request")
    @ResponseBody
    public Map<String, String> paymentRequest(@RequestBody Map<String, String> request) {
        /* html 파일에 결제 구동 스크립트 파일, 변수(3가지) 필요 */
        Map<String, String> response = new HashMap<>();

        response.put("html5InicisKey", html5InicisKey);
        response.put("orderName", request.get("orderName")); // 제품명
        response.put("currentPrice", request.get("currentPrice"));
        response.put("reqIDX", request.get("reqIDX")); // 결제를 요청한 페이지 IDX

        // 현재 로그인한 사용자의 정보 (이메일, idx)
//        List<String> memberInfo = new ArrayList<>();
        String userEmail = memberService.currentLoggedInEmail();
//        memberInfo.add(userEmail);
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
//        memberInfo.add(memberRegFormDTO.getId().toString());
        response.put("memberEmail", userEmail);
        response.put("memberName", memberRegFormDTO.getName());

        return response;
    }

    @PostMapping("/complete")
    @ResponseBody
    public Map<String, String> paymentComplete(@RequestParam String merchant_uid,
                                               @RequestParam String imp_uid,
                                               @RequestParam String apply_num, // 카드 승인 번호
                                               @RequestParam String buyer_email, // 결제사에서 받아오는 메일이라 결제시 메일 주소 수정해서 보내면 로그인한 사람 메일과 다를 것 같아서 데이터 받아봄
                                               @RequestParam String product_idx,
                                               @RequestParam String product_name,
                                               @RequestParam String product_type,
                                               @RequestParam String content1,
                                               @RequestParam String content2,
                                               @RequestParam String content3,
                                               @RequestParam String content4,
                                               @RequestParam String product_count,
                                               @RequestParam Integer amount,
                                               @RequestParam Integer plusPoint,
                                               @RequestParam Integer minusPoint,
                                               @PageableDefault(size = 1, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                               PaymentDTO paymentDTO,
                                               PointDTO pointDTO) {

        Map<String, String> response_complete = new HashMap<>();

        if(merchant_uid != null) {
            response_complete.put("imp_uid", imp_uid);

            paymentDTO.setMerchantUid(merchant_uid);
            paymentDTO.setImpUid(imp_uid);
            paymentDTO.setApplyNum(apply_num);
            paymentDTO.setBuyerEmail(buyer_email);
            paymentDTO.setPaymentType("CARD");
            paymentDTO.setPaymentStatus("결제완료");
            paymentDTO.setProductIdx(product_idx);
            paymentDTO.setProductName(product_name);
            paymentDTO.setProductType(product_type);
            paymentDTO.setContent1(content1);
            paymentDTO.setContent2(content2);
            paymentDTO.setContent3(content3);
            paymentDTO.setContent4(content4);
            paymentDTO.setProductCount(product_count);
            paymentDTO.setTotalPrice(amount);
            paymentService.savePayment(Payment.toEntity(paymentDTO, memberService.findUserIdx(memberService.currentLoggedInEmail())));

            pointDTO.setContent("["+product_name+"] 구매");
            pointDTO.setImpUid(imp_uid);
            // 현재 포인트
            int currentPoints = pointService.getPointInfo(memberService.findUserIdx(memberService.currentLoggedInEmail()), pageable).getContent().get(0).getCurrentPoints();
            // 누적 포인트
            int totalPoints = pointService.getPointInfo(memberService.findUserIdx(memberService.currentLoggedInEmail()), pageable).getContent().get(0).getTotalPoints();
            pointDTO.setTotalPoints(totalPoints);

            if((product_type).equals("MO")) { // 영화
                pointDTO.setContentType(true);
            } else { // 스낵
                pointDTO.setContentType(false);
            }

            // 포인트타입
            if(minusPoint > 0) { // 사용
                pointDTO.setPointType("-");
                pointDTO.setPoints(minusPoint);
                currentPoints -= minusPoint;
                pointDTO.setCurrentPoints(currentPoints);
                pointService.savePoint(Point.toEntity(memberService.findUserIdx(memberService.currentLoggedInEmail()), pointDTO));
            }

            if(plusPoint > 0) { // 적립
                pointDTO.setPointType("+");
                pointDTO.setPoints(plusPoint);
                currentPoints += plusPoint;
                pointDTO.setCurrentPoints(currentPoints);
                totalPoints += plusPoint;
                pointDTO.setTotalPoints(totalPoints);
                pointService.savePoint(Point.toEntity(memberService.findUserIdx(memberService.currentLoggedInEmail()), pointDTO));
            }
        }
        return response_complete;
    }

    @GetMapping("/paymentSuccessful")
    public String paymentSuccessful(@RequestParam String imp_uid, Model model) {

        PaymentDTO paymentDTO = paymentService.get(imp_uid);
        if(paymentDTO.getProductType().equals("MO")){ // 영화
            List<MovieDTO> movieDTOs = movieService.getMovieDetailInfo(Long.valueOf(paymentDTO.getProductIdx()));
            model.addAttribute("movieDTOs", movieDTOs);
        } else { // 스낵
            SnackDTO snackDTO = snackService.get(Long.valueOf(paymentDTO.getProductIdx())); // 스낵 바로 결제
            model.addAttribute("productInfo", snackDTO);
        }

        if(memberService.findUserIdx(memberService.currentLoggedInEmail()) == null ||
                paymentDTO.getMember_id() == null ||
                memberService.findUserIdx(memberService.currentLoggedInEmail()) != paymentDTO.getMember_id()) { // 비회원 혹은 다른 멤버가 요청할때
            return "redirect:/";
        } else { // 해당 멤버가 요청할때

            model.addAttribute("paymentInfo", paymentDTO);
            model.addAttribute("plusPoint", pointService.getPlusPoint(imp_uid));
            model.addAttribute("minusPoint", pointService.getMinusPoint(imp_uid));
            return "payment/payment_complete";
        }
    }

    // 주문 취소
    @PostMapping("/paymentCancel")
    @ResponseBody
    public Map<String, String> paymentCancel(@RequestParam("merchant_uid") String merchant_uid , @RequestParam("imp_uid") String imp_uid) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://api.iamport.kr/payments/cancel");
        post.setHeader("Authorization", paymentService.getImportToken());
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("merchant_uid", merchant_uid));

        Map<String, String> response_complete = new HashMap<>();

        String asd = "";
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse res = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            String body = EntityUtils.toString(res.getEntity());
            JsonNode rootNode = mapper.readTree(body); asd = rootNode.get("response").asText();
        } catch (Exception e) {
            e.printStackTrace();
            response_complete.put("errorMsg", "errorMsg");
        } if (asd.equals("null")) {
            System.err.println("환불실패");
            response_complete.put("errorMsg", "errorMsg");
        } else {
            System.err.println("환불성공"+imp_uid);
            paymentService.updatePaymentStatus(imp_uid, memberService.findUserIdx(memberService.currentLoggedInEmail()));
            response_complete.put("imp_uid", imp_uid);
        }
        return response_complete;
    }

    @GetMapping("/paymentCancelSuccessful")
    public String paymentCancelSuccessful(@RequestParam String imp_uid, Model model) {

        PaymentDTO paymentDTO = paymentService.get(imp_uid);
        if(paymentDTO.getProductType().equals("MO")){ // 영화
            List<MovieDTO> movieDTOs = movieService.getMovieDetailInfo(Long.valueOf(paymentDTO.getProductIdx()));
            model.addAttribute("movieDTOs", movieDTOs);
        } else { // 스낵
            SnackDTO snackDTO = snackService.get(Long.valueOf(paymentDTO.getProductIdx())); // 스낵 바로 결제
            model.addAttribute("productInfo", snackDTO);
        }

        if(memberService.findUserIdx(memberService.currentLoggedInEmail()) == null ||
                paymentDTO.getMember_id() == null ||
                memberService.findUserIdx(memberService.currentLoggedInEmail()) != paymentDTO.getMember_id()) { // 비회원 혹은 다른 멤버가 요청할때
            return "redirect:/";
        } else { // 해당 멤버가 요청할때
            model.addAttribute("paymentInfo", paymentDTO);
            return "payment/payment_cancel";
        }
    }
}
