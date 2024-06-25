package com.busanit.controller;

import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.service.MemberService;
import com.busanit.service.SnackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/snack")
@RequiredArgsConstructor
public class SnackController {

    private final SnackService snackService;
    private final MemberService memberService;

    @Value("${html5_inicis_key}")
    private String html5InicisKey;

    @GetMapping("/snackList")
    public String snackList(Model model, @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SnackDTO> snackDTOList = null;

        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

        int startPage = Math.max(1, snackDTOList.getPageable().getPageNumber() -5);
        int endPage = Math.min(snackDTOList.getTotalPages(), snackDTOList.getPageable().getPageNumber() +5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "snack/snack_list";
    }

    @GetMapping("/detail")
    public String detail(Long id, Model model, @PageableDefault(size = 4) Pageable pageable) {

        SnackDTO snackDTO = snackService.get(id);
        model.addAttribute("snack", snackDTO);

        // 스낵 추천 리스트(랜덤)
        Page<SnackDTO> snackDTOList = null;
        snackDTOList = snackService.getSnackListRandom(pageable);
        model.addAttribute("snackList", snackDTOList);

        // 결제
        model.addAttribute("html5InicisKey", html5InicisKey);

        // 현재 로그인한 사용자의 정보 (이메일, idx)
        List<String> memberInfo = new ArrayList<>();
        String userEmail = memberService.currentLoggedInEmail();
        memberInfo.add(userEmail);
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        memberInfo.add(memberRegFormDTO.getId().toString());
        model.addAttribute("memberInfo", memberInfo); // 사용자 정보 리스트(이메일, idx)

        return "snack/snack_get";
    }
}
