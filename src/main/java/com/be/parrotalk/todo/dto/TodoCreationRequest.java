package com.be.parrotalk.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoCreationRequest {
    private List<String> todos; // Todo 제목 리스트
    private TalkRequest talk;   // Talk 정보
    private List<UserRequest> users; // 참여자 정보
}