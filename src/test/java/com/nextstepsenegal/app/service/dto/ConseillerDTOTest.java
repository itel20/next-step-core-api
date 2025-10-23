package com.nextstepsenegal.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConseillerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConseillerDTO.class);
        ConseillerDTO conseillerDTO1 = new ConseillerDTO();
        conseillerDTO1.setId(1L);
        ConseillerDTO conseillerDTO2 = new ConseillerDTO();
        assertThat(conseillerDTO1).isNotEqualTo(conseillerDTO2);
        conseillerDTO2.setId(conseillerDTO1.getId());
        assertThat(conseillerDTO1).isEqualTo(conseillerDTO2);
        conseillerDTO2.setId(2L);
        assertThat(conseillerDTO1).isNotEqualTo(conseillerDTO2);
        conseillerDTO1.setId(null);
        assertThat(conseillerDTO1).isNotEqualTo(conseillerDTO2);
    }
}
