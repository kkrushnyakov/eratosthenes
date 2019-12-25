package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sieve {

    public final static int SIEVE_CHUNK_SIZE_LIMIT = 16 * 1024 * 1024;

    private static Logger log = LoggerFactory.getLogger(Sieve.class);

    private long maxNumber;

    private int threadsNumber;

    private int chunkSize;

    public static final Map<Integer, Integer> PRIMES_DENSITY_DISTRIBUTION_MAP = Collections.unmodifiableMap(new HashMap<>() {
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
    });

    // piece.length == 16
    private static int[] piece = new int[] { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 };
    // private static int[] piece = new int[] {0,1,0,1};

    // private List<Long> primes;

    private List<BlockingQueue<long[]>> primesPipes;

    public Sieve(long maxNumber, int threadsNumber, int chunkSize) {
        this(maxNumber, threadsNumber);
        this.chunkSize = chunkSize;
    }

    public Sieve(long maxNumber, int threadsNumber) {
        super();
        this.maxNumber = maxNumber;
        this.threadsNumber = threadsNumber;
        // this.primes = new ArrayList<>((int)(maxNumber / 64));
        this.primesPipes = Collections.synchronizedList(new ArrayList<>());
        this.chunkSize = countChunkSize(maxNumber, threadsNumber);

        for (int i = 0; i < (int) Math.ceil(maxNumber / (double) countChunkSize(maxNumber, threadsNumber)); i++) {
            // primesPipes.add(new
            // ArrayBlockingQueue<Long>((int)(primeNumbersPerMaxNumber(maxNumber) / 100)));
            // // !!!!
            primesPipes.add(new ArrayBlockingQueue<long[]>((int) (1024))); // !!!!
        }
        primesPipes.add(null);
        primesPipes.get(0).add(new long[] { 2l });
        primesPipes.get(0).add(new long[0]);
    }

    public static int countChunkSize(long maxNumber, int threadsNumber) {
        int result = piece.length;

//        while (result < Math.min(maxNumber / (2 * threadsNumber), SIEVE_CHUNK_SIZE_LIMIT)) {
            while (result < Math.min(maxNumber / threadsNumber, SIEVE_CHUNK_SIZE_LIMIT)) {
            if (result * 2 > SIEVE_CHUNK_SIZE_LIMIT)
                break;
            result *= 2;
        }
        return result;
    }

    /**
     * Верхняя оценка ожидаемого количества простых чисел
     * @param maxNumber
     * @return
     */

    public static long primeNumbersPerMaxNumber(long maxNumber) {

        return maxNumber / 100 *
        // https://en.wikipedia.org/wiki/Prime-counting_function
        // процентное отношение количества простых чисел к числовому порядку предела
        // поиска
                PRIMES_DENSITY_DISTRIBUTION_MAP.get(Math.log10(maxNumber) > 15 ? 15 : (int) Math.ceil(Math.log10(maxNumber)));

    }

    public long getMaxNumber() {
        return maxNumber;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public SieveResult sieve() throws InterruptedException, ExecutionException {
        int[] dataSample = new int[chunkSize];
        Future<ChunkResult> lastChunkResultFuture = null;
        int degree = (int) Math.floor(Math.log(chunkSize) / Math.log(2));

        degree = degree - (int) Math.floor(Math.log(piece.length) / Math.log(2));
        int length = piece.length;
        System.arraycopy(piece, 0, dataSample, 0, piece.length);
        for (int i = 0; i < degree; i++) {
            System.arraycopy(dataSample, 0, dataSample, length, length);
            length *= 2;
        }

        log.debug("chunkSize = {}, dataSample.length = {}", chunkSize, dataSample.length);

        Thread lastThread = null;
        // log.debug("chunk[#{} chunkSize={}, maxNumber={}]", i, chunkSize, maxNumber);

        ExecutorService executorService = Executors.newFixedThreadPool(EratostheneApplication.THREADS);
        int chunksNumber = 0;
        for (int i = 0; i * chunkSize < maxNumber; i++) {
            int[] initData = new int[dataSample.length];
            System.arraycopy(dataSample, 0, initData, 0, dataSample.length);

            // log.debug("chunk[#{} chunkSize={}, maxNumber={}]", i, chunkSize, maxNumber);
            SieveChunk chunk = new SieveChunk(primesPipes.get(i), primesPipes.get(i + 1), i, chunkSize, maxNumber, initData);
            chunksNumber++;

            log.debug("Starting chunk {}", chunk);
            lastChunkResultFuture =  executorService.submit(chunk);

            
            // lastThread = new Thread(chunk);
            // lastThread.start();

        }
        executorService.shutdown();
        
        while(!lastChunkResultFuture.isDone()) {
            Thread.sleep(100);
        }


        /*        try {
            lastThread.join();
        } catch (InterruptedException e) {
            log.error("{} thread is interrupted!", lastThread.getName());
        }
        */ 

        
        return new SieveResult(lastChunkResultFuture.get().getPrimes(), lastChunkResultFuture.get().size(), chunksNumber, chunkSize);

        /*        if(primesPipes.get(primesPipes.size() - 1).size() < 1000) {
            log.debug("ALL primes = {}", primesPipes.get(primesPipes.size() - 1));
        } else {
            log.info("ALL primes size = {}", primesPipes.get(primesPipes.size() - 1).size() - 1);
        }
          */

    }

}
