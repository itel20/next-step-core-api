package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.PublicationLikeAsserts.*;
import static com.nextstepsenegal.app.domain.PublicationLikeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicationLikeMapperTest {

    private PublicationLikeMapper publicationLikeMapper;

    @BeforeEach
    void setUp() {
        publicationLikeMapper = new PublicationLikeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPublicationLikeSample1();
        var actual = publicationLikeMapper.toEntity(publicationLikeMapper.toDto(expected));
        assertPublicationLikeAllPropertiesEquals(expected, actual);
    }
}
