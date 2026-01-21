package com.nextstepsenegal.app.service.dto;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.nextstepsenegal.app.domain.PublicationShare} entity.
 */
@Schema(description = "Partage d’une publication")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicationShareDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private UserType userType;

    @NotNull
    private Instant sharedAt;

    private PublicationDTO publication;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Instant getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(Instant sharedAt) {
        this.sharedAt = sharedAt;
    }

    public PublicationDTO getPublication() {
        return publication;
    }

    public void setPublication(PublicationDTO publication) {
        this.publication = publication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicationShareDTO)) {
            return false;
        }

        PublicationShareDTO publicationShareDTO = (PublicationShareDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, publicationShareDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicationShareDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", userType='" + getUserType() + "'" +
            ", sharedAt='" + getSharedAt() + "'" +
            ", publication=" + getPublication() +
            "}";
    }
}
