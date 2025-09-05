package com.yoesoff.plate.dto;

public class FighterStatsDTO {
    public long totalWins;
    public long totalLosses;
    public long totalDraws;
    public long totalFights;
    public long koWins;
    public long submissionWins;
    public long decisionWins;
    public Double averageRating;
    public long reviewCount;
    public long completedBookings;
    public long activeServices;

    public String getRecord() {
        return totalWins + "-" + totalLosses + "-" + totalDraws;
    }

    public Double getWinPercentage() {
        if (totalFights == 0) return 0.0;
        return (totalWins * 100.0) / totalFights;
    }
}