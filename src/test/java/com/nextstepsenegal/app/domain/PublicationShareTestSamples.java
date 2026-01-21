package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PublicationShareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PublicationShare getPublicationShareSample1() {
        return new PublicationShare().id(1L).userId(1L);
    }

    public static PublicationShare getPublicationShareSample2() {
        return new PublicationShare().id(2L).userId(2L);
    }

    public static PublicationShare getPublicationShareRandomSampleGenerator() {
        return new PublicationShare().id(longCount.incrementAndGet()).userId(longCount.incrementAndGet());
    }
}
