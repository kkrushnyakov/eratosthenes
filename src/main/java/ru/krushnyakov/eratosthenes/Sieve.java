package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sieve implements Runnable {
    
    
    private static Logger log = LoggerFactory.getLogger(Sieve.class);
    
    private long maxNumber;
    
    private int threads;

    private int chunkSize;
    
    private List<Long> primes;
    
    public Sieve(long maxNumber, int chunkSize, int threads) {
        super();
        this.maxNumber = maxNumber;
        this.threads = threads;
        this.chunkSize = chunkSize;
        this.primes = new ArrayList<>();
    }

    @Override
    public void run() {
        int[] data = new int[chunkSize];
//        int[] piece = new int[INIT_PIECE_SIZE];
        int[] piece = new int[] {0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1}; // piece.length == 16
//        Arrays.fill(piece, 1);
        
        int degree = (int) Math.floor(Math.log(chunkSize) / Math.log(2));

        degree = degree - (int) Math.floor(Math.log(piece.length) / Math.log(2)); 
        int length = piece.length;
        System.arraycopy(piece, 0, data, 0, piece.length);
        for (int i = 0; i < degree; i++) {
            System.arraycopy(data, 0, data, length, length);
            length *= 2;
        }
        System.arraycopy(data, 0, data, length, data.length - length);
        
        primes.add(2l);
        for(int i = 0; i  * chunkSize < maxNumber; i ++) {
//            log.debug("chunk[#{}  chunkSize={}, maxNumber={}]", i, chunkSize, maxNumber);
            List chunkPrimes = new SieveChunk(primes, i, chunkSize, maxNumber, data).countPrimes();
            if(chunkPrimes.size() < 100) {
                log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", i, chunkSize, maxNumber, chunkPrimes);
            } else
            {
                log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", i, chunkSize, maxNumber, chunkPrimes.size());
            }
            primes.addAll(chunkPrimes);
        }
        
        if(primes.size() < 1000) {
            log.debug("ALL primes = {}", primes);
        } else {
            log.info("ALL primes size = {}", primes.size());
        }
            
        
    }

}
