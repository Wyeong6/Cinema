package com.busanit.service;

import com.busanit.domain.EventDTO;
import com.busanit.domain.NoticeDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.entity.Notice;
import com.busanit.entity.movie.Comment;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;


    public void saveNotice(NoticeDTO noticeDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 member 이메일 "));

        Notice notice = Notice.toEntity(noticeDTO);
        notice.setMemberEmail(memberEmail);
        notice.addMember(member); // 이벤트에 회원 추가

        noticeRepository.save(notice);
    }

    public Page<NoticeDTO> getNoticeList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("noticeId").ascending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        List<NoticeDTO> noticeDTOList = noticePage.getContent().stream()
                .map(NoticeDTO::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(noticeDTOList, pageable, noticePage.getTotalElements());
    }

}
