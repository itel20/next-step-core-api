package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.PublicationAsserts.*;
import static com.nextstepsenegal.app.domain.PublicationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicationMapperTest {

    private PublicationMapper publicationMapper;

    @BeforeEach
    void setUp() {
        publicationMapper = new PublicationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPublicationSample1();
        var actual = publicationMapper.toEntity(publicationMapper.toDto(expected));
        assertPublicationAllPropertiesEquals(expected, actual);
    }
}
