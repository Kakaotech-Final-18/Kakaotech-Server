package com.be.parrotalk.todo.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.room_user_detail.service.RoomUserDetailService;
import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.talk.repository.TalkRepository;
import com.be.parrotalk.todo.domain.Todos;
import com.be.parrotalk.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final RoomUserDetailService roomUserDetailService;
    private final UserRepository userRepository;
    private final TalkRepository talkRepository;

    public void saveTodosAndDetails(List<String> todoTitles, Long talkId, List<String> userEmails) {
        // 1. Talk 조회
        Talks talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("Talk not found with id: " + talkId));

        // 2. User 조회
        List<User> users = userEmails.stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email)))
                .toList();

        // 3. Todos 저장
        List<Todos> todos = todoTitles.stream()
                .map(title -> Todos.builder().title(title).build())
                .toList();
        todoRepository.saveAll(todos);

        // 4. RoomUserDetail 저장
        roomUserDetailService.saveRoomUserDetailsForTodos(todos, talk, users);
    }
}
