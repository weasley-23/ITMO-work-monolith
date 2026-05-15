package com.itmo_work.api_monolith.mappers;

import com.itmo_work.api_monolith.dto.ApplicationDto;
import com.itmo_work.api_monolith.dto.request.ApplicationCreateRequestDto;
import com.itmo_work.api_monolith.model.Application;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Application application, ApplicationCreateRequestDto applicationCreateRequestDto);

    @Mapping(target = "status", source = "status.applicationStatus")
    @Mapping(target = "vacancyId", source = "vacancy.id")
    @Mapping(target = "vacancyTitle", source = "vacancy.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.fullName")
    ApplicationDto toDto(Application application);
}
