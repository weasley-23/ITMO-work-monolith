package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.request.VacancyCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.*;
import com.itmo_work.api_monolith.mappers.VacancyMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.VacancyRepository;
import com.itmo_work.api_monolith.service.interfaces.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CompanyService companyService;

    @Mock
    private VacancyStatusService vacancyStatusService;

    @Mock
    private VacancyMapper vacancyMapper;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    private static final String VACANCY_TITLE = "Java Dev";
    private static final String VACANCY_DESCRIPTION = "Middle";
    private static final Integer SALARY_FROM = 1000;
    private static final Integer SALARY_TO = 2000;
    private static final Long CURRENCY_ID = 2L;
    private static final Long COMPANY_ID = 10L;
    private static final Long VACANCY_ID = 99L;
    private static final Long USER_ID = 1L;
    private static final Long VACANCY_STATUS_ID = 5L;

    private static final VacancyCreateRequestDto vacancyCreateRequestDto =
            new VacancyCreateRequestDto(
                    VACANCY_TITLE,
                    VACANCY_DESCRIPTION,
                    SALARY_FROM,
                    SALARY_TO,
                    COMPANY_ID,
                    CURRENCY_ID
            );

    private Company company(Long id) {
        return Company.builder().id(id).build();
    }

    private Currency currency(Long id) {
        return Currency.builder().id(id).build();
    }

    private VacancyStatus status(Long id, VacancyStatusName name) {
        return VacancyStatus.builder().id(id).vacancyStatusName(name).build();
    }

    @Test
    void getAllPublishedVacanciesSuccess() {
        Vacancy v = Vacancy.builder()
                .id(VACANCY_ID)
                .title(VACANCY_TITLE)
                .description(VACANCY_DESCRIPTION)
                .salaryFrom(SALARY_FROM)
                .salaryTo(SALARY_TO)
                .company(company(COMPANY_ID))
                .currency(currency(CURRENCY_ID))
                .status(status(VACANCY_STATUS_ID, VacancyStatusName.PUBLISHED))
                .build();

        Page<Vacancy> page = new PageImpl<>(List.of(v));

        Mockito.when(vacancyRepository.getAllPublished(Mockito.any())).thenReturn(page);

        Page<VacancyResponseDto> result = vacancyService.getAllPublishedVacancies(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        VacancyResponseDto dto = result.getContent().get(0);
        assertEquals(VACANCY_ID, dto.id());
        assertEquals(COMPANY_ID, dto.companyId());
        assertEquals(CURRENCY_ID, dto.currencyId());
    }


    @Test
    void createVacancySuccess() {
        Company company = company(COMPANY_ID);
        Currency currency = currency(CURRENCY_ID);
        VacancyStatus s = status(VACANCY_STATUS_ID, VacancyStatusName.DRAFT);

        Vacancy saved = Vacancy.builder()
                .id(VACANCY_ID).company(company).currency(currency).status(s)
                .salaryFrom(SALARY_FROM).salaryTo(SALARY_TO)
                .title(VACANCY_TITLE)
                .build();

        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);
        Mockito.when(currencyService.findCurrencyById(CURRENCY_ID)).thenReturn(currency);
        Mockito.when(vacancyStatusService.findByVacancyStatusName(VacancyStatusName.DRAFT)).thenReturn(s);

        Mockito.when(vacancyRepository.save(Mockito.any())).thenReturn(saved);

        VacancyResponseDto response = vacancyService.createVacancy(USER_ID, vacancyCreateRequestDto, VacancyStatusName.DRAFT);

        assertEquals(VACANCY_ID, response.id());
        assertEquals(COMPANY_ID, response.companyId());
        assertEquals(CURRENCY_ID, response.currencyId());
        assertEquals(VACANCY_STATUS_ID, response.statusId());
    }

    @Test
    void createVacancyCompanyNotFoundThrowsException() {
        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(null);

        assertThrows(CompanyNotFoundException.class,
                () -> vacancyService.createVacancy(USER_ID, vacancyCreateRequestDto, VacancyStatusName.DRAFT));
    }

    @Test
    void createVacancyInvalidOwnershipThrowsException() {
        Company company = company(COMPANY_ID);

        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(false);

        assertThrows(CompanyNotFoundException.class,
                () -> vacancyService.createVacancy(USER_ID, vacancyCreateRequestDto, VacancyStatusName.DRAFT));
    }

    @Test
    void createVacancyCurrencyNotFoundThrowsException() {
        Company company = company(COMPANY_ID);

        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);
        Mockito.when(currencyService.findCurrencyById(CURRENCY_ID)).thenReturn(null);

        assertThrows(CurrencyNotFoundException.class,
                () -> vacancyService.createVacancy(USER_ID, vacancyCreateRequestDto, VacancyStatusName.DRAFT));
    }

    @Test
    void createVacancyInvalidSalaryBoundsThrowsException() {
        final Integer invalidSalaryFrom = 3000;
        final Integer invalidSalaryTo = 2000;
        VacancyCreateRequestDto invalidVacancyCreateRequestDto = VacancyCreateRequestDto.builder()
                        .companyId(COMPANY_ID)
                        .currencyId(CURRENCY_ID)
                        .salaryFrom(invalidSalaryFrom)
                        .salaryTo(invalidSalaryTo)
                        .build();

        Company company = company(COMPANY_ID);

        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company);
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);
        Mockito.when(currencyService.findCurrencyById(CURRENCY_ID)).thenReturn(currency(CURRENCY_ID));

        assertThrows(InvalidVacancySalaryException.class,
                () -> vacancyService.createVacancy(USER_ID, invalidVacancyCreateRequestDto, VacancyStatusName.DRAFT));
    }

    @Test
    void updateVacancySuccess() {
        final String oldTitle = "Old";
        Vacancy vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .title(oldTitle)
                .salaryFrom(SALARY_FROM)
                .salaryTo(SALARY_TO)
                .status(status(VACANCY_STATUS_ID, VacancyStatusName.DRAFT))
                .company(company(COMPANY_ID))
                .currency(currency(CURRENCY_ID))
                .build();
        final String updatedTitle = "New";
        VacancyUpdateRequestDto dto = VacancyUpdateRequestDto.builder()
                        .title(updatedTitle)
                        .build();


        Mockito.doAnswer(invocation -> {
            Vacancy target = invocation.getArgument(0);
            VacancyUpdateRequestDto source = invocation.getArgument(1);
            target.setTitle(source.title());
            return null;
        }).when(vacancyMapper).update(Mockito.any(), Mockito.any());

        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(vacancyRepository.findCompanyIdById(VACANCY_ID)).thenReturn(COMPANY_ID);
        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company(COMPANY_ID));
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);
        Mockito.when(vacancyRepository.save(Mockito.any())).thenReturn(vacancy);

        VacancyResponseDto response = vacancyService.updateVacancy(USER_ID, VACANCY_ID, dto);

        assertEquals(updatedTitle, response.title());
    }

    @Test
    void updateAndChangeStatusInvalidCurrentStatusThrowsException() {
        Vacancy vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .status(status(1L, VacancyStatusName.CLOSED))
                .company(company(COMPANY_ID))
                .build();

        VacancyUpdateRequestDto dto = VacancyUpdateRequestDto.builder().build();

        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(vacancyRepository.findCompanyIdById(VACANCY_ID)).thenReturn(COMPANY_ID);
        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company(COMPANY_ID));
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);

        assertThrows(InvalidVacancyStatusException.class,
                () -> vacancyService.updateAndChangeStatus(USER_ID, VACANCY_ID, dto, VacancyStatusName.PUBLISHED));
    }

    @Test
    void updateVacancyVacancyNotFoundThrowsException() {
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThrows(VacancyNotFoundException.class,
                () -> vacancyService.updateVacancy(USER_ID, VACANCY_ID, VacancyUpdateRequestDto.builder().build()));
    }

    @Test
    void updateVacancyChangeCurrencySuccess() {
        Vacancy vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .status(status(VACANCY_STATUS_ID, VacancyStatusName.DRAFT))
                .company(company(COMPANY_ID))
                .currency(currency(CURRENCY_ID))
                .salaryFrom(SALARY_FROM)
                .salaryTo(SALARY_TO)
                .build();

        VacancyUpdateRequestDto dto = VacancyUpdateRequestDto.builder()
                .currencyId(CURRENCY_ID).build();

        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(vacancyRepository.findCompanyIdById(VACANCY_ID)).thenReturn(COMPANY_ID);
        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company(COMPANY_ID));
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);
        Mockito.when(currencyService.findCurrencyById(CURRENCY_ID)).thenReturn(currency(CURRENCY_ID));
        Mockito.when(vacancyRepository.save(Mockito.any())).thenReturn(vacancy);

        VacancyResponseDto response = vacancyService.updateVacancy(USER_ID, VACANCY_ID, dto);

        assertEquals(CURRENCY_ID, response.currencyId());
    }

    @Test
    void changeStatusInvalidTransitionThrowsException() {

        Vacancy vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .company(company(COMPANY_ID))
                .status(status(VACANCY_STATUS_ID, VacancyStatusName.CLOSED))
                .build();

        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(vacancyRepository.findCompanyIdById(VACANCY_ID)).thenReturn(COMPANY_ID);
        Mockito.when(companyService.findCompanyById(COMPANY_ID)).thenReturn(company(COMPANY_ID));
        Mockito.when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);

        assertThrows(InvalidVacancyStatusChangeException.class,
                () -> vacancyService.changeStatus(USER_ID, VACANCY_ID, VacancyStatusName.PUBLISHED));
    }

    @Test
    void existsVacancyByIdTrue() {
        Mockito.when(vacancyRepository.existsVacanciesById(VACANCY_ID)).thenReturn(true);
        assertTrue(vacancyService.existsVacancyById(VACANCY_ID));
    }

    @Test
    void existsVacancyByIdFalse() {
        Mockito.when(vacancyRepository.existsVacanciesById(VACANCY_ID)).thenReturn(false);
        assertFalse(vacancyService.existsVacancyById(VACANCY_ID));
    }

    @Test
    void findCurrentVacancyStatus_success() {
        Mockito.when(vacancyRepository.findVacancyStatusById(VACANCY_ID)).thenReturn(7L);
        VacancyStatus s = status(7L, VacancyStatusName.PUBLISHED);
        Mockito.when(vacancyStatusService.findVacancyStatusById(7L)).thenReturn(s);

        VacancyStatus result = vacancyService.findCurrentVacancyStatusByVacancyId(VACANCY_ID);

        assertEquals(7L, result.getId());
    }
}
