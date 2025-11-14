package com.nextstepsenegal.app.domain;

import static com.nextstepsenegal.app.domain.EleveTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EleveTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Eleve.class);
        Eleve eleve1 = getEleveSample1();
        Eleve eleve2 = new Eleve();
        assertThat(eleve1).isNotEqualTo(eleve2);

        eleve2.setId(eleve1.getId());
        assertThat(eleve1).isEqualTo(eleve2);

        eleve2 = getEleveSample2();
        assertThat(eleve1).isNotEqualTo(eleve2);
    }
}
