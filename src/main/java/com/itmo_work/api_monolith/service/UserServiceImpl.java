package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.request.UserCreateRequestDto;
import com.itmo_work.api_monolith.dto.response.UserCreateResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.UserAlreadyExistsException;
import com.itmo_work.api_monolith.model.User;
import com.itmo_work.api_monolith.repository.UserRepository;
import com.itmo_work.api_monolith.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public boolean existsByEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException();
        return userRepository.existsUsersByEmail(email);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        if (login == null)
            throw new IllegalArgumentException();
        return userRepository.findUserByEmail(login);
    }

    @Override
    public Optional<Long> findUserIdByLogin(String login) {
        if (login == null)
            throw new IllegalArgumentException();
        return userRepository.findUserIdByEmail(login);
    }

    @Override
    public User getUserReferenceById(Long id) {
        if (id == null)
            throw new IllegalArgumentException();
        return userRepository.getReferenceById(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserCreateResponseDto createUser(UserCreateRequestDto userCreateRequestDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userCreateRequestDto.email());
        if(optionalUser.isPresent()) throw new UserAlreadyExistsException("Пользователь уже существует");
        User user = getUser(userCreateRequestDto);

        User savedUser = userRepository.save(user);

        return UserCreateResponseDto.builder()
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .id(savedUser.getId())
                .build();
    }

    private User getUser(UserCreateRequestDto userCreateRequestDto){
        User user = new User();
        user.setFullName(userCreateRequestDto.fullName());
        user.setPassword(userCreateRequestDto.password());
        user.setEmail(userCreateRequestDto.email());

        return user;
    }
}
