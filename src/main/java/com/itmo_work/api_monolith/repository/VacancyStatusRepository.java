package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.VacancyStatus;
import com.itmo_work.api_monolith.model.VacancyStatusName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacancyStatusRepository extends JpaRepository<VacancyStatus, Long> {
    Optional<VacancyStatus> findByVacancyStatusName(VacancyStatusName statusName);
    Optional<VacancyStatus> findVacancyStatusById(Long vacancyStatusId);
}
