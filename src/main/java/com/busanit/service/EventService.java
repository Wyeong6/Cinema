package com.busanit.service;

import com.busanit.domain.EventDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.repository.EventRepository;
import com.busanit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    //추가
    public void saveEvent(EventDTO eventDTO){



        System.out.println("eventDTO.getMemberEmail()  " + eventDTO.getMemberEmail());

        Event event = eventRepository.findById(eventDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 eventId: " + eventDTO.getId()));

        Member member = memberRepository.findByEmail(eventDTO.getMemberEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 member 이메일: " + eventDTO.getMemberEmail()));

        event.addMember(member); // 이벤트에 회원 추가

//        Member member = memberRepository.findByEmail(eventDTO.getMemberEmail())
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 memberId: " + eventDTO.getMemberEmail()));
//        System.out.println("memberEmail  " + member);
//        Event event = Event.toEntity(eventDTO);
//
//        member.addEvent(event);

        eventRepository.save(event);
    }

    //리스트
    public Page<EventDTO> getEventList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Event> eventPage = eventRepository.findAll(pageable);
        List<EventDTO> eventDTOList = eventPage.getContent().stream()
                .map(EventDTO::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(eventDTOList, pageable, eventPage.getTotalElements());
    }

    //업데이트

    //삭제
    public void delete(Long eventId){
        eventRepository.deleteById(eventId);

    }




}
