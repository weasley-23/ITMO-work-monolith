package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.dto.request.CompanyRequestDto;
import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.CompanyResponseDto;
import com.itmo_work.api_monolith.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompanyService {
    Company findCompanyById(Long companyId);
    CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto);
    Page<CompanyResponseDto> getAllCompanies(Pageable pageable);

    CompanyResponseDto updateCompany(Long companyId, Long userId, CompanyUpdateRequestDto companyUpdateRequestDto);

    ResponseEntity<?> deleteCompany(Long id, Long userId);
    boolean validateCompanyOwnership(Long companyId, Long userId);
}
