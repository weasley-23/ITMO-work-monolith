package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.request.VacancyCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.*;
import com.itmo_work.api_monolith.mappers.VacancyMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.VacancyRepository;
import com.itmo_work.api_monolith.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final CurrencyService currencyService;
    private final CompanyService companyService;
    private final VacancyStatusService vacancyStatusService;

    private final VacancyMapper vacancyMapper;

    private static final Map<VacancyStatusName, Set<VacancyStatusName>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            VacancyStatusName.DRAFT, Set.of(VacancyStatusName.PUBLISHED, VacancyStatusName.CLOSED),
            VacancyStatusName.PUBLISHED, Set.of(VacancyStatusName.DRAFT, VacancyStatusName.CLOSED)
    );

    @Override
    public Page<VacancyResponseDto> getAllPublishedVacancies(Pageable pageable) {
        Page<Vacancy> vacancies = vacancyRepository.getAllPublished(pageable);

        return vacancies.map(v -> new VacancyResponseDto(
                v.getId(),
                v.getTitle(),
                v.getDescription(),
                v.getSalaryFrom(),
                v.getSalaryTo(),
                v.getStatus().getId(),
                v.getCompany().getId(),
                v.getCurrency().getId()
        ));
    }

    @Override
    @Transactional
    public VacancyResponseDto updateAndChangeStatus(Long userId, Long id, VacancyUpdateRequestDto dto, VacancyStatusName newStatus) {

        Vacancy vacancy = getAndValidateVacancy(id);
        Company company = getAndValidateCompany(id);

        if (!companyService.validateCompanyOwnership(company.getId(), userId)) {
            throw new CompanyNotFoundException("User doesn't own this company");
        }

        VacancyStatusName currentStatus = vacancy.getStatus().getVacancyStatusName();

        if (!(currentStatus == VacancyStatusName.DRAFT || currentStatus == VacancyStatusName.PUBLISHED)) {
            throw new InvalidVacancyStatusException("Update available only for DRAFT or PUBLISHED vacancies");
        }

        if (!((currentStatus == VacancyStatusName.DRAFT && newStatus == VacancyStatusName.PUBLISHED) ||
                (currentStatus == VacancyStatusName.PUBLISHED && newStatus == VacancyStatusName.DRAFT))) {
            throw new InvalidVacancyStatusChangeException("Impossible to change status from " + currentStatus + " to " + newStatus);
        }

        vacancyMapper.update(vacancy, dto);

        if (dto.currencyId() != null) {
            Currency currency = currencyService.findCurrencyById(dto.currencyId());
            if (currency == null) {
                throw new CurrencyNotFoundException("Currency with id=" + dto.currencyId() + " not found");
            }
            vacancy.setCurrency(currency);
        }

        validateSalaryBounds(vacancy.getSalaryFrom(), vacancy.getSalaryTo());

        VacancyStatus statusEntity = vacancyStatusService.findByVacancyStatusName(newStatus);
        if (statusEntity == null) {
            throw new InvalidVacancyStatusException("Invalid status: " + newStatus);
        }

        vacancy.setStatus(statusEntity);

        Vacancy saved = vacancyRepository.save(vacancy);

        return buildResponse(saved);
    }



    @Override
    @Transactional
    public VacancyResponseDto updateVacancy(Long userId, Long id, VacancyUpdateRequestDto dto) {

        Vacancy vacancy = getAndValidateVacancy(id);
        Company company = getAndValidateCompany(id);

        if (!companyService.validateCompanyOwnership(company.getId(), userId)) {
            throw new CompanyNotFoundException("User doesn't own this company");
        }

        if (vacancy.getStatus().getVacancyStatusName() != VacancyStatusName.DRAFT &&
                vacancy.getStatus().getVacancyStatusName() != VacancyStatusName.PUBLISHED) {
            throw new InvalidVacancyStatusException("Update available only for DRAFT or PUBLISHED vacancies");
        }

        vacancyMapper.update(vacancy, dto);

        if (dto.currencyId() != null) {
            Currency currency = currencyService.findCurrencyById(dto.currencyId());
            if (currency == null) {
                throw new CurrencyNotFoundException("Currency with id=" + dto.currencyId() + " not found");
            }
            vacancy.setCurrency(currency);
        }

        validateSalaryBounds(vacancy.getSalaryFrom(), vacancy.getSalaryTo());

        Vacancy saved = vacancyRepository.save(vacancy);

        return buildResponse(saved);
    }


    @Override
    @Transactional
    public VacancyResponseDto changeStatus(Long userId, Long id, VacancyStatusName newStatus) {

        Vacancy vacancy = getAndValidateVacancy(id);

        Company company = getAndValidateCompany(id);
        if (!companyService.validateCompanyOwnership(company.getId(), userId)) {
            throw new CompanyNotFoundException("User doesn't own this company");
        }

        VacancyStatusName currentStatus = vacancy.getStatus().getVacancyStatusName();

        var allowedNextStatuses = ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidVacancyStatusChangeException(
                    "Impossible to change status from " + currentStatus + " to " + newStatus
            );
        }

        VacancyStatus statusEntity = vacancyStatusService.findByVacancyStatusName(newStatus);
        if (statusEntity == null) {
            throw new InvalidVacancyStatusException("Invalid status: " + newStatus);
        }

        vacancy.setStatus(statusEntity);

        validateSalaryBounds(vacancy.getSalaryFrom(), vacancy.getSalaryTo());

        Vacancy saved = vacancyRepository.save(vacancy);

        return buildResponse(saved);
    }



    @Override
    @Transactional
    public VacancyResponseDto createVacancy(Long userId, VacancyCreateRequestDto request, VacancyStatusName statusName) {
        Company company = companyService.findCompanyById(request.companyId());
        if (company == null) {
            throw new CompanyNotFoundException("Company id not found");
        }

        if (!companyService.validateCompanyOwnership(company.getId(), userId)) {
            throw new CompanyNotFoundException("User doesn't own this company" );
        }

        Currency currency = currencyService.findCurrencyById(request.currencyId());
        if (currency == null) {
            throw new CurrencyNotFoundException("Currency with id=" + request.currencyId() + " not found");
        }
        VacancyStatus vacancyStatus = vacancyStatusService.findByVacancyStatusName(statusName);

        validateSalaryBounds(request.salaryFrom(), request.salaryTo());

        Vacancy vacancy = Vacancy.builder()
                .title(request.title())
                .description(request.description())
                .salaryFrom(request.salaryFrom())
                .salaryTo(request.salaryTo())
                .createdAt(LocalDateTime.now())
                .company(company)
                .status(vacancyStatus)
                .currency(currency)
                .build();

        Vacancy saved = vacancyRepository.save(vacancy);

        return VacancyResponseDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .salaryFrom(saved.getSalaryFrom())
                .salaryTo(saved.getSalaryTo())
                .statusId(saved.getStatus().getId())
                .companyId(saved.getCompany().getId())
                .currencyId(saved.getCurrency().getId())
                .build();

    }



    @Override
    public Vacancy getReferenceById(Long vacancyId) {
        return vacancyRepository.getReferenceById(vacancyId);
    }

    @Override
    public boolean existsVacancyById(Long id) {
        return vacancyRepository.existsVacanciesById(id);
    }

    @Override
    public VacancyStatus findCurrentVacancyStatusByVacancyId(Long id) {
        var vacancyStatusId = vacancyRepository.findVacancyStatusById(id);
        return vacancyStatusService.findVacancyStatusById(vacancyStatusId);
    }

    private void validateSalaryBounds(Integer salaryFrom, Integer salaryTo) {
        if (salaryFrom == null || salaryFrom < 0) {
            throw new InvalidVacancySalaryException("salary_from must be non-negative");
        }

        if (salaryTo != null && salaryTo < 0) {
            throw new InvalidVacancySalaryException("salary_to must be non-negative");
        }

        if (salaryTo != null && salaryFrom > salaryTo) {
            throw new InvalidVacancySalaryException("salary_from cannot be greater than salary_to");
        }
    }

    @Override
    public Long findCompanyByVacancyId(Long applicationId) {
        return vacancyRepository.findCompanyIdById(applicationId);
    }

    private Company getAndValidateCompany(Long vacancyId) {
        Long companyId = vacancyRepository.findCompanyIdById(vacancyId);
        Company company = companyService.findCompanyById(companyId);

        if (company == null) {
            throw new CompanyNotFoundException("Company with id=" + companyId + " not found");
        }

        return company;
    }

    private Vacancy getAndValidateVacancy(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy with id=" + vacancyId + " not found"));
    }

    private VacancyResponseDto buildResponse(Vacancy saved) {
        return VacancyResponseDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .salaryFrom(saved.getSalaryFrom())
                .salaryTo(saved.getSalaryTo())
                .statusId(saved.getStatus().getId())
                .companyId(saved.getCompany().getId())
                .currencyId(saved.getCurrency().getId())
                .build();
    }
}

