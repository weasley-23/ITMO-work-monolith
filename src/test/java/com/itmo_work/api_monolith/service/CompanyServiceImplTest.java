package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.request.CompanyRequestDto;
import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.CompanyResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.CompanyAlreadyExistsException;
import com.itmo_work.api_monolith.exception.exceptions.CompanyNotFoundException;
import com.itmo_work.api_monolith.exception.exceptions.UserNotFoundException;
import com.itmo_work.api_monolith.mappers.CompanyMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.CompanyRepository;
import com.itmo_work.api_monolith.service.interfaces.CompanyStatusService;
import com.itmo_work.api_monolith.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyStatusService companyStatusService;

    @Mock
    private UserService userService;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;

    private User owner;

    private static final String COMPANY_NAME = "Test Company";
    private static final String COMPANY_EMAIL = "test@example.com";
    private static final String COMPANY_DESCRIPTION = "Test description";
    private static final String COMPANY_OWNER_FULL_NAME = "John Doe";
    private static final String COMPANY_OWNER_EMAIL = "owner@example.com";
    private static final String COMPANY_OWNER_PASSWORD = "123456";

    private static final Long COMPANY_ID = 1L;
    private static final Long OWNER_ID = 42L;

    private static final CompanyRequestDto companyRequestDto =
            new CompanyRequestDto(
                    COMPANY_NAME,
                    COMPANY_EMAIL,
                    COMPANY_DESCRIPTION,
                    COMPANY_OWNER_FULL_NAME,
                    COMPANY_OWNER_EMAIL,
                    COMPANY_OWNER_PASSWORD);

    private static final CompanyUpdateRequestDto companyUpdateRequestDto =
            new CompanyUpdateRequestDto(COMPANY_NAME, COMPANY_EMAIL, COMPANY_DESCRIPTION);


    @Test
    public void createCompanySuccessfully(){
        when(companyRepository.existsByEmail(anyString())).thenReturn(false);
        when(userService.findUserByLogin(anyString())).thenReturn(Optional.of(new User()));
        when(companyStatusService.findCompanyStatusByCompanyStatusName(CompanyStatusName.PENDING_VERIFICATION)).thenReturn(Optional.of(new CompanyStatus()));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv ->{
            Company c = inv.getArgument(0);
            c.setId(COMPANY_ID);
            return c;
                }
        );

        CompanyResponseDto response = companyService.createCompany(companyRequestDto);

        assertThat(response.id()).isEqualTo(COMPANY_ID);
        assertThat(response.name()).isEqualTo(COMPANY_NAME);

        verify(companyRepository, times(1)).save(any(Company.class));

    }


    @Test
    public void createCompanyWithExistsEmailCompanyShouldThrowException(){
        when(companyRepository.existsByEmail(anyString())).thenReturn(true);
        assertThatThrownBy(() -> companyService.createCompany(companyRequestDto))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("Компания с таким email уже существует");
    }


    @Test
    public void createCompanyWithNotExistsUserShouldThrowException(){
        when(companyRepository.existsByEmail(anyString())).thenReturn(false);
        when(companyStatusService.findCompanyStatusByCompanyStatusName(CompanyStatusName.PENDING_VERIFICATION)).thenReturn(Optional.of(new CompanyStatus()));
        when(userService.findUserByLogin(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.createCompany(companyRequestDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateCompanyShouldUpdateSuccessfully() {
        company = new Company();
        owner = new User();

        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(true);
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(new Company());

        CompanyResponseDto companyResponseDto = companyService.updateCompany(COMPANY_ID, OWNER_ID, companyUpdateRequestDto);

        assertThat(companyResponseDto).isNotNull();
        assertThat(companyResponseDto.statusMessage()).isEqualTo("Компания была успешно обновлена");
        verify(companyMapper).updateCompanyFromDto(company, companyUpdateRequestDto);
        verify(companyRepository).save(company);

    }

    @Test
    void updateCompanyShouldThrowIfUserNotFound() {
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> companyService.updateCompany(COMPANY_ID, OWNER_ID,  companyUpdateRequestDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateCompanyShouldThrowIfOwnershipInvalid() {
        owner = new User();
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> companyService.updateCompany(COMPANY_ID, OWNER_ID, companyUpdateRequestDto))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessage("Компания не была найдена");
    }

    @Test
    void updateCompanyShouldThrowIfCompanyNotFound() {
        owner = new User();
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(true);
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> companyService.updateCompany(COMPANY_ID, OWNER_ID, companyUpdateRequestDto))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessage("Компания с id " + COMPANY_ID + " не была найдена");
    }

    @Test
    public void deleteCompanyShouldDeleteSuccessfully(){
        company = new Company();
        owner = new User();
        List<Company> companies = new ArrayList<>(List.of(company));
        owner.setCompanies(companies);

        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(true);
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(companyStatusService.findCompanyStatusByCompanyStatusName(CompanyStatusName.DELETED)).thenReturn(Optional.of(new CompanyStatus()));

        ResponseEntity<?> deleteResponseDto = companyService.deleteCompany(COMPANY_ID, OWNER_ID);
        assertThat(deleteResponseDto).isNotNull();
        assertThat(deleteResponseDto.getBody()).isEqualTo("Компания была успешно удалена!");
        assertThat(owner.getCompanies()).isEmpty();

    }

    @Test
    public void deleteCompanyShouldThrowIfUserNotFound(){
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, OWNER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void deleteCompanyShouldThrowIfOwnershipInvalid(){
        owner = new User();
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(false);

        assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, OWNER_ID))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("Компания не была найдена");


    }

    @Test
    public void deleteCompanyShouldThrowIfCompanyNotFound(){
        owner = new User();
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(true);
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, OWNER_ID))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("Компания с id " + COMPANY_ID + " не была найдена");
    }

    @Test
    public void deleteCompanyShouldThrowIfCompanyStatusNameIsNotFound(){
        company = new Company();
        owner = new User();
        when(userService.findUserById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(companyRepository.existsByIdAndUsers_Id(COMPANY_ID, OWNER_ID)).thenReturn(true);
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(companyStatusService.findCompanyStatusByCompanyStatusName(CompanyStatusName.DELETED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, OWNER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Статус DELETED не найден");
    }


}
