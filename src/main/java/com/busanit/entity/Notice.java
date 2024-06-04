package com.busanit.entity;

import com.busanit.domain.EventDTO;
import com.busanit.domain.NoticeDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;
    private String memberEmail;
    private String noticeTitle;
    private String noticeContent;
    private int viewCount;

    @ManyToMany
    @JoinTable(
            name = "member_notice",
            joinColumns = @JoinColumn(name = "notice_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<Member> members = new ArrayList<>();

    public void addMember(Member member) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        this.members.add(member);
    }

    public static Notice toEntity(NoticeDTO noticeDTO) {
        return Notice.builder()
               .noticeTitle(noticeDTO.getNoticeTitle())
               .noticeContent(noticeDTO.getNoticeContent())
               .memberEmail(noticeDTO.getMemberEmail())
               .build();
    }

    public void update(NoticeDTO noticeDTO) {
        this.memberEmail = noticeDTO.getMemberEmail();
        this.noticeTitle = noticeDTO.getNoticeTitle();
        this.noticeContent = noticeDTO.getNoticeContent();
        this.viewCount = noticeDTO.getViewCount();
    }
}
