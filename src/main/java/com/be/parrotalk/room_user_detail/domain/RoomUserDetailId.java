package com.be.parrotalk.room_user_detail.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoomUserDetailId implements Serializable {

    private Long userId;
    private Long todoId;
    private Long talkId;
}
