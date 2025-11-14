package com.nextstepsenegal.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EleveDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EleveDTO.class);
        EleveDTO eleveDTO1 = new EleveDTO();
        eleveDTO1.setId(1L);
        EleveDTO eleveDTO2 = new EleveDTO();
        assertThat(eleveDTO1).isNotEqualTo(eleveDTO2);
        eleveDTO2.setId(eleveDTO1.getId());
        assertThat(eleveDTO1).isEqualTo(eleveDTO2);
        eleveDTO2.setId(2L);
        assertThat(eleveDTO1).isNotEqualTo(eleveDTO2);
        eleveDTO1.setId(null);
        assertThat(eleveDTO1).isNotEqualTo(eleveDTO2);
    }
}
