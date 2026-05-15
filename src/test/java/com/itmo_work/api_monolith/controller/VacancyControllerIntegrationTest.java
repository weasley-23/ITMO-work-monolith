package com.itmo_work.api_monolith.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo_work.api_monolith.dto.request.VacancyCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VacancyControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private CompanyStatusRepository companyStatusRepository;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private CurrencyRepository currencyRepository;
    @Autowired private VacancyRepository vacancyRepository;
    @Autowired private ObjectMapper objectMapper;

    private Long userId;
    private Long companyId;
    private Long currencyId;

    private static final String VACANCY_TITLE = "Java Dev";
    private static final String VACANCY_DESCRIPTION = "Middle";
    private static final Integer SALARY_FROM = 1000;
    private static final Integer SALARY_TO = 2000;
    private static final Long CURRENCY_ID = 1L;
    private static final String FULL_USER_NAME = "Test Owner";
    private static final String USER_EMAIL = "owner@test.com";
    private static final String USER_PASSWORD = "12345";
    public static final String COMPANY_NAME = "TestCo";
    public static final String COMPANY_EMAIL = "test@test.com";
    public static final String COMPANY_DESCRIPTION = "desc";

    private VacancyCreateRequestDto vacancyCreateRequestDto;

    @BeforeEach
    void setup() {

        CompanyStatus status = companyStatusRepository.findCompanyStatusByStatusName(CompanyStatusName.APPROVED).get();
        User user = new User();
        user.setFullName(FULL_USER_NAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        userRepository.save(user);

        Company company = new Company();
        company.setName(COMPANY_NAME);
        company.setEmail(COMPANY_EMAIL);
        company.setDescription(COMPANY_DESCRIPTION);
        company.setCompanyStatus(status);
        companyRepository.save(company);

        user.setCompanies(new ArrayList<>(List.of(company)));
        company.setUsers(new ArrayList<>(List.of(user)));
        userRepository.save(user);
        companyRepository.save(company);

        Currency currency = currencyRepository.findById(CURRENCY_ID).get();

        this.userId = user.getId();
        this.companyId = company.getId();
        this.currencyId = currency.getId();
        vacancyCreateRequestDto = VacancyCreateRequestDto.builder()
                .title(VACANCY_TITLE)
                .description(VACANCY_DESCRIPTION)
                .salaryFrom(SALARY_FROM)
                .salaryTo(SALARY_TO)
                .currencyId(CURRENCY_ID)
                .companyId(companyId)
                .build();

    }

    @Test
    void createDraftVacancyShouldReturn201() throws Exception {

        mockMvc.perform(post("/api/vacancies/draft")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(VACANCY_TITLE))
                .andExpect(jsonPath("$.salary_from").value(SALARY_FROM));
    }

    @Test
    void updateVacancyShouldReturn200() throws Exception {

        var result = mockMvc.perform(post("/api/vacancies/draft")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyCreateRequestDto)))
                .andReturn();

        Long vacancyId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        final String updatedTitle = "Senior Java Dev";
        final String updatedDescription = "Updated desc";
        final Integer updatedSalaryFrom = 1500;
        final Integer updatedSalaryTo = 2500;

        VacancyUpdateRequestDto update = VacancyUpdateRequestDto.builder()
                .title(updatedTitle)
                .description(updatedDescription)
                .salaryFrom(updatedSalaryFrom)
                .salaryTo(updatedSalaryTo)
                .build();


        mockMvc.perform(patch("/api/vacancies/{id}/update", vacancyId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.salary_from").value(updatedSalaryFrom));
    }

    @Test
    void changeStatusShouldReturn200() throws Exception {

        var result = mockMvc.perform(post("/api/vacancies/draft")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyCreateRequestDto)))
                .andReturn();

        Long vacancyId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/vacancies/{id}/change-status", vacancyId)
                        .param("userId", userId.toString())
                        .param("newStatus", "PUBLISHED"))
                .andExpect(status().isOk());

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow();

        assertThat(vacancy.getStatus().getVacancyStatusName())
                .isEqualTo(VacancyStatusName.PUBLISHED);
    }
}
