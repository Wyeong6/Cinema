package com.busanit.customerService.Notice;

import com.busanit.customerService.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;
    private final List<Notice> noticeList;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, NoticeMapper noticeMapper) {
        this.noticeRepository = noticeRepository;
        this.noticeMapper = noticeMapper;
        this.noticeList = new ArrayList<>();
    }

    public void NoticeSave(NoticeDTO noticeDTO) {
        Notice notice = noticeMapper.toNotice(noticeDTO);
        noticeRepository.save(notice);
    }

    public NoticeDTO findById(Long id) {
        return noticeRepository.findById(id).map(noticeMapper::toNoticeDTO).orElse(null);
    }

    public void prepareNoticeList(Model model, int page, int size) {
        Page<NoticeDTO> noticePage = getNoticePaginated(page, size);
        model.addAttribute("noticeList", noticePage.getContent()); // Get content from Page
        int totalPages = noticePage.getTotalPages(); // Get total pages from Page
        PaginationUtil.addPaginationAttributes(model, page, size, totalPages);

        // Calculate page numbers to display
        List<Integer> pageNumbers = PaginationUtil.calculatePageNumbers(page, totalPages);
        model.addAttribute("pageNumbers", pageNumbers);
    }

    public Page<NoticeDTO> getNoticePaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> noticePage = noticeRepository.findAllOrderedByPinnedAndId(pageable);


        return noticePage.map(noticeMapper::toNoticeDTO);
    }


    // 조회수
    public void incrementViewCount(com.busanit.customerService.Notice.Notice notice) {
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);
    }

    // 상세 게시물 보기
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    // 이전 게시물
    public Notice getPreviousNotice(Long id) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Notice> previousNoticePage = noticeRepository.findPreviousNotice(id, pageable);
        return previousNoticePage.getContent().isEmpty() ? null : previousNoticePage.getContent().get(0);
    }

    // 다음 게시물
    public Notice getNextNotice(Long id) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Notice> nextNoticePage = noticeRepository.findNextNotice(id, pageable);
        return nextNoticePage.getContent().isEmpty() ? null : nextNoticePage.getContent().get(0);
    }

    public boolean deleteNoticeById(Long id) {
        Optional<Notice> noticeOptional = noticeRepository.findById(id);
        if(noticeOptional.isPresent()) {
            noticeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}