//package com.busanit.service;
//
//import com.busanit.domain.MemberRegFormDTO;
//import com.busanit.domain.TermsAgreeDTO;
//import com.busanit.entity.Member;
//import com.busanit.entity.TermsAgree;
//import com.busanit.repository.MemberRepository;
//import com.busanit.repository.TermsRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class TermsService {
//    private final TermsRepository termsRepository;
//    private final MemberRepository memberRepository;
//
//    public void saveTerms(TermsAgree termsAgree) {
//        termsRepository.save(termsAgree);
//    }
//
////    public void TermsAgreeService(MemberRepository memberRepository) {
////        this.memberRepository = memberRepository;
////    }
//
//    public TermsAgree createTermsAgree(MemberRegFormDTO memberRegFormDTO, TermsAgreeDTO termsAgreeDTO) {
//        Long memberId = memberRepository.findUserIdx(memberRegFormDTO.getEmail());
//        if (memberId == null) {
//            throw new RuntimeException("Member not found with email: " + memberRegFormDTO.getEmail());
//        }
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
//        return TermsAgree.builder()
//                .member_id(member)
//                .checkedTermsE(termsAgreeDTO.getCheckedTermsE())
//                .checkedTermsS(termsAgreeDTO.getCheckedTermsS())
//                .build();
//    }
//}
