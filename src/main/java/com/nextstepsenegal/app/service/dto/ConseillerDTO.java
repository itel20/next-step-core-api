package com.nextstepsenegal.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.nextstepsenegal.app.domain.Conseiller} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConseillerDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    @NotNull
    private String specialite;

    @NotNull
    private String email;

    private String description;

    private Boolean certifie;

    @NotNull
    private String password;

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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCertifie() {
        return certifie;
    }

    public void setCertifie(Boolean certifie) {
        this.certifie = certifie;
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
        if (!(o instanceof ConseillerDTO)) {
            return false;
        }

        ConseillerDTO conseillerDTO = (ConseillerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, conseillerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConseillerDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", specialite='" + getSpecialite() + "'" +
            ", email='" + getEmail() + "'" +
            ", description='" + getDescription() + "'" +
            ", certifie='" + getCertifie() + "'" +
            ", password='" + getPassword() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            ", keycloakId='" + getKeycloakId() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
