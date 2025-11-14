package com.nextstepsenegal.app.domain;

import static com.nextstepsenegal.app.domain.ConseillerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConseillerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conseiller.class);
        Conseiller conseiller1 = getConseillerSample1();
        Conseiller conseiller2 = new Conseiller();
        assertThat(conseiller1).isNotEqualTo(conseiller2);

        conseiller2.setId(conseiller1.getId());
        assertThat(conseiller1).isEqualTo(conseiller2);

        conseiller2 = getConseillerSample2();
        assertThat(conseiller1).isNotEqualTo(conseiller2);
    }
}
