package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FighterServiceDTO {
    public UUID id;
    public ServiceType serviceType;
    public String title;
    public String description;
    public BigDecimal pricePerHour;
    public Integer durationMinutes;
    public boolean isActive;
    public boolean isOnline;
    public String fighterName;
    public String fighterUsername;
    public String fighterDiscipline;
    public String fighterCity;
    public LocalDateTime createdAt;
}