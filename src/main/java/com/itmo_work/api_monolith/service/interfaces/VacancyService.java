package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.dto.request.VacancyCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.model.Vacancy;
import com.itmo_work.api_monolith.model.VacancyStatus;
import com.itmo_work.api_monolith.model.VacancyStatusName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VacancyService {

    VacancyResponseDto updateAndChangeStatus(Long userId, Long id, VacancyUpdateRequestDto dto, VacancyStatusName newStatus);
    VacancyResponseDto updateVacancy(Long userId, Long id, VacancyUpdateRequestDto dto);
    VacancyResponseDto changeStatus(Long userId, Long id, VacancyStatusName newStatus);
    VacancyResponseDto createVacancy(Long userId, VacancyCreateRequestDto request, VacancyStatusName statusName);

    Vacancy getReferenceById(Long vacancyId);
    boolean existsVacancyById(Long id);
    VacancyStatus findCurrentVacancyStatusByVacancyId(Long id);
    Long findCompanyByVacancyId(Long vacancyId);
    Page<VacancyResponseDto> getAllPublishedVacancies(Pageable pageable);
}