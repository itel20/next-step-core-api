package com.nextstepsenegal.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Etudiant.
 */
@Entity
@Table(name = "etudiant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "etudiant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Etudiant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nom;

    @NotNull
    @Column(name = "prenom", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "telephone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String telephone;

    @Column(name = "adresse")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String adresse;

    @NotNull
    @Column(name = "email", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @NotNull
    @Column(name = "type_bac", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String typeBac;

    @Column(name = "annee_bac")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer anneeBac;

    @Column(name = "niveau_detudes")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String niveauDetudes;

    @Column(name = "universite_souhaitee")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String universiteSouhaitee;

    @Column(name = "specialite_souhaitee")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String specialiteSouhaitee;

    @Column(name = "password")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String password;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String passwordHash;

    @Column(name = "keycloak_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String keycloakId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Etudiant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Etudiant nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Etudiant prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Etudiant dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Etudiant telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public Etudiant adresse(String adresse) {
        this.setAdresse(adresse);
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return this.email;
    }

    public Etudiant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeBac() {
        return this.typeBac;
    }

    public Etudiant typeBac(String typeBac) {
        this.setTypeBac(typeBac);
        return this;
    }

    public void setTypeBac(String typeBac) {
        this.typeBac = typeBac;
    }

    public Integer getAnneeBac() {
        return this.anneeBac;
    }

    public Etudiant anneeBac(Integer anneeBac) {
        this.setAnneeBac(anneeBac);
        return this;
    }

    public void setAnneeBac(Integer anneeBac) {
        this.anneeBac = anneeBac;
    }

    public String getNiveauDetudes() {
        return this.niveauDetudes;
    }

    public Etudiant niveauDetudes(String niveauDetudes) {
        this.setNiveauDetudes(niveauDetudes);
        return this;
    }

    public void setNiveauDetudes(String niveauDetudes) {
        this.niveauDetudes = niveauDetudes;
    }

    public String getUniversiteSouhaitee() {
        return this.universiteSouhaitee;
    }

    public Etudiant universiteSouhaitee(String universiteSouhaitee) {
        this.setUniversiteSouhaitee(universiteSouhaitee);
        return this;
    }

    public void setUniversiteSouhaitee(String universiteSouhaitee) {
        this.universiteSouhaitee = universiteSouhaitee;
    }

    public String getSpecialiteSouhaitee() {
        return this.specialiteSouhaitee;
    }

    public Etudiant specialiteSouhaitee(String specialiteSouhaitee) {
        this.setSpecialiteSouhaitee(specialiteSouhaitee);
        return this;
    }

    public void setSpecialiteSouhaitee(String specialiteSouhaitee) {
        this.specialiteSouhaitee = specialiteSouhaitee;
    }

    public String getPassword() {
        return this.password;
    }

    public Etudiant password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Etudiant passwordHash(String passwordHash) {
        this.setPasswordHash(passwordHash);
        return this;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getKeycloakId() {
        return this.keycloakId;
    }

    public Etudiant keycloakId(String keycloakId) {
        this.setKeycloakId(keycloakId);
        return this;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Etudiant user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etudiant)) {
            return false;
        }
        return getId() != null && getId().equals(((Etudiant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etudiant{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", email='" + getEmail() + "'" +
            ", typeBac='" + getTypeBac() + "'" +
            ", anneeBac=" + getAnneeBac() +
            ", niveauDetudes='" + getNiveauDetudes() + "'" +
            ", universiteSouhaitee='" + getUniversiteSouhaitee() + "'" +
            ", specialiteSouhaitee='" + getSpecialiteSouhaitee() + "'" +
            ", password='" + getPassword() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            ", keycloakId='" + getKeycloakId() + "'" +
            "}";
    }
}
