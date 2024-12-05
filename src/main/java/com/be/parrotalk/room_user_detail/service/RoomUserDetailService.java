package com.be.parrotalk.room_user_detail.service;

import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.room_user_detail.domain.RoomUserDetail;
import com.be.parrotalk.room_user_detail.domain.RoomUserDetailId;
import com.be.parrotalk.room_user_detail.repository.RoomUserDetailRepository;
import com.be.parrotalk.room_user_detail.util.TodoStatus;
import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.todo.domain.Todos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomUserDetailService {

    private final RoomUserDetailRepository roomUserDetailRepository;

    public void saveRoomUserDetailsForTodos(List<Todos> todos, Talks talk, List<User> users) {

        // 한꺼번에 저장 => 위험성 N+1
        List<RoomUserDetail> roomUserDetails = todos.stream().flatMap(todo ->
                users.stream().map(user ->
                        RoomUserDetail.builder()
                                .userId(user.getId())
                                .talkId(talk.getTalkId())
                                .todoId(todo.getTodoId())
                                .todoStatus(TodoStatus.PENDING)
                                .build()
                )
        ).toList();

        roomUserDetailRepository.saveAll(roomUserDetails);
    }
}
