package com.nextstepsenegal.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nextstepsenegal.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicationLikeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicationLikeDTO.class);
        PublicationLikeDTO publicationLikeDTO1 = new PublicationLikeDTO();
        publicationLikeDTO1.setId(1L);
        PublicationLikeDTO publicationLikeDTO2 = new PublicationLikeDTO();
        assertThat(publicationLikeDTO1).isNotEqualTo(publicationLikeDTO2);
        publicationLikeDTO2.setId(publicationLikeDTO1.getId());
        assertThat(publicationLikeDTO1).isEqualTo(publicationLikeDTO2);
        publicationLikeDTO2.setId(2L);
        assertThat(publicationLikeDTO1).isNotEqualTo(publicationLikeDTO2);
        publicationLikeDTO1.setId(null);
        assertThat(publicationLikeDTO1).isNotEqualTo(publicationLikeDTO2);
    }
}
