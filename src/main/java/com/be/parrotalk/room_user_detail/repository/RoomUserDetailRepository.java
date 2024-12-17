package com.be.parrotalk.room_user_detail.repository;

import com.be.parrotalk.room_user_detail.domain.RoomUserDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomUserDetailRepository extends JpaRepository<RoomUserDetail, Long> {
    @Query("SELECT r FROM RoomUserDetail r JOIN FETCH r.talk JOIN FETCH r.todo WHERE r.userId = :userId")
    List<RoomUserDetail> findByUserId(@Param("userId") Long userId);
    Optional<RoomUserDetail> findByTalkIdAndUserIdAndTodoTitle(Long talkId, Long userId, String todoTitle);

    List<RoomUserDetail> findByTalkIdAndUserId(Long talkId, Long userId);
}
