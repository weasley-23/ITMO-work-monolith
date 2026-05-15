package com.itmo_work.api_monolith.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.itmo_work.api_monolith.dto.request.CompanyRequestDto;
import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.model.CompanyStatusName;
import com.itmo_work.api_monolith.repository.CompanyRepository;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

    }

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String COMPANY_NAME = "IntegrationTest Company";
    private static final String COMPANY_EMAIL = "it@company.com";
    private static final String COMPANY_DESCRIPTION = "Integration test company";
    private static final String COMPANY_OWNER_FULL_NAME = "Owner Test";
    private static final String COMPANY_OWNER_EMAIL = "owner@it.com";
    private static final String COMPANY_OWNER_PASSWORD = "123456";

    private static final CompanyRequestDto companyRequestDto =
            new CompanyRequestDto(
                    COMPANY_NAME,
                    COMPANY_EMAIL,
                    COMPANY_DESCRIPTION,
                    COMPANY_OWNER_FULL_NAME,
                    COMPANY_OWNER_EMAIL,
                    COMPANY_OWNER_PASSWORD);

    @Test
    public void createCompanyShouldPersistAndReturn201() throws Exception {
        mockMvc.perform(post("/api/companies/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequestDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath(("$.name")).value("IntegrationTest Company"))
                        .andExpect(jsonPath(("$.email")).value("it@company.com"))
                        .andExpect(jsonPath(("$.description")).value("Integration test company"));


        assertThat(companyRepository.existsByEmail("it@company.com")).isTrue();
    }

    @Test
    public void updateCompanyShouldPersistAndReturn200() throws Exception {
        var res = mockMvc.perform(post("/api/companies/auth/register-company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequestDto))).andReturn();

        String responseBody = res.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long companyId = jsonNode.get("id").asLong();
        String userId = jsonNode.get("user_id").asText();

        CompanyUpdateRequestDto updateDto = CompanyUpdateRequestDto.builder()
                .name("Updated IntegrationTest Company")
                .description("Updated IntegrationTest Company")
                .build();

        mockMvc.perform(patch("/api/companies/update-company/{companyId}", companyId)
                .contentType(MediaType.APPLICATION_JSON).param("userId", userId)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.name")).value("Updated IntegrationTest Company"))
                .andExpect(jsonPath(("$.description")).value("Updated IntegrationTest Company"));
    }

    @Test
    public void deleteCompanyShouldPersistAndReturn200() throws Exception {
        var res = mockMvc.perform(post("/api/companies/auth/register-company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequestDto))).andReturn();

        String responseBody = res.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long companyId = jsonNode.get("id").asLong();
        String userId = jsonNode.get("user_id").asText();

        mockMvc.perform(post("/api/companies/delete-company/{companyId}", companyId)
                .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", userId))
                .andExpect(status().isOk());

        assertThat(companyRepository.findById(companyId).get().getCompanyStatus().getStatusName()).isEqualTo(CompanyStatusName.DELETED);

    }
}
