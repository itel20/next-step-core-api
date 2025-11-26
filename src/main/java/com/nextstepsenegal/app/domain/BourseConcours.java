package com.nextstepsenegal.app.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bourses_concours")
public class BourseConcours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    // national / international
    private String type;

    // complète / moitié
    private String typeBourse;

    private Integer nombreBeneficiaires;

    private Double tauxAccepte;

    private LocalDate dateLimite;

    @ElementCollection
    @CollectionTable(name = "bourse_criteres", joinColumns = @JoinColumn(name = "bourse_id"))
    @Column(name = "critere")
    private List<String> criteres;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTypeBourse() { return typeBourse; }
    public void setTypeBourse(String typeBourse) { this.typeBourse = typeBourse; }

    public Integer getNombreBeneficiaires() { return nombreBeneficiaires; }
    public void setNombreBeneficiaires(Integer nombreBeneficiaires) { this.nombreBeneficiaires = nombreBeneficiaires; }

    public Double getTauxAccepte() { return tauxAccepte; }
    public void setTauxAccepte(Double tauxAccepte) { this.tauxAccepte = tauxAccepte; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }

    public List<String> getCriteres() { return criteres; }
    public void setCriteres(List<String> criteres) { this.criteres = criteres; }
}
