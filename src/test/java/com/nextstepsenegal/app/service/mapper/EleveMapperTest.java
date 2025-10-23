package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.EleveAsserts.*;
import static com.nextstepsenegal.app.domain.EleveTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EleveMapperTest {

    private EleveMapper eleveMapper;

    @BeforeEach
    void setUp() {
        eleveMapper = new EleveMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEleveSample1();
        var actual = eleveMapper.toEntity(eleveMapper.toDto(expected));
        assertEleveAllPropertiesEquals(expected, actual);
    }
}
