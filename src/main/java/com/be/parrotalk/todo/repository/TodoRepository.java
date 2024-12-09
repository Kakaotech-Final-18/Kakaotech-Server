package com.be.parrotalk.todo.repository;

import com.be.parrotalk.todo.domain.Todos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todos, Long> {
}
