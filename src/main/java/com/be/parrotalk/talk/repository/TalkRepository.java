package com.be.parrotalk.talk.repository;

import com.be.parrotalk.talk.domain.Talks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkRepository extends JpaRepository<Talks, Long> {
}
