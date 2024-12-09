package com.be.parrotalk.room_user_detail.dto;

import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.todo.domain.Todos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUserDetailResponse {
    private String receiverName;
    private String receiverProfileImage;
    private String talkCreatedAt;
    private Long talkId;
    private String todoTitle;
    private String todoStatus;
}
