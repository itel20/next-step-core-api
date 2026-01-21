package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.PublicationShareAsserts.*;
import static com.nextstepsenegal.app.domain.PublicationShareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicationShareMapperTest {

    private PublicationShareMapper publicationShareMapper;

    @BeforeEach
    void setUp() {
        publicationShareMapper = new PublicationShareMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPublicationShareSample1();
        var actual = publicationShareMapper.toEntity(publicationShareMapper.toDto(expected));
        assertPublicationShareAllPropertiesEquals(expected, actual);
    }
}
