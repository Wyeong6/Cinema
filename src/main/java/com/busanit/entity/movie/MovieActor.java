package com.busanit.entity.movie;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Actor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorId;

    // 배우 이름
    private String actorName;

    


}
