package com.nextstepsenegal.app.service.mapper;

import static com.nextstepsenegal.app.domain.ConseillerAsserts.*;
import static com.nextstepsenegal.app.domain.ConseillerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConseillerMapperTest {

    private ConseillerMapper conseillerMapper;

    @BeforeEach
    void setUp() {
        conseillerMapper = new ConseillerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getConseillerSample1();
        var actual = conseillerMapper.toEntity(conseillerMapper.toDto(expected));
        assertConseillerAllPropertiesEquals(expected, actual);
    }
}
