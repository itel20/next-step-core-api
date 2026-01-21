package com.nextstepsenegal.app.domain;

import static com.nextstepsenegal.app.domain.PublicationShareTestSamples.*;
import static com.nextstepsenegal.app.domain.PublicationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicationShareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicationShare.class);
        PublicationShare publicationShare1 = getPublicationShareSample1();
        PublicationShare publicationShare2 = new PublicationShare();
        assertThat(publicationShare1).isNotEqualTo(publicationShare2);

        publicationShare2.setId(publicationShare1.getId());
        assertThat(publicationShare1).isEqualTo(publicationShare2);

        publicationShare2 = getPublicationShareSample2();
        assertThat(publicationShare1).isNotEqualTo(publicationShare2);
    }

    @Test
    void publicationTest() {
        PublicationShare publicationShare = getPublicationShareRandomSampleGenerator();
        Publication publicationBack = getPublicationRandomSampleGenerator();

        publicationShare.setPublication(publicationBack);
        assertThat(publicationShare.getPublication()).isEqualTo(publicationBack);

        publicationShare.publication(null);
        assertThat(publicationShare.getPublication()).isNull();
    }
}
