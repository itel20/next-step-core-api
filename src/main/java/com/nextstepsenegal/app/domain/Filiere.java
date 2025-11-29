package com.nextstepsenegal.app.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filieres")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String domaine;

    @Column(columnDefinition = "TEXT")
    private String descriptionFormation;

    private Integer difficulte; // /10

    private Integer tauxEmploi;

    private Integer tauxSatisfaction;

    private Double salaireMin;

    private Double salaireMax;

    private Integer dureeFormation; // en années

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "filiere_ecoles", joinColumns = @JoinColumn(name = "filiere_id"))
    @Column(name = "ecole")
    private List<String> ecoles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "filiere_debouches", joinColumns = @JoinColumn(name = "filiere_id"))
    @Column(name = "debouche")
    private List<String> debouches = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "filiere_competences", joinColumns = @JoinColumn(name = "filiere_id"))
    @Column(name = "competence")
    private List<String> competences = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String temoignages;

    // Getters et Setters

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

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
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
