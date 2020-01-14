/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkrushnyakov
 *
 */
public class SieveChunk implements Callable<long[]> {

    private static Logger log = LoggerFactory.getLogger(SieveChunk.class);

    private long maxNumber = 0;

    private int chunkIndex = 0;

    private int chunksTotal = 0;

    private int chunkSize = 0;

    private final boolean[] dataSample;

    private boolean[] data;

    private long[] primes;

    private Sieve sieve;

    // private AtomicBoolean done;

    /**
     * 
     * @param primes
     * @param chunkIndex
     * @param chunkSize должна быть степенью двойки!
     * @param maxNumber
     */

    public SieveChunk(Sieve sieve, int chunkIndex, int chunksTotal, int chunkSize, long maxNumber, final boolean[] dataSample) {
        this.dataSample = dataSample;
        this.chunkSize = chunkSize;
        this.chunkIndex = chunkIndex;
        this.chunksTotal = chunksTotal;
        this.maxNumber = maxNumber;
        this.sieve = sieve;

    }

    public long[] countPrimes() throws InterruptedException, ExecutionException {

        long chunkSizeMultipliedByChunkIndex = chunkSize * (long) chunkIndex;

        data = new boolean[dataSample.length];
        System.arraycopy(dataSample, 0, data, 0, dataSample.length);

        long[] resultPrimes = new long[(int) Sieve.primeNumbersPerMaxNumber(chunkSize)];
        
        int resultPrimesCounter = 0;

        
        // Отмечаем в решете простые числа из предыдущего чанка
        // while (true) {
//        final StopWatch oldStopWatch = new StopWatch("old");

//        oldStopWatch.start();
        for (int importChunkIndex = 0; importChunkIndex < Math.ceil(chunkIndex / 2.0); importChunkIndex++) {

            for (long currentPrime : sieve.getPrimesMap().get(importChunkIndex).get()) {

                /*                if(Arrays.asList(2l).contains(currentPrime)) {
                    currentPrime++;
                    continue;
                }
                */
                
                if (chunkIndex == 0) {
                    for (long i = currentPrime; i < maxNumber && i < chunkSize; i += currentPrime) {
                        data[(int) i] = false;
                    }
                } else {

                    if (currentPrime < chunkSize) {
                        for (int i = (int) ((chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime
                                - chunkSizeMultipliedByChunkIndex); chunkSizeMultipliedByChunkIndex + i < maxNumber
                                        && i < chunkSize; i += currentPrime) {
                            data[i] = false;
                        }
                    } else {
                        long l = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex;
                        if (l < chunkSize) {
                            data[(int) l] = false;
                        }

                    }

                }
            }
        }

//        oldStopWatch.stop();
//        log.debug("{} old = {}", chunkIndex, oldStopWatch.getTotalTimeMillis());
        
        long currentPrime = (chunkIndex == 0 ? 3 : (chunkSize * (long) chunkIndex) + 1);

        // Ищем новые простые числа в текущем чанке
        
//        final StopWatch newStopWatch = new StopWatch("new");

//        newStopWatch.start();


        while (currentPrime < (chunkIndex + 1) * (long) chunkSize && currentPrime <= maxNumber) {

            int ii = (int) ((chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex);
            if (data[ii] == true) {

                if (chunkIndex == 0) {
                    for (long i = currentPrime; i < maxNumber && i < chunkSize; i += currentPrime) {
                        data[(int) i] = false;
                    }
                } else {

                    if (currentPrime < chunkSize) {
                        for (long i = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime
                                - chunkSizeMultipliedByChunkIndex; chunkSizeMultipliedByChunkIndex + i < maxNumber
                                        && i < chunkSize; i += currentPrime) {
                            data[(int) i] = false;
                        }
                    } else {
                        long l = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex;
                        if (l < chunkSize) {
                            data[(int) l] = false;
                        }
                    }

                }
             resultPrimes[resultPrimesCounter] = currentPrime;
             resultPrimesCounter++;
            }
            currentPrime++;
        }

//        newStopWatch.stop();
//        log.debug("{} new = {}", chunkIndex, newStopWatch.getTotalTimeMillis());

        
        // List<Long> resultPrimesList=
        // resultPrimes.stream().flatMapToLong(Arrays::stream).boxed().filter(l -> l !=
        // 0).collect(Collectors.toList());

        // setResult(new ChunkResult(resultPrimes));
        primes = new long[resultPrimesCounter];
        System.arraycopy(resultPrimes, 0, primes, 0, resultPrimesCounter);
        return primes;
    }


    @Override
    public long[] call() throws Exception {
        long[] result = null;

        try {
            result = countPrimes();
            if (result.length < 100) {
                log.debug("chunk[#{}/{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunksTotal, chunkSize, maxNumber, result);
            } else {
                log.debug("chunk[#{}/{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunksTotal, chunkSize, maxNumber, result.length);
            }

        } catch (InterruptedException e) {
            log.error("{} interrupted", Thread.currentThread().getName());
        }
        return result;
    }

}
