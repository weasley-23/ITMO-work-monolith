package com.itmo_work.api_monolith.controller;

import com.itmo_work.api_monolith.dto.request.UserCreateRequestDto;
import com.itmo_work.api_monolith.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userCreateRequestDto));
    }
}
