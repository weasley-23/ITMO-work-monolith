package com.itmo_work.api_monolith.controller;

import com.itmo_work.api_monolith.dto.request.CompanyRequestDto;
import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.CompanyResponseDto;
import com.itmo_work.api_monolith.service.interfaces.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedModel;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/auth/register-company")
    public ResponseEntity<?> createCompany(@RequestBody @Valid CompanyRequestDto companyCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(companyCreateDto));
    }

    @PatchMapping("/update-company/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable Long companyId, @RequestParam Long userId, @RequestBody @Valid CompanyUpdateRequestDto companyUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.updateCompany(companyId, userId, companyUpdateRequestDto));
    }

    @PostMapping("/delete-company/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long companyId, @RequestParam Long userId) {
        return companyService.deleteCompany(companyId, userId);
    }

    @GetMapping
    public PagedModel<CompanyResponseDto> getAllCompanies(Pageable pageable){
        return new PagedModel<>(companyService.getAllCompanies(pageable));
    }
}
