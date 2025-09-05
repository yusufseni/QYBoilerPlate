package com.yoesoff.plate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDTO {
    public UUID id;
    public Integer rating;
    public String comment;
    public LocalDateTime createdAt;
    public String clientName;
    public String clientUsername;
    public String fighterName;
    public String fighterUsername;
}