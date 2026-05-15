package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.model.VacancyStatus;
import com.itmo_work.api_monolith.model.VacancyStatusName;

public interface VacancyStatusService {
    VacancyStatus findByVacancyStatusName(VacancyStatusName statusName);
    VacancyStatus findVacancyStatusById(Long vacancyStatusId);
}
