package com.nextstepsenegal.app.service.dto;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.nextstepsenegal.app.domain.Publication} entity.
 */
@Schema(description = "Publication")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicationDTO implements Serializable {

    private Long id;

    @Lob
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private UserType authorType;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public UserType getAuthorType() {
        return authorType;
    }

    public void setAuthorType(UserType authorType) {
        this.authorType = authorType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicationDTO)) {
            return false;
        }

        PublicationDTO publicationDTO = (PublicationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, publicationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicationDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", authorId=" + getAuthorId() +
            ", authorType='" + getAuthorType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
