package com.busanit.customerService.Faq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class FaqService {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    @Autowired
    public FaqService(FaqRepository faqRepository, FaqMapper faqMapper) {
        this.faqRepository = faqRepository;
        this.faqMapper = faqMapper;
    }

    public void FaqSave(FaqDTO faqDTO) {
        Faq faq = faqMapper.toEntity(faqDTO); // DTO -> Entity
        faqRepository.save(faq); // Entity 저장
    }

    public FaqDTO findById(long id) {
        return faqRepository.findById(id).map(faqMapper::toDto).orElse(null);
    }

    public Page<FaqDTO> getFAQPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // Convert to zero-based page numbering
        Page<Faq> faqPage = faqRepository.findAll(pageable);
        return faqPage.map(faqMapper::toDto);
    }
}