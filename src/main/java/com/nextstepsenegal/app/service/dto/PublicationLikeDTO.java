package com.nextstepsenegal.app.service.dto;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.nextstepsenegal.app.domain.PublicationLike} entity.
 */
@Schema(description = "Like d’une publication")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicationLikeDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private UserType userType;

    @NotNull
    private Instant likedAt;

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

    public Instant getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(Instant likedAt) {
        this.likedAt = likedAt;
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
        if (!(o instanceof PublicationLikeDTO)) {
            return false;
        }

        PublicationLikeDTO publicationLikeDTO = (PublicationLikeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, publicationLikeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicationLikeDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", userType='" + getUserType() + "'" +
            ", likedAt='" + getLikedAt() + "'" +
            ", publication=" + getPublication() +
            "}";
    }
}
