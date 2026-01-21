package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PublicationLikeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PublicationLike getPublicationLikeSample1() {
        return new PublicationLike().id(1L).userId(1L);
    }

    public static PublicationLike getPublicationLikeSample2() {
        return new PublicationLike().id(2L).userId(2L);
    }

    public static PublicationLike getPublicationLikeRandomSampleGenerator() {
        return new PublicationLike().id(longCount.incrementAndGet()).userId(longCount.incrementAndGet());
    }
}
