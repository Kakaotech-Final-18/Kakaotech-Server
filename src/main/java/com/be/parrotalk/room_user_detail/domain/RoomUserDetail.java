package com.be.parrotalk.room_user_detail.domain;

import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.room_user_detail.domain.RoomUserDetailId;
import com.be.parrotalk.room_user_detail.util.TodoStatus;
import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.todo.domain.Todos;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_user_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RoomUserDetailId.class)
public class RoomUserDetail {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "todo_id")
    private Long todoId;

    @Id
    @Column(name = "talk_id")
    private Long talkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "todo_status", length = 255, nullable = false)
    private TodoStatus todoStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user; // 동일한 컬럼 이름 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", insertable = false, updatable = false)
    private Todos todo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_id", insertable = false, updatable = false)
    private Talks talk;
}
