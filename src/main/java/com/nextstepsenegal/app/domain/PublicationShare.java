package com.nextstepsenegal.app.domain;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Partage d’une publication
 */
@Entity
@Table(name = "publication_share")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "publicationshare")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicationShare implements Serializable {

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
    @Column(name = "shared_at", nullable = false)
    private Instant sharedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Publication publication;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PublicationShare id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public PublicationShare userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public PublicationShare userType(UserType userType) {
        this.setUserType(userType);
        return this;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Instant getSharedAt() {
        return this.sharedAt;
    }

    public PublicationShare sharedAt(Instant sharedAt) {
        this.setSharedAt(sharedAt);
        return this;
    }

    public void setSharedAt(Instant sharedAt) {
        this.sharedAt = sharedAt;
    }

    public Publication getPublication() {
        return this.publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public PublicationShare publication(Publication publication) {
        this.setPublication(publication);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicationShare)) {
            return false;
        }
        return getId() != null && getId().equals(((PublicationShare) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicationShare{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", userType='" + getUserType() + "'" +
            ", sharedAt='" + getSharedAt() + "'" +
            "}";
    }
}
