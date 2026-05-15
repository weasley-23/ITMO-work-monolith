package com.itmo_work.api_monolith.controller;


import com.itmo_work.api_monolith.dto.ApplicationDto;
import com.itmo_work.api_monolith.dto.request.ApplicationCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.ApplicationStatusUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.ApplicationCreateResponseDto;
import com.itmo_work.api_monolith.dto.response.ApplicationStatusUpdateResponseDto;
import com.itmo_work.api_monolith.service.interfaces.ApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationCreateResponseDto> createApplication(@RequestParam Long vacancyId, @RequestParam Long userId, @Valid @RequestBody ApplicationCreateRequestDto applicationCreateRequestDto) {
        return new ResponseEntity<>(applicationService.createApplication(vacancyId, userId, applicationCreateRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{vacancyId}")
    public ResponseEntity<ApplicationCreateResponseDto> updateApplication(@PathVariable Long vacancyId, @RequestParam Long userId, @Valid @RequestBody ApplicationCreateRequestDto applicationCreateRequestDto) {
        return new ResponseEntity<>(applicationService.updateApplication(vacancyId, userId, applicationCreateRequestDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationStatusUpdateResponseDto> updateApplicationStatus(@PathVariable Long id, @RequestParam Long userId, @Valid @RequestBody ApplicationStatusUpdateRequestDto applicationStatusUpdateRequestDto) {
        return new ResponseEntity<>(applicationService.updateApplicationStatus(id, userId, applicationStatusUpdateRequestDto), HttpStatus.OK);
    }

    @GetMapping
    public PagedModel<ApplicationDto> getAllApplicationsForVacancy(@RequestParam Long vacancyId, @RequestParam Long userId, Pageable pageable) {
        return new PagedModel<>(applicationService.getAllApplicationsByVacancyId(vacancyId, userId, pageable));
    }


}
