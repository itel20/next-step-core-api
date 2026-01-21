package com.nextstepsenegal.app.domain;

import static com.nextstepsenegal.app.domain.PublicationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Publication.class);
        Publication publication1 = getPublicationSample1();
        Publication publication2 = new Publication();
        assertThat(publication1).isNotEqualTo(publication2);

        publication2.setId(publication1.getId());
        assertThat(publication1).isEqualTo(publication2);

        publication2 = getPublicationSample2();
        assertThat(publication1).isNotEqualTo(publication2);
    }
}
