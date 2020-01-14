package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sieve {

    private static Logger log = LoggerFactory.getLogger(Sieve.class);

    private long maxNumber;

    private long summaryChunksSizeLimit;

    private int threadsNumber;

    private long[] initPrimes = new long[] { 2l };
    
    private static boolean[] piece = new boolean[] { false, true };
    
    private Map<Integer, Future<long[]>> primesMap = new ConcurrentHashMap<>(); //?


    public Sieve(long maxNumber, int threadsNumber, long summaryChunksSizeLimit) {
        super();
        this.maxNumber = maxNumber;
        this.threadsNumber = threadsNumber;
        this.summaryChunksSizeLimit = summaryChunksSizeLimit;

        for (int i = 0; i < computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).size(); i++) {

        }
    }

    /*
     *  result.size >=  MAX( maxNumber / chunkSize, threadsNumber) 
     *  chunkSize <= summaryChunksSizeLimit / threadsNumber
     *  result.size >=  (maxNumber * threadsNumber) / summaryChunksSizeLimit
     *  result.size >=  MAX( maxNumber / (summaryChunksSizeLimit / threadsNumber), threadsNumber) 
         
     * */
    public static List<Integer> computeChunksLengths(long maxNumber, int threadsNumber, long summaryChunksSizeLimit) {
        List<Integer> result = new ArrayList<>();

        int chunkSize = (int) Math.min(Integer.MAX_VALUE,
                Math.min(Math.ceil(summaryChunksSizeLimit / threadsNumber), Math.ceil((maxNumber + 1) / threadsNumber)));
        chunkSize = (chunkSize % 2 == 0) ? chunkSize : chunkSize - 1;
        int resultSize = (int) Math.ceil(Math.max((maxNumber + 1) / (double) chunkSize, threadsNumber));
        return Collections.nCopies(resultSize, chunkSize);

    }
    
    

    public long[] getInitPrimes() {
        return initPrimes;
    }

    public long getMaxNumber() {
        return maxNumber;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }
    

    public Map<Integer, Future<long[]>> getPrimesMap() {
        return primesMap;
    }

    public SieveResult sieve() throws InterruptedException, ExecutionException {
        int maxChunkSize = computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).stream().mapToInt(Integer::intValue).max()
                .getAsInt();
        boolean[] dataSample = new boolean[maxChunkSize];
        Future<long[]> lastFuture = null;
        int degree = (int) Math.floor(Math.log(maxChunkSize) / Math.log(2));

        degree = degree - (int) Math.floor(Math.log(piece.length) / Math.log(2)) + 1;
        int length = piece.length;
        System.arraycopy(piece, 0, dataSample, 0, piece.length);
        for (int i = 0; i < degree; i++) {
            System.arraycopy(dataSample, 0, dataSample, length, Math.min(length, dataSample.length - length));
            length *= 2;
        }

        Thread lastThread = null;

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);
        int chunksNumber = 0;
        for (int i = 0; i < computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).size(); i++) {

            // log.debug("chunk[#{} chunkSize={}, maxNumber={}]", i,
            // computeChunksLengths(maxNumber, threadsNumber,
            // summaryChunksSizeLimit).get(i), maxNumber);
            SieveChunk chunk = new SieveChunk(this, i, computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).size() - 1,
                    computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).get(i), maxNumber, dataSample);
            chunksNumber++;

            // log.debug("Starting chunk {}", chunk);
            
            lastFuture = executorService.submit(chunk);
            primesMap.put(i, lastFuture);

        }
        executorService.shutdown();

        while (!lastFuture.isDone()) {
            Thread.sleep(10);
        }

        long[][] result = new long[primesMap.values().size() + 1][];
        
            result[0] = initPrimes;
            
            for (int i = 0; i < primesMap.values().size(); i++) {
                result[i + 1] = primesMap.get(i).get();
            }
        
        return new SieveResult(result);

    }

    /**
     * Верхняя оценка ожидаемого количества простых чисел
     * @param maxNumber
     * @return
     */

    public static long primeNumbersPerMaxNumber(long maxNumber) {

        return (long)(maxNumber / 100.0 *
        // https://en.wikipedia.org/wiki/Prime-counting_function
        // процентное отношение количества простых чисел к числовому порядку предела
        // поиска
                new HashMap<Integer, Integer>() {
                    private static final long serialVersionUID = 1L;
                    {
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

                    }
                }.get(Math.log10(maxNumber) > 15 ? 15 : (int) Math.ceil(Math.log10(maxNumber)) - 1));
    }

        

}
