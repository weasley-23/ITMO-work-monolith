package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.exception.exceptions.VacancyStatusNotFoundException;
import com.itmo_work.api_monolith.model.VacancyStatus;
import com.itmo_work.api_monolith.model.VacancyStatusName;
import com.itmo_work.api_monolith.repository.VacancyStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyStatusServiceImplTest {

    @Mock
    private VacancyStatusRepository vacancyStatusRepository;
    @InjectMocks
    private VacancyStatusServiceImpl vacancyStatusService;
    private VacancyStatus draftStatus;

    @BeforeEach
    void setup() {
        draftStatus = new VacancyStatus(1L, VacancyStatusName.DRAFT);
    }

    @Nested
    class FindByVacancyStatusNameTest {

        @Test
        void findByVacancyStatusNameWhenSuccess() {
            when(vacancyStatusRepository.findByVacancyStatusName(VacancyStatusName.DRAFT))
                    .thenReturn(Optional.of(draftStatus));

            VacancyStatus result = vacancyStatusService.findByVacancyStatusName(VacancyStatusName.DRAFT);

            assertThat(result).isEqualTo(draftStatus);
            verify(vacancyStatusRepository).findByVacancyStatusName(VacancyStatusName.DRAFT);
            verifyNoMoreInteractions(vacancyStatusRepository);
        }

        @Test
        void findByVacancyStatusNameWhenError() {
            when(vacancyStatusRepository.findByVacancyStatusName(VacancyStatusName.DRAFT))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    vacancyStatusService.findByVacancyStatusName(VacancyStatusName.DRAFT)
            ).isInstanceOf(VacancyStatusNotFoundException.class);
            verify(vacancyStatusRepository).findByVacancyStatusName(VacancyStatusName.DRAFT);
            verifyNoMoreInteractions(vacancyStatusRepository);
        }
    }

    @Nested
    class FindVacancyStatusByIdTest {

        @Test
        void findByVacancyStatusNameWhenSuccess() {
            when(vacancyStatusRepository.findVacancyStatusById(1L))
                    .thenReturn(Optional.of(draftStatus));

            VacancyStatus result = vacancyStatusService.findVacancyStatusById(1L);

            assertThat(result).isEqualTo(draftStatus);
            verify(vacancyStatusRepository).findVacancyStatusById(1L);
            verifyNoMoreInteractions(vacancyStatusRepository);
        }

        @Test
        void findByVacancyStatusNameWhenError() {
            when(vacancyStatusRepository.findVacancyStatusById(1L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    vacancyStatusService.findVacancyStatusById(1L)
            ).isInstanceOf(VacancyStatusNotFoundException.class);
            verify(vacancyStatusRepository).findVacancyStatusById(1L);
            verifyNoMoreInteractions(vacancyStatusRepository);
        }
    }


}