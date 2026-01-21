package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PublicationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Publication getPublicationSample1() {
        return new Publication().id(1L).authorId(1L);
    }

    public static Publication getPublicationSample2() {
        return new Publication().id(2L).authorId(2L);
    }

    public static Publication getPublicationRandomSampleGenerator() {
        return new Publication().id(longCount.incrementAndGet()).authorId(longCount.incrementAndGet());
    }
}
