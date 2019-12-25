package ru.krushnyakov.eratosthenes;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChunkResult {

    private final List<Long> primes;

    private final long primesNumber;

    public ChunkResult(List<Long> primes, long primesNumber) {
        super();
        this.primes = primes;
        this.primesNumber = primesNumber;
    }

    public List<Long> getPrimes() {
        return Collections.unmodifiableList(primes);
    }

    public long size() {
        return primesNumber;
    }


}