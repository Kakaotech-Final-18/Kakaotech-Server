package com.be.parrotalk.todo.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.room_user_detail.domain.RoomUserDetail;
import com.be.parrotalk.room_user_detail.repository.RoomUserDetailRepository;
import com.be.parrotalk.room_user_detail.service.RoomUserDetailService;
import com.be.parrotalk.room_user_detail.util.TodoStatus;
import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.talk.repository.TalkRepository;
import com.be.parrotalk.todo.domain.Todos;
import com.be.parrotalk.todo.dto.TodoUpdateRequestDto;
import com.be.parrotalk.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final RoomUserDetailService roomUserDetailService;
    private final UserRepository userRepository;
    private final TalkRepository talkRepository;
    private final RoomUserDetailRepository roomUserDetailRepository;

    public void saveTodosAndDetails(List<String> todoTitles, Long talkId, Long userId) {
        // 1. Talk 조회
        Talks talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("Talk not found with id: " + talkId));

        // 3. Todos 저장
        List<Todos> todos = todoTitles.stream()
                .map(title -> Todos.builder().title(title).build())
                .toList();
        todoRepository.saveAll(todos);

        // 4. RoomUserDetail 저장
        roomUserDetailService.saveRoomUserDetailsForTodos(todos, talk, userId);
    }

    public void updateTodo(TodoUpdateRequestDto requestDto, String userId) {
        RoomUserDetail todo = roomUserDetailRepository.findByTalkIdAndUserIdAndTodoTitle(
                requestDto.getTalkId(),
                Long.parseLong(userId),
                requestDto.getTodoTitle()
        ).orElseThrow(() -> new EntityNotFoundException("Todo not found"));

        // 현재 사용자가 이 Todo를 업데이트할 권한이 있는지 확인
        if (!todo.getUser().getId().equals(Long.parseLong(userId))) {
            throw new RuntimeException("Unauthorized access"); // 사용자 정의 예외 사용
        }

        System.out.println(requestDto.getNewTodoStatus());
        if (requestDto.getNewTodoStatus().equals("PENDING")) {
            todo.setTodoStatus(TodoStatus.PENDING);
        } else {
            todo.setTodoStatus(TodoStatus.DONE);
        }
        System.out.println(todo.getTodoId());
        System.out.println(todo.getUserId());
        System.out.println(todo.getTalkId());
        System.out.println(todo.getTodoStatus());

        roomUserDetailRepository.save(todo); // 데이터 저장
    }



}
