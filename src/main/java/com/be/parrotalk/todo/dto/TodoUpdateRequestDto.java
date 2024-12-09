package com.be.parrotalk.todo.dto;

import com.be.parrotalk.room_user_detail.util.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO for Update Request
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoUpdateRequestDto {
    private Long talkId;
    private String todoTitle;
    private String newTodoStatus;
}