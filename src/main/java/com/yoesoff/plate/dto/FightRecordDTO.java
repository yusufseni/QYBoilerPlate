package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.FightMethod;
import com.yoesoff.plate.enums.FightResult;

import java.time.LocalDate;
import java.util.UUID;

public class FightRecordDTO {
    public UUID id;
    public String opponent;
    public LocalDate fightDate;
    public FightResult result;
    public FightMethod method;
    public Integer round;
    public String time;
    public String event;
    public String location;
    public String weightClass;
    public String notes;
}