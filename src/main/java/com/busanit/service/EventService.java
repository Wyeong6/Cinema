package com.busanit.service;

import com.busanit.domain.EventDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.repository.EventRepository;
import com.busanit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    //추가
    public void saveEvent(EventDTO eventDTO){

        System.out.println("eventDTO.getMemberEmail()  " + eventDTO.getMemberEmail());
        Member member = memberRepository.findByEmail(eventDTO.getMemberEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 memberId: " + eventDTO.getMemberEmail()));
        System.out.println("memberEmail  " + member);
        Event event = Event.toEntity(eventDTO);

        member.addEvent(event);

        eventRepository.save(event);
    }

    //리스트

    //업데이트

    //삭제



}
