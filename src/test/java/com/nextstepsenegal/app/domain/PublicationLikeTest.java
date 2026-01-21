package com.nextstepsenegal.app.domain;

import static com.nextstepsenegal.app.domain.PublicationLikeTestSamples.*;
import static com.nextstepsenegal.app.domain.PublicationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicationLikeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicationLike.class);
        PublicationLike publicationLike1 = getPublicationLikeSample1();
        PublicationLike publicationLike2 = new PublicationLike();
        assertThat(publicationLike1).isNotEqualTo(publicationLike2);

        publicationLike2.setId(publicationLike1.getId());
        assertThat(publicationLike1).isEqualTo(publicationLike2);

        publicationLike2 = getPublicationLikeSample2();
        assertThat(publicationLike1).isNotEqualTo(publicationLike2);
    }

    @Test
    void publicationTest() {
        PublicationLike publicationLike = getPublicationLikeRandomSampleGenerator();
        Publication publicationBack = getPublicationRandomSampleGenerator();

        publicationLike.setPublication(publicationBack);
        assertThat(publicationLike.getPublication()).isEqualTo(publicationBack);

        publicationLike.publication(null);
        assertThat(publicationLike.getPublication()).isNull();
    }
}
