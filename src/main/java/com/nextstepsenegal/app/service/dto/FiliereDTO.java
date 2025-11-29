package com.nextstepsenegal.app.service.dto;

import java.util.List;

public class FiliereDTO {

    private Long id;
    private String titre;
    private String domaine;
    private String descriptionFormation;
    private Integer difficulte;
    private Integer tauxEmploi;
    private Integer tauxSatisfaction;
    private Double salaireMin;
    private Double salaireMax;
    private Integer dureeFormation;
    private List<String> ecoles;
    private List<String> debouches;
    private List<String> competences;
    private String temoignages;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getDescriptionFormation() {
        return descriptionFormation;
    }

    public void setDescriptionFormation(String descriptionFormation) {
        this.descriptionFormation = descriptionFormation;
    }

    public Integer getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(Integer difficulte) {
        this.difficulte = difficulte;
    }

    public Integer getTauxEmploi() {
        return tauxEmploi;
    }

    public void setTauxEmploi(Integer tauxEmploi) {
        this.tauxEmploi = tauxEmploi;
    }

    public Integer getTauxSatisfaction() {
        return tauxSatisfaction;
    }

    public void setTauxSatisfaction(Integer tauxSatisfaction) {
        this.tauxSatisfaction = tauxSatisfaction;
    }

    public Double getSalaireMin() {
        return salaireMin;
    }

    public void setSalaireMin(Double salaireMin) {
        this.salaireMin = salaireMin;
    }

    public Double getSalaireMax() {
        return salaireMax;
    }

    public void setSalaireMax(Double salaireMax) {
        this.salaireMax = salaireMax;
    }

    public Integer getDureeFormation() {
        return dureeFormation;
    }

    public void setDureeFormation(Integer dureeFormation) {
        this.dureeFormation = dureeFormation;
    }

    public List<String> getEcoles() {
        return ecoles;
    }

    public void setEcoles(List<String> ecoles) {
        this.ecoles = ecoles;
    }

    public List<String> getDebouches() {
        return debouches;
    }

    public void setDebouches(List<String> debouches) {
        this.debouches = debouches;
    }

    public List<String> getCompetences() {
        return competences;
    }

    public void setCompetences(List<String> competences) {
        this.competences = competences;
    }

    public String getTemoignages() {
        return temoignages;
    }

    public void setTemoignages(String temoignages) {
        this.temoignages = temoignages;
    }
}
