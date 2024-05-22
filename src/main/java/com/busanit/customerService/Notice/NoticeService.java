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


    public void incrementViewCount(com.busanit.customerService.Notice.Notice notice) {
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);
    }



    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    public com.busanit.customerService.Notice.Notice getPreviousNotice(Long id) {
        for (int i = 0; i < noticeList.size(); i++) {
            if (noticeList.get(i).getId().equals(id)) {
                if (i > 0) {
                    return noticeList.get(i - 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public com.busanit.customerService.Notice.Notice getNextNotice(Long id) {
        for (int i = 0; i < noticeList.size(); i++) {
            if (noticeList.get(i).getId().equals(id)) {
                if (i < noticeList.size() - 1) {
                    return noticeList.get(i + 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}