package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sieve implements Runnable {
    
    
    private static Logger log = LoggerFactory.getLogger(Sieve.class);
    
    private long maxNumber;
    
    private int threadsNumber;

    private int chunkSize;
    
    public static final Map<Integer, Integer> PRIMES_DENSITY_DISTRIBUTION_MAP  = Collections.unmodifiableMap(new HashMap<>() {{
        put(0, 100);
        put(1, 40);
        put(2, 26);
        put(3, 17);
        put(3, 17);
        put(4, 13);
        put(5, 10);
        put(6, 8);
        put(7, 7);
        put(8, 6);
        put(9, 6);
        put(10, 5);
        put(11, 5);
        put(12, 4);
        put(13, 4);
        put(14, 4);
        put(15, 3);
    }});
    
    // piece.length == 16
    private static int[] piece = new int[] {0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1};
    
//    private List<Long> primes;

    private List<BlockingQueue<Long>> primesPipes;
    
    public Sieve(long maxNumber, int threadsNumber, int chunkSize) {
        this(maxNumber, threadsNumber);
        this.chunkSize = chunkSize;
    }
    
    public Sieve(long maxNumber, int threadsNumber) {
        super();
        this.maxNumber = maxNumber;
        this.threadsNumber = threadsNumber;
//        this.primes = new ArrayList<>((int)(maxNumber / 64));
        this.primesPipes = Collections.synchronizedList(new ArrayList<>());
        this.chunkSize = countChunkSize(maxNumber, threadsNumber);
        
        for (int i = 0; i < (int)Math.ceil(maxNumber / (double) countChunkSize(maxNumber, threadsNumber)) ; i++) {
//            primesPipes.add(new ArrayBlockingQueue<Long>((int)(primeNumbersPerMaxNumber(maxNumber) / 100))); // !!!!
            primesPipes.add(new ArrayBlockingQueue<Long>((int)(1024))); // !!!!
        }
        primesPipes.add(null);
        primesPipes.get(0).add(2l);
        primesPipes.get(0).add(0l);
    }

    public static int countChunkSize(long maxNumber, int threadsNumber) {
        int result = piece.length;
        
        while (result < Math.min(maxNumber / threadsNumber, EratostheneApplication.SIEVE_CHUNK_SIZE_LIMIT))
            {
                if (result * 2 > EratostheneApplication.SIEVE_CHUNK_SIZE_LIMIT) break; 
                result *= 2;
            }
        return result;
    }
    
    /**
     * Верхняя оценка ожидаемого количества простых чисел
     * @param maxNumber
     * @return
     */
    
    public static long primeNumbersPerMaxNumber (long maxNumber) {
        
        return maxNumber / 100 * 
                // https://en.wikipedia.org/wiki/Prime-counting_function
                // процентное отношение количества простых чисел к числовому порядку предела поиска
                PRIMES_DENSITY_DISTRIBUTION_MAP.get(Math.log10(maxNumber) > 15 ? 15 : (int)Math.ceil(Math.log10(maxNumber)));
        
    }
    
    @Override
    public void run() {
        int[] dataSample = new int[chunkSize];
//        int[] piece = new int[INIT_PIECE_SIZE];
//        Arrays.fill(piece, 1);
        
        int degree = (int) Math.floor(Math.log(chunkSize) / Math.log(2));

        degree = degree - (int) Math.floor(Math.log(piece.length) / Math.log(2)); 
        int length = piece.length;
        System.arraycopy(piece, 0, dataSample, 0, piece.length);
        for (int i = 0; i < degree; i++) {
            System.arraycopy(dataSample, 0, dataSample, length, length);
            length *= 2;
        }
//        System.arraycopy(dataSample, 0, dataSample, length, dataSample.length - length);
        
        log.debug("chunkSize = {}, dataSample.length = {}",  chunkSize, dataSample.length);
        
        Thread lastThread = null;
        
//        primes.add(2l);
        for(int i = 0; i  * chunkSize < maxNumber; i ++) {
            int [] initData = new int[dataSample.length];
            System.arraycopy(dataSample, 0, initData, 0, dataSample.length);

//            log.debug("chunk[#{}  chunkSize={}, maxNumber={}]", i, chunkSize, maxNumber);

            lastThread = new Thread(new SieveChunk(primesPipes.get(i), primesPipes.get(i + 1), i, chunkSize, maxNumber, initData));
            lastThread.start();
            
            
//            primes.addAll(chunkPrimes);
        }
        
        try {
            lastThread.join();
        } catch (InterruptedException e) {
            log.error("{} thread is interrupted!", lastThread.getName());
        }
        
        /*        if(primesPipes.get(primesPipes.size() - 1).size() < 1000) {
            log.debug("ALL primes = {}", primesPipes.get(primesPipes.size() - 1));
        } else {
            log.info("ALL primes size = {}", primesPipes.get(primesPipes.size() - 1).size() - 1);
        }
        */            
        
    }

    public int getChunkSize() {
        return chunkSize;
    }
    
    

}
