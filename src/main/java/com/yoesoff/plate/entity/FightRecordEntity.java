package com.yoesoff.plate.entity;

import com.yoesoff.plate.enums.FightResult;
import com.yoesoff.plate.enums.FightMethod;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fight_records")
public class FightRecordEntity extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fighter_id")
    public UserEntity fighter;

    @Column(nullable = false)
    @NotBlank
    public String opponent;

    @Column(nullable = false)
    @NotNull
    public LocalDate fightDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public FightResult result;

    @Enumerated(EnumType.STRING)
    public FightMethod method;

    public Integer round; // Which round the fight ended
    public String time; // Time in the round (e.g., "2:34")

    public String event; // Event name
    public String location; // Fight location
    public String weightClass;

    @Column(columnDefinition = "TEXT")
    public String notes;

    // Helper method to get record stats
    public static long getWins(UserEntity fighter) {
        return count("fighter = ?1 and result = ?2", fighter, FightResult.WIN);
    }

    public static long getLosses(UserEntity fighter) {
        return count("fighter = ?1 and result = ?2", fighter, FightResult.LOSS);
    }

    public static long getDraws(UserEntity fighter) {
        return count("fighter = ?1 and result = ?2", fighter, FightResult.DRAW);
    }
}