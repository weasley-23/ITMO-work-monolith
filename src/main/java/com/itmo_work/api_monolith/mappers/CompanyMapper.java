package com.itmo_work.api_monolith.mappers;

import com.itmo_work.api_monolith.dto.request.CompanyUpdateRequestDto;
import com.itmo_work.api_monolith.model.Company;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompanyFromDto(@MappingTarget Company company, CompanyUpdateRequestDto companyRequestDto);
}
