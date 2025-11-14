package com.nextstepsenegal.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.nextstepsenegal.app.domain.Etudiant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtudiantDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    private LocalDate dateNaissance;

    private String telephone;

    private String adresse;

    @NotNull
    private String email;

    @NotNull
    private String typeBac;

    private Integer anneeBac;

    private String niveauDetudes;

    private String universiteSouhaitee;

    private String specialiteSouhaitee;

    private String password;

    @NotNull
    private String passwordHash;

    private String keycloakId;

    private UserDTO user;

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeBac() {
        return typeBac;
    }

    public void setTypeBac(String typeBac) {
        this.typeBac = typeBac;
    }

    public Integer getAnneeBac() {
        return anneeBac;
    }

    public void setAnneeBac(Integer anneeBac) {
        this.anneeBac = anneeBac;
    }

    public String getNiveauDetudes() {
        return niveauDetudes;
    }

    public void setNiveauDetudes(String niveauDetudes) {
        this.niveauDetudes = niveauDetudes;
    }

    public String getUniversiteSouhaitee() {
        return universiteSouhaitee;
    }

    public void setUniversiteSouhaitee(String universiteSouhaitee) {
        this.universiteSouhaitee = universiteSouhaitee;
    }

    public String getSpecialiteSouhaitee() {
        return specialiteSouhaitee;
    }

    public void setSpecialiteSouhaitee(String specialiteSouhaitee) {
        this.specialiteSouhaitee = specialiteSouhaitee;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtudiantDTO)) {
            return false;
        }

        EtudiantDTO etudiantDTO = (EtudiantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etudiantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtudiantDTO{" +
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
            ", user=" + getUser() +
            "}";
    }
}
