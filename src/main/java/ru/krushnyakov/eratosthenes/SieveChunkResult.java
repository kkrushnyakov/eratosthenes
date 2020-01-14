package ru.krushnyakov.eratosthenes;

public class SieveChunkResult {
 
    public long[] primes;
    
    public int primesNumber;
    
    public boolean hasPrimes;

    public SieveChunkResult(long[] primes, int primesNumber, boolean hasPrimes) {
        super();
        this.primes = primes;
        this.primesNumber = primesNumber;
        this.hasPrimes = hasPrimes;
    }

    public long[] getPrimes() {
        return primes;
    }

    public boolean hasPrimes() {
        return hasPrimes;
    }

    public int getPrimesNumber() {
        return primesNumber;
    }

}