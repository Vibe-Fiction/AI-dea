package com.spring.aidea.vibefiction.dto.response;


import com.spring.aidea.vibefiction.entity.Collaborators;
import lombok.*;

import static com.spring.aidea.vibefiction.entity.Collaborators.*;

/**
 *
 *
 * return 작품id, 유저 id, 유저 닉네임, 참여유형
 *
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorsResponseDtoSH {

     Long novelId;
     Long userId;
     String userNickname;
     Role role;

     public static CollaboratorsResponseDtoSH from(Collaborators collaborator){

         CollaboratorsResponseDtoSH collaboratorsDto = CollaboratorsResponseDtoSH.builder()
                 .novelId(collaborator.getNovel().getNovelId())
                 .userId(collaborator.getUser().getUserId())
                 .userNickname(collaborator.getUser().getNickname())
                 .role(collaborator.getRole())
                 .build();

         return collaboratorsDto;

     }












}
