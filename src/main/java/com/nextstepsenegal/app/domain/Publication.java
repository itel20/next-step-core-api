package com.nextstepsenegal.app.domain;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Publication
 */
@Entity
@Table(name = "publication")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "publication")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String content;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private UserType authorType;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Publication id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Publication content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public Publication authorId(Long authorId) {
        this.setAuthorId(authorId);
        return this;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public UserType getAuthorType() {
        return this.authorType;
    }

    public Publication authorType(UserType authorType) {
        this.setAuthorType(authorType);
        return this;
    }

    public void setAuthorType(UserType authorType) {
        this.authorType = authorType;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Publication createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Publication)) {
            return false;
        }
        return getId() != null && getId().equals(((Publication) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Publication{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", authorId=" + getAuthorId() +
            ", authorType='" + getAuthorType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
