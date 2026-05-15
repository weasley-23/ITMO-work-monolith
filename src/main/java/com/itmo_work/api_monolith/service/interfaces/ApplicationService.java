package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.dto.ApplicationDto;
import com.itmo_work.api_monolith.dto.request.ApplicationCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.ApplicationStatusUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.ApplicationCreateResponseDto;
import com.itmo_work.api_monolith.dto.response.ApplicationStatusUpdateResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {

    ApplicationCreateResponseDto createApplication(Long id, Long userId, ApplicationCreateRequestDto applicationCreateRequestDto);

    boolean hasUserAlreadyAppliedForVacancy(Long userId, Long vacancyId);

    ApplicationCreateResponseDto updateApplication(Long id, Long userId, ApplicationCreateRequestDto applicationCreateRequestDto);

    ApplicationStatusUpdateResponseDto updateApplicationStatus(Long applicationId, Long currentUser, ApplicationStatusUpdateRequestDto applicationStatusUpdateRequestDto);

    Page<ApplicationDto> getAllApplicationsByVacancyId(Long id, Long currentUserId, Pageable pageable);
}
