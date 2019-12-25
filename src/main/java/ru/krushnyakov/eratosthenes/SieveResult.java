package ru.krushnyakov.eratosthenes;

import java.util.List;

public class SieveResult {

    private List<Long> primes;

    private long primesNumber;

    private int chunksNumber;
    
    private int chunkSize;
    
    public SieveResult(List<Long> primes, long primesNumber, int chunksNumber, int chunkSize) {
        super();
        this.primes = primes;
        this.primesNumber = primesNumber;
        this.chunksNumber = chunksNumber;
        this.chunkSize = chunkSize;
    }

    public List<Long> getPrimes() {
        return primes;
    }

    public long size() {
        return primesNumber;
    }

    public int getChunksNumber() {
        return chunksNumber;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    
    
}