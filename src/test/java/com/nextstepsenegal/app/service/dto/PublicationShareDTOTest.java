package com.nextstepsenegal.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicationShareDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicationShareDTO.class);
        PublicationShareDTO publicationShareDTO1 = new PublicationShareDTO();
        publicationShareDTO1.setId(1L);
        PublicationShareDTO publicationShareDTO2 = new PublicationShareDTO();
        assertThat(publicationShareDTO1).isNotEqualTo(publicationShareDTO2);
        publicationShareDTO2.setId(publicationShareDTO1.getId());
        assertThat(publicationShareDTO1).isEqualTo(publicationShareDTO2);
        publicationShareDTO2.setId(2L);
        assertThat(publicationShareDTO1).isNotEqualTo(publicationShareDTO2);
        publicationShareDTO1.setId(null);
        assertThat(publicationShareDTO1).isNotEqualTo(publicationShareDTO2);
    }
}
