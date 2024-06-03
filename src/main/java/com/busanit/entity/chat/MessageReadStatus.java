package com.busanit.entity.chat;

import com.busanit.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Column(name = "read_timestamp")
    private LocalDateTime readTimestamp;

//    ----메세지 읽음 표시하는 경우 사용
//
//    //기존의 연관관계를 명시적으로 해제 후 직접적으로 양방향 연관관계를 설정하고 유지
//    //복잡한 양방향 연관 관계 관리와 데이터 일관성 보장 , 객체의 상태변경 추적할 때
//    public void setMessage(Message message) {
//        // 이전 메시지에서 현재 읽음 상태를 제거
//        if (this.message != null) {
//            this.message.getReadStatuses().remove(this);
//        }
//        this.message = message;
//        // 새 메시지의 읽음 상태 목록에 현재 읽음 상태를 추가하지 않았다면 추가
//        if (!message.getReadStatuses().contains(this)) {
//            message.getReadStatuses().add(this);
//        }
//    }
//        //단순한 연관관계 관리, 단방향 설정 ,단일 연관성 유지일 때 사용
////    public void setMessage(Message message){
////        this.message = message;
////        if(message != null){
////            message.addReadStatus(this);
////        }
////    }
}
