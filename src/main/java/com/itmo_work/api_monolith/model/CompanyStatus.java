package com.itmo_work.api_monolith.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "company_status")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "status", nullable = false, unique = true)
    private CompanyStatusName statusName;
}
