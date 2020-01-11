package ru.krushnyakov.eratosthenes;

import java.util.List;

public class SieveResult {

    private List<Long> primes;

    private long primesNumber;

    private List<ChunkResult> chunks;
    
    public SieveResult(List<Long> primes, long primesNumber, List<ChunkResult> chunks) {
        super();
        this.primes = primes;
        this.primesNumber = primesNumber;
        this.chunks = chunks;
    }

    public List<Long> getPrimes() {
        return primes;
    }

    public long size() {
        return primesNumber;
    }

    public long getPrimesNumber() {
        return primesNumber;
    }

    public List<ChunkResult> getChunks() {
        return chunks;
    }

    @Override
    public String toString() {
        final int maxLen = 100;
        return "SieveResult [primes=" + (primes != null ? primes.subList(0, Math.min(primes.size(), maxLen)) : null) + ", primesNumber="
                + primesNumber + ", chunks=" + (chunks != null ? chunks.subList(0, Math.min(chunks.size(), maxLen)) : null) + "]";
    }


    
    
    
    
}