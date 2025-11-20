package com.nextstepsenegal.app.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "etablissements")
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String type; // Publique ou Privée

    private Integer classement;

    private String ville;

    private String pays;

    // Statistiques principales
    private Integer nombreEtudiants;
    private Integer nombreEnseignants;
    private String laboratoiresBibliotheques;
    private Double fraisScolarite;
    private Double tauxSelectivite;

    @ElementCollection
    private List<String> filieresDisponibles;

    // Processus d'admission
    private String processusAdmission;
    private Double tauxAcceptation;
    private String pointsBacRequis;

    // Vie étudiante
    @Column(columnDefinition = "TEXT")
    private String vieEtudiante;

    // Informations pratiques
    @Column(columnDefinition = "TEXT")
    private String informationsPratiques;

    // Insertion professionnelle
    private Double tauxInsertion;
    private Double salaireMoyen;

    // Témoignages étudiants
    @Column(columnDefinition = "TEXT")
    private String temoignages;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getClassement() {
        return classement;
    }

    public void setClassement(Integer classement) {
        this.classement = classement;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Integer getNombreEtudiants() {
        return nombreEtudiants;
    }

    public void setNombreEtudiants(Integer nombreEtudiants) {
        this.nombreEtudiants = nombreEtudiants;
    }

    public Integer getNombreEnseignants() {
        return nombreEnseignants;
    }

    public void setNombreEnseignants(Integer nombreEnseignants) {
        this.nombreEnseignants = nombreEnseignants;
    }

    public String getLaboratoiresBibliotheques() {
        return laboratoiresBibliotheques;
    }

    public void setLaboratoiresBibliotheques(String laboratoiresBibliotheques) {
        this.laboratoiresBibliotheques = laboratoiresBibliotheques;
    }

    public Double getFraisScolarite() {
        return fraisScolarite;
    }

    public void setFraisScolarite(Double fraisScolarite) {
        this.fraisScolarite = fraisScolarite;
    }

    public Double getTauxSelectivite() {
        return tauxSelectivite;
    }

    public void setTauxSelectivite(Double tauxSelectivite) {
        this.tauxSelectivite = tauxSelectivite;
    }

    public List<String> getFilieresDisponibles() {
        return filieresDisponibles;
    }

    public void setFilieresDisponibles(List<String> filieresDisponibles) {
        this.filieresDisponibles = filieresDisponibles;
    }

    public String getProcessusAdmission() {
        return processusAdmission;
    }

    public void setProcessusAdmission(String processusAdmission) {
        this.processusAdmission = processusAdmission;
    }

    public Double getTauxAcceptation() {
        return tauxAcceptation;
    }

    public void setTauxAcceptation(Double tauxAcceptation) {
        this.tauxAcceptation = tauxAcceptation;
    }

    public String getPointsBacRequis() {
        return pointsBacRequis;
    }

    public void setPointsBacRequis(String pointsBacRequis) {
        this.pointsBacRequis = pointsBacRequis;
    }

    public String getVieEtudiante() {
        return vieEtudiante;
    }

    public void setVieEtudiante(String vieEtudiante) {
        this.vieEtudiante = vieEtudiante;
    }

    public String getInformationsPratiques() {
        return informationsPratiques;
    }

    public void setInformationsPratiques(String informationsPratiques) {
        this.informationsPratiques = informationsPratiques;
    }

    public Double getTauxInsertion() {
        return tauxInsertion;
    }

    public void setTauxInsertion(Double tauxInsertion) {
        this.tauxInsertion = tauxInsertion;
    }

    public Double getSalaireMoyen() {
        return salaireMoyen;
    }

    public void setSalaireMoyen(Double salaireMoyen) {
        this.salaireMoyen = salaireMoyen;
    }

    public String getTemoignages() {
        return temoignages;
    }

    public void setTemoignages(String temoignages) {
        this.temoignages = temoignages;
    }
}
