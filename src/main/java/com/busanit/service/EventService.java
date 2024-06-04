package com.busanit.service;

import com.busanit.domain.EventDTO;
import com.busanit.entity.Event;
import com.busanit.entity.Member;
import com.busanit.repository.EventRepository;
import com.busanit.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    //추가
    public void saveEvent(EventDTO eventDTO){

        Member member = memberRepository.findByEmail(eventDTO.getMemberEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 member 이메일: " + eventDTO.getMemberEmail()));

        Event event = Event.toEntity(eventDTO);
        event.addMember(member); // 이벤트에 회원 추가
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

    //업데이트 할 데이터를 페이지에 표시
    public EventDTO getEvent(Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NullPointerException("event null"));
        return EventDTO.toDTO(event);
    }

    //수정해야할 것         event.update(eventDTO);
    public void updateEvent(EventDTO eventDTO) {
        Event event = eventRepository.findById(eventDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventDTO.getId()));

        event.update(eventDTO); // Event 엔티티의 update 메소드를 호출하여 업데이트
        eventRepository.save(event); // 변경 감지를 통한 업데이트
    }

    //삭제
    public void delete(Long eventId){
        eventRepository.deleteById(eventId);

    }

    // 중복 체크 메서드 추가
    public boolean isDuplicate(String eventDetail, String eventName) {
        return eventRepository.existsByEventDetailAndEventName(eventDetail, eventName);
    }
}
