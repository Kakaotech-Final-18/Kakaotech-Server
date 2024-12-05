package com.be.parrotalk.room_user_detail.repository;

import com.be.parrotalk.room_user_detail.domain.RoomUserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomUserDetailRepository extends JpaRepository<RoomUserDetail, Long> {
}
