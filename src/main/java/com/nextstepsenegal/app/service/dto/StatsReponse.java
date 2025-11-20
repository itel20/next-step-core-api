package com.nextstepsenegal.app.service.dto;

public class StatsReponse {

    private long totalEtudiants;
    private long totalEleves;
    private long totalConseillers;
    private long totalAll;

    public long getTotalEtudiants() {
        return totalEtudiants;
    }

    public void setTotalEtudiants(long totalEtudiants) {
        this.totalEtudiants = totalEtudiants;
    }

    public long getTotalEleves() {
        return totalEleves;
    }

    public void setTotalEleves(long totalEleves) {
        this.totalEleves = totalEleves;
    }

    public long getTotalConseillers() {
        return totalConseillers;
    }

    public void setTotalConseillers(long totalConseillers) {
        this.totalConseillers = totalConseillers;
    }

    public long getTotalAll() {
        return totalAll;
    }

    public void setTotalAll(long totalAll) {
        this.totalAll = totalAll;
    }
}
