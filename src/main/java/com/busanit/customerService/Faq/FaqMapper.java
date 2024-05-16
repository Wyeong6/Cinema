package com.busanit.customerService.Faq;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Integrates with Spring
public interface FaqMapper {

    Faq toEntity(FaqDTO faqDTO);

    FaqDTO toDto(Faq faq);
}