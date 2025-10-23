package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.EtudiantAsserts.*;
import static com.nextstepsenegal.app.domain.EtudiantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtudiantMapperTest {

    private EtudiantMapper etudiantMapper;

    @BeforeEach
    void setUp() {
        etudiantMapper = new EtudiantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEtudiantSample1();
        var actual = etudiantMapper.toEntity(etudiantMapper.toDto(expected));
        assertEtudiantAllPropertiesEquals(expected, actual);
    }
}
