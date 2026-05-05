package com.bornfire.xbrl.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTemplateConfigRepository extends JpaRepository<ReportTemplateConfig, String> {
}
