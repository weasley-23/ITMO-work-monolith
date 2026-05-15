package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUsersByEmail(String email);

    Optional<User> findUserByEmail(String email);

    @Query("select u.id from User u where u.email= :email")
    Optional<Long> findUserIdByEmail(String email);
}
