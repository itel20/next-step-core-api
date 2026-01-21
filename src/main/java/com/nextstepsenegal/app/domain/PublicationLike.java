package com.nextstepsenegal.app.domain;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Like d’une publication
 */
@Entity
@Table(name = "publication_like")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "publicationlike")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicationLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private UserType userType;

    @NotNull
    @Column(name = "liked_at", nullable = false)
    private Instant likedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Publication publication;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PublicationLike id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public PublicationLike userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public PublicationLike userType(UserType userType) {
        this.setUserType(userType);
        return this;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Instant getLikedAt() {
        return this.likedAt;
    }

    public PublicationLike likedAt(Instant likedAt) {
        this.setLikedAt(likedAt);
        return this;
    }

    public void setLikedAt(Instant likedAt) {
        this.likedAt = likedAt;
    }

    public Publication getPublication() {
        return this.publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public PublicationLike publication(Publication publication) {
        this.setPublication(publication);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicationLike)) {
            return false;
        }
        return getId() != null && getId().equals(((PublicationLike) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicationLike{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", userType='" + getUserType() + "'" +
            ", likedAt='" + getLikedAt() + "'" +
            "}";
    }
}
