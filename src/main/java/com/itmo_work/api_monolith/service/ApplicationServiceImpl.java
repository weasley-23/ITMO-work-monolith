package com.itmo_work.api_monolith.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo_work.api_monolith.dto.ApplicationDto;
import com.itmo_work.api_monolith.dto.request.ApplicationCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.ApplicationStatusUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.ApplicationCreateResponseDto;
import com.itmo_work.api_monolith.dto.response.ApplicationStatusUpdateResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.*;
import com.itmo_work.api_monolith.mappers.ApplicationMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.ApplicationRepository;
import com.itmo_work.api_monolith.service.interfaces.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VacancyService vacancyService;
    private final UserService userService;
    private final ApplicationStatusService applicationStatusService;
    private final CompanyService companyService;


    private final ApplicationMapper applicationMapper;


    @Override
    @Transactional
    public ApplicationCreateResponseDto createApplication(Long id, Long userId, ApplicationCreateRequestDto applicationCreateRequestDto) {
        if (!vacancyService.existsVacancyById(id)) {
            throw new VacancyNotFoundException();
        }
        if (userService.findUserById(userId).isEmpty()){
            throw new UserNotFoundException();
        }

        VacancyStatus vacancyStatus = vacancyService.findCurrentVacancyStatusByVacancyId(id);

        if (!vacancyStatus.getVacancyStatusName().equals(VacancyStatusName.PUBLISHED)) {
            throw new VacancyNotOpenedException();
        }
        if (hasUserAlreadyAppliedForVacancy(userId, id)) {
            throw new UserHasAlreadyAppliedException();
        }

        User user = userService.getUserReferenceById(userId);
        Vacancy vacancy = vacancyService.getReferenceById(id);
        ApplicationStatus applicationStatus = applicationStatusService.findApplicationStatusByApplicationStatusName(ApplicationStatusName.NEW).orElseThrow(ApplicationStatusNotFoundException::new);

        Application application = Application.builder()
                .coverLetter(applicationCreateRequestDto.coverLetter())
                .createdAt(LocalDateTime.now())
                .status(applicationStatus).updatedAt(LocalDateTime.now())
                .vacancy(vacancy).user(user).build();
        var saved = applicationRepository.save(application);
        return new ApplicationCreateResponseDto(saved.getId(), applicationStatus.getApplicationStatus().name(), LocalDateTime.now());
    }


    @Override
    public ApplicationCreateResponseDto updateApplication(Long id, Long userId, ApplicationCreateRequestDto applicationCreateRequestDto) {
        if (userService.findUserById(userId).isEmpty())
            throw new UserNotFoundException();
        if (hasUserAlreadyAppliedForVacancy(userId, id)) {
            var applicationEntity = applicationRepository.findApplicationByUser_IdAndVacancy_Id(userId, id).orElseThrow(ApplicationNotFoundException::new);
            applicationMapper.update(applicationEntity, applicationCreateRequestDto);
            applicationEntity.setUpdatedAt(LocalDateTime.now());
            var saved = applicationRepository.save(applicationEntity);
            return new ApplicationCreateResponseDto(saved.getId(), saved.getStatus().getApplicationStatus().name(), saved.getCreatedAt());
        } else throw new ApplicationNotFoundException();
    }

    @Override
    public boolean hasUserAlreadyAppliedForVacancy(Long userId, Long vacancyId) {
        return applicationRepository.existsByUserIdAndVacancy_Id(userId, vacancyId);
    }

    @Override
    public ApplicationStatusUpdateResponseDto updateApplicationStatus(Long applicationId, Long currentUser, ApplicationStatusUpdateRequestDto applicationStatusUpdateRequestDto) {
        if (userService.findUserById(currentUser).isEmpty())
            throw new UserNotFoundException();
        Application entity = applicationRepository.findApplicationById(applicationId).orElseThrow(ApplicationNotFoundException::new);
        if (!companyService.validateCompanyOwnership(entity.getVacancy().getCompany().getId(), currentUser)){
            throw new UserDoesNotBelongToCompanyException();
        }
        var applicationStatus = applicationStatusService.findApplicationStatusByApplicationStatusName(applicationStatusUpdateRequestDto.status()).orElseThrow(ApplicationStatusNotFoundException::new);

        entity.setStatus(applicationStatus);
        entity.setUpdatedAt(LocalDateTime.now());

        var saved = applicationRepository.save(entity);

        return new ApplicationStatusUpdateResponseDto(applicationStatusUpdateRequestDto.status().getValue(), saved.getUpdatedAt());
    }

    @Override
    public Page<ApplicationDto> getAllApplicationsByVacancyId(Long vacancyId, Long currentUserId, Pageable pageable) {
        if (userService.findUserById(currentUserId).isEmpty())
            throw new UserNotFoundException();
        if (vacancyService.existsVacancyById(vacancyId)){
            Long companyId = vacancyService.findCompanyByVacancyId(vacancyId);
            if ( companyId == null || !companyService.validateCompanyOwnership(companyId, currentUserId)){
                throw new UserDoesNotBelongToCompanyException();
            }
        } else throw new VacancyNotFoundException();
        return applicationRepository.getAllByVacancy_Id(vacancyId, pageable).map(applicationMapper::toDto);
    }

}
