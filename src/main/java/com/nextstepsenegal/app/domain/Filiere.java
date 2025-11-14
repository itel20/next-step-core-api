package com.nextstepsenegal.app.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "filieres")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String categorie;

    @Column(columnDefinition = "TEXT")
    private String descriptionDetaillee;

    private Integer difficulte;

    private Integer tauxEmploi;

    private Double satisfaction;

    private Double salaireMoyen;

    private String dureeFormation;

    @ElementCollection
    private List<String> universites;

    @ElementCollection
    private List<String> debouches;

    @ElementCollection
    private List<String> competences;

    @Column(columnDefinition = "TEXT")
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

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescriptionDetaillee() {
        return descriptionDetaillee;
    }

    public void setDescriptionDetaillee(String descriptionDetaillee) {
        this.descriptionDetaillee = descriptionDetaillee;
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

    public Double getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Double satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Double getSalaireMoyen() {
        return salaireMoyen;
    }

    public void setSalaireMoyen(Double salaireMoyen) {
        this.salaireMoyen = salaireMoyen;
    }

    public String getDureeFormation() {
        return dureeFormation;
    }

    public void setDureeFormation(String dureeFormation) {
        this.dureeFormation = dureeFormation;
    }

    public List<String> getUniversites() {
        return universites;
    }

    public void setUniversites(List<String> universites) {
        this.universites = universites;
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
