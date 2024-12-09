package com.be.parrotalk.todo.controller;

import com.be.parrotalk.login.security.JwtTokenProvider;
import com.be.parrotalk.todo.dto.TodoCreationRequest;
import com.be.parrotalk.todo.dto.TodoUpdateRequestDto;
import com.be.parrotalk.todo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<String> saveTodos(@RequestBody TodoCreationRequest request, HttpServletRequest rq) {
        String accessToken = jwtTokenProvider.resolveToken(rq);
        String userId = jwtTokenProvider.getUserId(accessToken);
        todoService.saveTodosAndDetails(
            request.getTodos(),
            request.getTalk().getTalkId(),
            Long.parseLong(userId)
        );
        return ResponseEntity.ok("Todos and RoomUserDetails saved successfully.");
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateTodo(
            @RequestBody TodoUpdateRequestDto requestDto,
            HttpServletRequest request
    ) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        String userId = jwtTokenProvider.getUserId(accessToken);
        try {
            todoService.updateTodo(requestDto, userId);
            return ResponseEntity.ok().build(); // 200 OK 반환
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found 반환
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error 반환
        }
    }

}
