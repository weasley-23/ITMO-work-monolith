package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.ApplicationDto;
import com.itmo_work.api_monolith.dto.request.ApplicationCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.ApplicationStatusUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.ApplicationCreateResponseDto;
import com.itmo_work.api_monolith.dto.response.ApplicationStatusUpdateResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.*;
import com.itmo_work.api_monolith.mappers.ApplicationMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.ApplicationRepository;
import com.itmo_work.api_monolith.service.interfaces.ApplicationStatusService;
import com.itmo_work.api_monolith.service.interfaces.CompanyService;
import com.itmo_work.api_monolith.service.interfaces.UserService;
import com.itmo_work.api_monolith.service.interfaces.VacancyService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private VacancyService vacancyService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationStatusService applicationStatusService;

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private CompanyService companyService;



    @InjectMocks
    private ApplicationServiceImpl applicationServiceImpl;

    private static final long USER_ID = 10L;
    private static final long VACANCY_ID = 1050L;
    private static final long APPLICATION_ID = 321L;
    private static final Long COMPANY_ID = 432L;



    @Nested
    class CreateApplicationTests{
        @Test
        void createApplicationWhenVacancyExistsAndUserHasNotApplied(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            VacancyStatus vacancyStatus = mock(VacancyStatus.class);
            when(vacancyStatus.getVacancyStatusName()).thenReturn(VacancyStatusName.PUBLISHED);
            when(vacancyService.findCurrentVacancyStatusByVacancyId(VACANCY_ID)).thenReturn(vacancyStatus);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(false);

            User userRef = mock(User.class);
            Vacancy vacancyRef = mock(Vacancy.class);
            when(userService.getUserReferenceById(USER_ID)).thenReturn(userRef);
            when(vacancyService.getReferenceById(VACANCY_ID)).thenReturn(vacancyRef);

            ApplicationStatus newStatus = mock(ApplicationStatus.class);
            when(newStatus.getApplicationStatus()).thenReturn(ApplicationStatusName.NEW);
            when(applicationStatusService.findApplicationStatusByApplicationStatusName(ApplicationStatusName.NEW))
                    .thenReturn(Optional.of(newStatus));

            Application saved = mock(Application.class);
            when(saved.getId()).thenReturn(APPLICATION_ID);
            when(applicationRepository.save(any(Application.class))).thenReturn(saved);

            ApplicationCreateRequestDto req = new ApplicationCreateRequestDto("Cover letter text");

            ApplicationCreateResponseDto resp = applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, req);

            assertNotNull(resp);
            assertEquals(APPLICATION_ID, resp.id());
            assertEquals(ApplicationStatusName.NEW.name(), resp.status());
            assertNotNull(resp.createdAt());

            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        void createApplicationWhenVacancyNotFound(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(false);

            assertThrows(VacancyNotFoundException.class, () ->
                    applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );

            verifyNoInteractions(userService, applicationStatusService, applicationRepository);
        }

        @Test
        void createApplicationWhenUserNotFound(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            when(userService.findUserById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );

            verifyNoInteractions(applicationStatusService, applicationRepository);
        }

        @Test
        void createApplicationWhenVacancyNotOpened(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            VacancyStatus vacancyStatus = mock(VacancyStatus.class);
            when(vacancyStatus.getVacancyStatusName()).thenReturn(VacancyStatusName.DRAFT);
            when(vacancyService.findCurrentVacancyStatusByVacancyId(VACANCY_ID)).thenReturn(vacancyStatus);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            assertThrows(VacancyNotOpenedException.class, () ->
                    applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );

            verify(applicationRepository, never()).save(any());
        }

        @Test
        void createApplicationWhenUserHasAlreadyApplied(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            VacancyStatus vacancyStatus = mock(VacancyStatus.class);
            when(vacancyStatus.getVacancyStatusName()).thenReturn(VacancyStatusName.PUBLISHED);
            when(vacancyService.findCurrentVacancyStatusByVacancyId(VACANCY_ID)).thenReturn(vacancyStatus);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(true);

            assertThrows(UserHasAlreadyAppliedException.class, () ->
                    applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );

            verify(applicationRepository, never()).save(any());
        }

        @Test
        void createApplicationWhenApplicationStatusNotFound(){
            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            VacancyStatus vacancyStatus = mock(VacancyStatus.class);
            when(vacancyStatus.getVacancyStatusName()).thenReturn(VacancyStatusName.PUBLISHED);
            when(vacancyService.findCurrentVacancyStatusByVacancyId(VACANCY_ID)).thenReturn(vacancyStatus);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(false);

            when(applicationStatusService.findApplicationStatusByApplicationStatusName(ApplicationStatusName.NEW))
                    .thenReturn(Optional.empty());

            assertThrows(ApplicationStatusNotFoundException.class, () ->
                    applicationServiceImpl.createApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );

            verify(applicationRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateApplicationTest{
        @Test
        void updateApplicationWhenUserHasAlreadyApplied(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(true);

            Application applicationEntity = mock(Application.class);
            ApplicationStatus status = mock(ApplicationStatus.class);
            when(status.getApplicationStatus()).thenReturn(ApplicationStatusName.NEW);

            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

            when(applicationRepository.findApplicationByUser_IdAndVacancy_Id(USER_ID, VACANCY_ID))
                    .thenReturn(Optional.of(applicationEntity));

            Application saved = mock(Application.class);
            when(saved.getId()).thenReturn(APPLICATION_ID);
            when(saved.getStatus()).thenReturn(status);
            when(saved.getCreatedAt()).thenReturn(createdAt);
            when(applicationRepository.save(applicationEntity)).thenReturn(saved);

            ApplicationCreateRequestDto req = new ApplicationCreateRequestDto("new letter");

            ApplicationCreateResponseDto resp = applicationServiceImpl.updateApplication(VACANCY_ID, USER_ID, req);

            assertEquals(APPLICATION_ID, resp.id());
            assertEquals(ApplicationStatusName.NEW.name(), resp.status());
            assertEquals(createdAt, resp.createdAt());

            verify(applicationMapper).update(applicationEntity, req);
            verify(applicationEntity).setUpdatedAt(any(LocalDateTime.class));
            verify(applicationRepository).save(applicationEntity);
        }

        @Test
        void updateApplicationWhenNoExistingApplication(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(false);

            assertThrows(ApplicationNotFoundException.class, () ->
                    applicationServiceImpl.updateApplication(VACANCY_ID, USER_ID, new ApplicationCreateRequestDto("x"))
            );
        }

        @Test
        void updateApplicationWhenUserNotFound(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () ->
                    applicationServiceImpl.updateApplication(VACANCY_ID,USER_ID, new ApplicationCreateRequestDto("any"))
            );

            verifyNoInteractions(applicationRepository, applicationMapper);
        }
    }

    @Nested
    class HasUserAlreadyAppliedTests {
        @Test
        void delegatesToRepository() {
            when(applicationRepository.existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID)).thenReturn(true);

            boolean result = applicationServiceImpl.hasUserAlreadyAppliedForVacancy(USER_ID, VACANCY_ID);

            assertTrue(result);
            verify(applicationRepository).existsByUserIdAndVacancy_Id(USER_ID, VACANCY_ID);
        }
    }

    @Nested
    class UpdateApplicationStatusTests {
        @Test
        void updateApplicationStatusSuccess(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            Application entity = mock(Application.class);
            Vacancy vacancy = mock(Vacancy.class);
            Company company = mock(Company.class);
            when(entity.getVacancy()).thenReturn(vacancy);
            when(vacancy.getCompany()).thenReturn(company);
            when(company.getId()).thenReturn(COMPANY_ID);

            when(applicationRepository.findApplicationById(APPLICATION_ID)).thenReturn(Optional.of(entity));

            when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);

            ApplicationStatus desired = mock(ApplicationStatus.class);
            when(applicationStatusService.findApplicationStatusByApplicationStatusName(ApplicationStatusName.ACCEPTED))
                    .thenReturn(Optional.of(desired));

            LocalDateTime now = LocalDateTime.now();
            when(entity.getUpdatedAt()).thenReturn(now);
            when(applicationRepository.save(entity)).thenReturn(entity);


            ApplicationStatusUpdateRequestDto req = new ApplicationStatusUpdateRequestDto(ApplicationStatusName.ACCEPTED);
            ApplicationStatusUpdateResponseDto resp = applicationServiceImpl.updateApplicationStatus(APPLICATION_ID, USER_ID, req);

            assertEquals(ApplicationStatusName.ACCEPTED.getValue(), resp.status());
            assertNotNull(resp.updatedAt());

            verify(entity).setStatus(desired);
            verify(entity).setUpdatedAt(any(LocalDateTime.class));
            verify(applicationRepository).save(entity);
        }

        @Test
        void updateApplicationWhenUserNotFound() {
            when(userService.findUserById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    applicationServiceImpl.updateApplicationStatus(APPLICATION_ID, USER_ID,
                            new ApplicationStatusUpdateRequestDto(ApplicationStatusName.ACCEPTED))
            );

            verifyNoInteractions(applicationRepository, companyService, applicationStatusService);
        }

        @Test
        void updateApplicationWhenUserDoesNotBelongToCompany(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            Application entity = mock(Application.class);
            Vacancy vacancy = mock(Vacancy.class);
            Company company = mock(Company.class);
            when(entity.getVacancy()).thenReturn(vacancy);
            when(vacancy.getCompany()).thenReturn(company);
            when(company.getId()).thenReturn(COMPANY_ID);

            when(applicationRepository.findApplicationById(APPLICATION_ID)).thenReturn(Optional.of(entity));

            when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(false);

            assertThrows(UserDoesNotBelongToCompanyException.class, () ->
                    applicationServiceImpl.updateApplicationStatus(APPLICATION_ID, USER_ID,
                            new ApplicationStatusUpdateRequestDto(ApplicationStatusName.ACCEPTED))
            );

            verify(applicationStatusService, never()).findApplicationStatusByApplicationStatusName(any());
            verify(applicationRepository, never()).save(any());
        }

        @Test
        void updateApplicationWhenApplicationNotFound(){
            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));
            when(applicationRepository.findApplicationById(APPLICATION_ID)).thenReturn(Optional.empty());

            assertThrows(ApplicationNotFoundException.class, () ->
                    applicationServiceImpl.updateApplicationStatus(APPLICATION_ID, USER_ID,
                            new ApplicationStatusUpdateRequestDto(ApplicationStatusName.REJECTED))
            );

            verifyNoInteractions(companyService, applicationStatusService);
        }

        @Test
        void updateApplicationWhenApplicationStatusNotFound(){


            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            Application entity = mock(Application.class);
            Vacancy vacancy = mock(Vacancy.class);
            Company company = mock(Company.class);
            when(entity.getVacancy()).thenReturn(vacancy);
            when(vacancy.getCompany()).thenReturn(company);
            when(company.getId()).thenReturn(COMPANY_ID);

            when(applicationRepository.findApplicationById(APPLICATION_ID)).thenReturn(Optional.of(entity));

            when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);

            when(applicationStatusService.findApplicationStatusByApplicationStatusName(ApplicationStatusName.REJECTED))
                    .thenReturn(Optional.empty());

            assertThrows(ApplicationStatusNotFoundException.class, () ->
                    applicationServiceImpl.updateApplicationStatus(APPLICATION_ID, USER_ID,
                            new ApplicationStatusUpdateRequestDto(ApplicationStatusName.REJECTED))
            );
            verify(applicationRepository, never()).save(any());
        }

    }

    @Nested
    class GetAllApplicationsByVacancyIdTests {
        @Test
        void getAllApplicationsByVacancyIdSuccess() {
            Pageable pageable = PageRequest.of(0, 20);
            Application e1 = mock(Application.class);
            Application e2 = mock(Application.class);
            Page<Application> page = new PageImpl<>(List.of(e1, e2));

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));


            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            when(vacancyService.findCompanyByVacancyId(VACANCY_ID)).thenReturn(COMPANY_ID);
            when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(true);

            when(applicationRepository.getAllByVacancy_Id(VACANCY_ID, pageable)).thenReturn(page);

            ApplicationDto d1 = mock(ApplicationDto.class);
            ApplicationDto d2 = mock(ApplicationDto.class);
            when(applicationMapper.toDto(e1)).thenReturn(d1);
            when(applicationMapper.toDto(e2)).thenReturn(d2);

            Page<ApplicationDto> result = applicationServiceImpl.getAllApplicationsByVacancyId(VACANCY_ID, USER_ID, pageable);

            assertEquals(List.of(d1, d2), result.getContent());
            verify(applicationRepository).getAllByVacancy_Id(VACANCY_ID, pageable);
        }

        @Test
        void getAllApplicationsByVacancyIdWhenUserDoesNotBelongToCompany(){
            Pageable pageable = PageRequest.of(0, 20);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(true);
            when(vacancyService.findCompanyByVacancyId(VACANCY_ID)).thenReturn(COMPANY_ID);
            when(companyService.validateCompanyOwnership(COMPANY_ID, USER_ID)).thenReturn(false);

            assertThrows(UserDoesNotBelongToCompanyException.class, () ->
                    applicationServiceImpl.getAllApplicationsByVacancyId(VACANCY_ID, USER_ID, pageable)
            );

            verify(applicationRepository, never()).getAllByVacancy_Id(anyLong(), any());
        }

        @Test
        void getAllApplicationsByVacancyIdWhenVacancyNotFound(){
            Pageable pageable = PageRequest.of(0, 20);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.of(mock(User.class)));

            when(vacancyService.existsVacancyById(VACANCY_ID)).thenReturn(false);

            assertThrows(VacancyNotFoundException.class, () ->
                    applicationServiceImpl.getAllApplicationsByVacancyId(VACANCY_ID, USER_ID, pageable)
            );

            verify(vacancyService).existsVacancyById(VACANCY_ID);
            verifyNoInteractions(companyService, applicationRepository, applicationMapper);
        }

        @Test
        void getApplicationsByVacancyIdWhenUserNotFound(){
            Pageable pageable = PageRequest.of(0, 20);

            when(userService.findUserById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () ->
                    applicationServiceImpl.getAllApplicationsByVacancyId(VACANCY_ID, USER_ID, pageable)
            );

            verifyNoInteractions(vacancyService, companyService, applicationRepository, applicationMapper);
        }
    }


}