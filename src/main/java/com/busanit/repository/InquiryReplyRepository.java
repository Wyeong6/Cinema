package com.busanit.repository;

import com.busanit.entity.Inquiry;
import com.busanit.entity.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryReplyRepository  extends JpaRepository<InquiryReply, Long> {
}
