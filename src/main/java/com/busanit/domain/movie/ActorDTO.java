package com.busanit.domain.movie;

import com.busanit.entity.movie.MovieActor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO {

    private Long actorId;
    private String actorName;
    private String actorGender;
    private String actorProfilePic;


    public static ActorDTO convertToDto(MovieActor movieActor) {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setActorId(movieActor.getActorId());
        actorDTO.setActorName(movieActor.getActorName());
        actorDTO.setActorGender(movieActor.getActorGender());
        actorDTO.setActorProfilePic(movieActor.getActorProfilePic());
        return actorDTO;
    }

}
