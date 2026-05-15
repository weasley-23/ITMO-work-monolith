package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.dto.request.CompanyRequestDto;
import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.request.UserCreateRequestDto;
import com.itmo_work.api_monolith.dto.response.CompanyResponseDto;
import com.itmo_work.api_monolith.exception.exceptions.CompanyAlreadyExistsException;
import com.itmo_work.api_monolith.exception.exceptions.CompanyNotFoundException;
import com.itmo_work.api_monolith.exception.exceptions.UserNotFoundException;
import com.itmo_work.api_monolith.mappers.CompanyMapper;
import com.itmo_work.api_monolith.model.*;
import com.itmo_work.api_monolith.repository.CompanyRepository;
import com.itmo_work.api_monolith.service.interfaces.CompanyService;
import com.itmo_work.api_monolith.service.interfaces.CompanyStatusService;
import com.itmo_work.api_monolith.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyStatusService companyStatusService;
    private final UserService userService;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        boolean isExists = companyRepository.existsByEmail(companyRequestDto.email());
        if(isExists) throw new CompanyAlreadyExistsException("Компания с таким email уже существует");

        Company company = getCompany(companyRequestDto);
        userService.createUser(new UserCreateRequestDto(companyRequestDto.ownerFullName(), companyRequestDto.ownerEmail(), companyRequestDto.ownerPassword()));
        User owner = userService.findUserByLogin(companyRequestDto.ownerEmail()).orElseThrow(UserNotFoundException::new);

        owner.setCompanies(new ArrayList<>(List.of(company)));
        Company companySaved = companyRepository.save(company);

        return CompanyResponseDto.builder()
                .id(companySaved.getId())
                .name(company.getName())
                .email(company.getEmail())
                .description(company.getDescription())
                .userId(owner.getId())
                .statusMessage("Компания зарегистрирована. Ожидайте проверки администратора.")
                .build();
    }

    @Override
    @Transactional
    public CompanyResponseDto updateCompany(Long id, Long userId, CompanyUpdateRequestDto companyUpdateRequestDto) {
        userService.findUserById(userId).orElseThrow(UserNotFoundException::new);
        if(validateCompanyOwnership(id, userId)){
            Company companyEntity = companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException("Компания с id " + id + " не была найдена"));
            companyMapper.updateCompanyFromDto(companyEntity, companyUpdateRequestDto);
            Company companySaved = companyRepository.save(companyEntity);
            return CompanyResponseDto.builder()
                    .id(companySaved.getId())
                    .name(companySaved.getName())
                    .email(companySaved.getEmail())
                    .description(companySaved.getDescription())
                    .statusMessage("Компания была успешно обновлена")
                    .build();
        }
        else throw new CompanyNotFoundException("Компания не была найдена");
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteCompany(Long id, Long userId){
        User owner = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);
        if(validateCompanyOwnership(id, userId)){
            Company companyEntity = companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException("Компания с id " + id + " не была найдена"));
            CompanyStatus companyStatus = companyStatusService.findCompanyStatusByCompanyStatusName(CompanyStatusName.DELETED).orElseThrow(() -> new EntityNotFoundException("Статус DELETED не найден"));
            companyEntity.setCompanyStatus(companyStatus);
            owner.getCompanies().remove(companyEntity);
            return ResponseEntity.status(HttpStatus.OK).body("Компания была успешно удалена!");
        }
        else throw new CompanyNotFoundException("Компания не была найдена");
    }

    @Override
    public Page<CompanyResponseDto> getAllCompanies(Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        return mapToCompanyResponseDto(companies);
    }

    @Override
    public boolean validateCompanyOwnership(Long companyId, Long userId){
        return companyRepository.existsByIdAndUsers_Id(companyId, userId);
    }

    private Company getCompany(CompanyRequestDto companyRequestDto){
        Company company = new Company();
        company.setName(companyRequestDto.name());
        company.setEmail(companyRequestDto.email());
        company.setDescription(companyRequestDto.description());
        company.setCompanyStatus(companyStatusService.findCompanyStatusByCompanyStatusName(
                (CompanyStatusName.PENDING_VERIFICATION)).orElseThrow(() -> new EntityNotFoundException("Статус PENDING_VERIFICATION не найден")));
        return company;
    }

    @Override
    public Company findCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    }


    private Page<CompanyResponseDto> mapToCompanyResponseDto(Page<Company> companies){

        return companies.map(company -> CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .description(company.getDescription())
                .build());

        }

}

