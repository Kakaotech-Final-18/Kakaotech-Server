package com.be.parrotalk.todo.domain;

import jakarta.persistence.*;

@Entity
public class Todos {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column(length = 255, nullable = false)
    private String title;
}
