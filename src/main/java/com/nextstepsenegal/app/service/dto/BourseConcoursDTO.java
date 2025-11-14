package com.nextstepsenegal.app.service.dto;

import java.time.LocalDate;
import java.util.List;

public class BourseConcoursDTO {

    private Long id;
    private String titre;
    private String type;
    private Double montant;
    private String periodicite;
    private Integer nombreBeneficiaires;
    private Double tauxAcceptation;
    private LocalDate dateLimite;
    private List<String> criteresEligibilite;
    private List<String> tags;
    private String conseilsPratiques;
    private Boolean favoris;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public String getPeriodicite() { return periodicite; }
    public void setPeriodicite(String periodicite) { this.periodicite = periodicite; }
    public Integer getNombreBeneficiaires() { return nombreBeneficiaires; }
    public void setNombreBeneficiaires(Integer nombreBeneficiaires) { this.nombreBeneficiaires = nombreBeneficiaires; }
    public Double getTauxAcceptation() { return tauxAcceptation; }
    public void setTauxAcceptation(Double tauxAcceptation) { this.tauxAcceptation = tauxAcceptation; }
    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }
    public List<String> getCriteresEligibilite() { return criteresEligibilite; }
    public void setCriteresEligibilite(List<String> criteresEligibilite) { this.criteresEligibilite = criteresEligibilite; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getConseilsPratiques() { return conseilsPratiques; }
    public void setConseilsPratiques(String conseilsPratiques) { this.conseilsPratiques = conseilsPratiques; }
    public Boolean getFavoris() { return favoris; }
    public void setFavoris(Boolean favoris) { this.favoris = favoris; }
}
