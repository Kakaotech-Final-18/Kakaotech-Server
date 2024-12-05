package com.be.parrotalk.todo.controller;

import com.be.parrotalk.todo.dto.TodoCreationRequest;
import com.be.parrotalk.todo.dto.UserRequest;
import com.be.parrotalk.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/create")
    public ResponseEntity<String> saveTodos(@RequestBody TodoCreationRequest request) {
        todoService.saveTodosAndDetails(
            request.getTodos(),
            request.getTalk().getTalkId(),
                request.getUsers().stream().map(UserRequest::getEmail).toList()
        );
        return ResponseEntity.ok("Todos and RoomUserDetails saved successfully.");
    }

}
