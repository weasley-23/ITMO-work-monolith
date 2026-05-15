package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.exception.exceptions.VacancyStatusNotFoundException;
import com.itmo_work.api_monolith.model.VacancyStatus;
import com.itmo_work.api_monolith.model.VacancyStatusName;
import com.itmo_work.api_monolith.repository.VacancyStatusRepository;
import com.itmo_work.api_monolith.service.interfaces.VacancyStatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VacancyStatusServiceImpl implements VacancyStatusService {
    private final VacancyStatusRepository vacancyStatusRepository;

    @Override
    public VacancyStatus findByVacancyStatusName(VacancyStatusName statusName) {
        return vacancyStatusRepository.findByVacancyStatusName(statusName)
                .orElseThrow(VacancyStatusNotFoundException::new);
    }

    @Override
    public VacancyStatus findVacancyStatusById(Long vacancyStatusId) {
        return vacancyStatusRepository.findVacancyStatusById(vacancyStatusId)
                .orElseThrow(VacancyStatusNotFoundException::new);
    }
}
