package ru.krushnyakov.eratosthenes;

public class ChunkResult {

    private final long[] primes;

    public ChunkResult(long[] primes) {
        super();
        this.primes = primes;
    }

    public long[] getPrimes() {
        return primes;
    }
    
    public long size() {
        return primes.length;
    }


}