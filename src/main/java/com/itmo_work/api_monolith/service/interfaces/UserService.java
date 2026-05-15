package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.dto.request.UserCreateRequestDto;
import com.itmo_work.api_monolith.dto.response.UserCreateResponseDto;
import com.itmo_work.api_monolith.model.User;

import java.util.Optional;

public interface UserService {
    boolean existsByEmail(String email);

    Optional<User> findUserByLogin(String login);

    Optional<Long> findUserIdByLogin(String login);

    User getUserReferenceById(Long id);

    Optional<User> findUserById(Long id);

    UserCreateResponseDto createUser(UserCreateRequestDto userCreateRequestDto);

}
