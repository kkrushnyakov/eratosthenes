package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Collections;
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

    private static Logger log = LoggerFactory.getLogger(Sieve.class);

    private long maxNumber;

    private long summaryChunksSizeLimit;

    private int threadsNumber;

    private static int[] piece = new int[] { 0, 1 };

    private List<BlockingQueue<long[]>> primesPipes;

    public static final Map<Long, Integer> QUEUE_ARRAY_SIZES_MAP = Map.of(
            100l, 1,
            1000l, 4,
            10000l, 16,
            100000l, 64,
            1000000l, 256,
            10000000l, 4096
            );
    
    public Sieve(long maxNumber, int threadsNumber, long summaryChunksSizeLimit) {
        super();
        this.maxNumber = maxNumber;
        this.threadsNumber = threadsNumber;
        this.summaryChunksSizeLimit = summaryChunksSizeLimit;
        this.primesPipes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).size(); i++) {
            primesPipes.add(new ArrayBlockingQueue<long[]>((int) (65536))); // !!!!
        }
        primesPipes.add(null);
        primesPipes.get(0).add(new long[] { 2l });
        primesPipes.get(0).add(new long[0]);
    }

    /*
     *  result.size >=  MAX( maxNumber / chunkSize, threadsNumber) 
     *  chunkSize <= summaryChunksSizeLimit / threadsNumber
     *  result.size >=  (maxNumber * threadsNumber) / summaryChunksSizeLimit
     *  result.size >=  MAX( maxNumber / (summaryChunksSizeLimit / threadsNumber), threadsNumber) 
         
     * */
    public static List<Integer> computeChunksLengths(long maxNumber, int threadsNumber, long summaryChunksSizeLimit) {
        List<Integer> result = new ArrayList<>();
        
        int chunkSize = (int) Math.min(Integer.MAX_VALUE, Math.min(Math.ceil(summaryChunksSizeLimit / threadsNumber), Math.ceil((maxNumber + 1) / threadsNumber)));
        chunkSize = (chunkSize % 2 == 0) ? chunkSize : chunkSize - 1;
         int resultSize = (int)Math.ceil(Math.max( (maxNumber + 1) / (double)chunkSize, threadsNumber));
         return Collections.nCopies(resultSize, chunkSize);
         
    }
    
    
    public long getMaxNumber() {
        return maxNumber;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public SieveResult sieve() throws InterruptedException, ExecutionException {
        int maxChunkSize = computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).stream().mapToInt(Integer::intValue).max().getAsInt();
        int[] dataSample = new int[maxChunkSize];
        Future<ChunkResult> lastChunkResultFuture = null;
        int degree = (int) Math.floor(Math.log(maxChunkSize) / Math.log(2));

        degree = degree - (int) Math.floor(Math.log(piece.length) / Math.log(2)) + 1;
        int length = piece.length;
        System.arraycopy(piece, 0, dataSample, 0, piece.length);
        for (int i = 0; i < degree; i++) {
            System.arraycopy(dataSample, 0, dataSample, length, Math.min(length, dataSample.length - length));
            length *= 2;
        }

        Thread lastThread = null;
        List<ChunkResult> chunkResults = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(EratostheneApplication.THREADS);
        int chunksNumber = 0;
        for (int i = 0; i < computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).size(); i++) {

//             log.debug("chunk[#{} chunkSize={}, maxNumber={}]", i, computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).get(i), maxNumber);
            SieveChunk chunk = new SieveChunk(primesPipes.get(i), primesPipes.get(i + 1), i, computeChunksLengths(maxNumber, threadsNumber, summaryChunksSizeLimit).get(i), maxNumber, dataSample);
            chunksNumber++;

            log.debug("Starting chunk {}", chunk);
            lastChunkResultFuture =  executorService.submit(chunk);
//            chunkResults.add(lastChunkResultFuture.get());
            
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

        
        return new SieveResult(lastChunkResultFuture.get().getPrimes(), lastChunkResultFuture.get().size(), chunkResults);

        /*        if(primesPipes.get(primesPipes.size() - 1).size() < 1000) {
            log.debug("ALL primes = {}", primesPipes.get(primesPipes.size() - 1));
        } else {
            log.info("ALL primes size = {}", primesPipes.get(primesPipes.size() - 1).size() - 1);
        }
          */

    }

}
