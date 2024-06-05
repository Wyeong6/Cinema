package com.busanit.service;

import com.busanit.domain.EventDTO;
import com.busanit.domain.NoticeDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.entity.Notice;
import com.busanit.entity.movie.Comment;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    //공지사항 저장
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
    //공지사항 리스트
    public Page<NoticeDTO> getNoticeList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("noticeId").ascending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        List<NoticeDTO> noticeDTOList = noticePage.getContent().stream()
                .map(NoticeDTO::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(noticeDTOList, pageable, noticePage.getTotalElements());
    }

    //업데이트 할 데이터를 페이지에 표시
    public NoticeDTO getNotice(Long noticeId){
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NullPointerException("notice null"));
        notice.setViewCount(notice.getViewCount() + 1);
        return NoticeDTO.toDTO(notice);
    }

    public Notice getPreviousNotice(Long noticeId) {
        return noticeRepository.findPreviousNotice(noticeId);
    }

    public Notice getNextNotice(Long noticeId) {
        return noticeRepository.findNextNotice(noticeId);
    }
    //업데이트기능
    public void updateNotice(NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeDTO.getNoticeId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + noticeDTO.getNoticeId()));

        notice.update(noticeDTO);
        noticeRepository.save(notice);
    }
    //삭제
    public void delete(Long noticeId){
        noticeRepository.deleteById(noticeId);

    }

    // 중복 체크 메서드 추가
    public boolean isDuplicate(String noticeTitle, String noticeContent) {
        return noticeRepository.existsByNoticeTitleAndNoticeContent(noticeTitle, noticeContent);
    }
}
