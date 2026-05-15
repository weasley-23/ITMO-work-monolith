package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndVacancy_Id(Long userId, Long vacancyId);

    Optional<Application> findApplicationByUser_IdAndVacancy_Id(Long userId, Long vacancyId);

    Optional<Application> findApplicationById(Long id);


    @EntityGraph(attributePaths = {"status", "vacancy", "vacancy.company", "user"})
    Page<Application> getAllByVacancy_Id(Long vacancyId, Pageable pageable);
}
