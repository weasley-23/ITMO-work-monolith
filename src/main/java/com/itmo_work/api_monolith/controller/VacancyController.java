package com.itmo_work.api_monolith.controller;

import com.itmo_work.api_monolith.dto.request.VacancyCreateRequestDto;
import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.model.VacancyStatusName;
import com.itmo_work.api_monolith.service.interfaces.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/draft")
    public ResponseEntity<VacancyResponseDto> createDraftVacancy(
            @RequestParam Long userId,
            @RequestBody @Valid VacancyCreateRequestDto request) {

        VacancyResponseDto response = vacancyService.createVacancy(userId, request, VacancyStatusName.DRAFT);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/publish")
    public ResponseEntity<VacancyResponseDto> createPublishedVacancy(
            @RequestParam Long userId,
            @RequestBody @Valid VacancyCreateRequestDto request) {

        VacancyResponseDto response = vacancyService.createVacancy(userId, request, VacancyStatusName.PUBLISHED);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<VacancyResponseDto> updateVacancy(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid VacancyUpdateRequestDto dto) {

        return ResponseEntity.ok(vacancyService.updateVacancy(userId, id, dto));
    }

    @PatchMapping("/{id}/change-status")
    public ResponseEntity<VacancyResponseDto> changeStatus(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestParam VacancyStatusName newStatus) {

        return ResponseEntity.ok(vacancyService.changeStatus(userId, id, newStatus));
    }

    @PatchMapping("/{id}/update-and-change-status")
    public ResponseEntity<VacancyResponseDto> updateAndChangeStatus(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid VacancyUpdateRequestDto dto,
            @RequestParam VacancyStatusName newStatus) {

        return ResponseEntity.ok(vacancyService.updateAndChangeStatus(userId, id, dto, newStatus));
    }

    @GetMapping
    public PagedModel<VacancyResponseDto> getAllPublishedVacancies(Pageable pageable) {
        return new PagedModel<>(vacancyService.getAllPublishedVacancies(pageable));
    }
}
