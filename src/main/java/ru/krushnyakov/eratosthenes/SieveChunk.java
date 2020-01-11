/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkrushnyakov
 *
 */
public class SieveChunk implements Callable<ChunkResult> {

    private static Logger log = LoggerFactory.getLogger(SieveChunk.class);

    private long maxNumber = 0;

    private int chunkIndex = 0;
    
    private int chunksTotal = 0;

    private int chunkSize = 0;

    private BlockingQueue<long[]> primesIn;

    private final BlockingQueue<long[]> primesOut;

    private int[] dataSample;
    
    private int[] data;
    
    private ChunkResult result;

//    private AtomicBoolean done;
    
    /**
     * 
     * @param primes
     * @param chunkIndex
     * @param chunkSize должна быть степенью двойки!
     * @param maxNumber
     */

    public SieveChunk(BlockingQueue<long[]> primesIn, BlockingQueue<long[]> primesOut, int chunkIndex, int chunksTotal, int chunkSize, long maxNumber,
            int[] dataSample) {
        this.dataSample = dataSample;
        this.chunkSize = chunkSize;
        this.chunkIndex = chunkIndex;
        this.chunksTotal = chunksTotal;
        this.maxNumber = maxNumber;
        if (primesIn == null)
            throw new IllegalArgumentException("PrimesIn can't be null!");
        this.primesIn = primesIn;
        this.primesOut = primesOut;

    }

    public ChunkResult countPrimes() throws InterruptedException {

        long chunkSizeMultipliedByChunkIndex = chunkSize * (long)chunkIndex;
        
        data = new int[dataSample.length];
        System.arraycopy(dataSample, 0, data, 0, dataSample.length);

        
        List<long[]> resultPrimes = new ArrayList<>();
        long resultPrimesCounter = 0;

        if (primesOut == null) {
            if (maxNumber < 1000) resultPrimes = new ArrayList<>(192);
        }

        // Отмечаем в решете простые числа из предыдущего чанка
        long currentPrime = 0;
        while (true) {
            long[] currentPrimesPortion = primesIn.take();

//            log.debug("[{}]took {}", chunkIndex, Arrays.toString(currentPrimesPortion));

            if (currentPrimesPortion.length == 0)
                break;
            resultPrimesCounter += currentPrimesPortion.length;
            if (primesOut == null) {
                if (maxNumber < 1000) resultPrimes.add(currentPrimesPortion);
            } else {
//                log.debug("[{}]put {}", chunkIndex, Arrays.toString(currentPrimesPortion));
                primesOut.put(currentPrimesPortion);
            }
            for (int arrayIndex = 0; arrayIndex < currentPrimesPortion.length; arrayIndex++) {
                currentPrime = currentPrimesPortion[arrayIndex];
                if (currentPrime == 2)
                    continue;
                if (currentPrime == 0)
                    break;

                
                if(chunkIndex == 0 ) {
                    for(long i = currentPrime; i < maxNumber && i < chunkSize; i += currentPrime) {
                        data[(int)i] = 0;
                    }
                 } else {
                     
                     if (currentPrime < chunkSize) {
                         for(long i = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex; chunkSizeMultipliedByChunkIndex + i < maxNumber && i < chunkSize; i += currentPrime) {
                             data[(int)i] = 0;
                         }
                     } else {
                         long l = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex;
                         if (l < chunkSize) {
                             data[(int)l] = 0;
                         }
                         
                     }
                      
                 }
            }
        }

        currentPrime = (chunkIndex == 0 ? 3 : (chunkSize * (long) chunkIndex) + 1);

        // Ищем новые простые числа в текущем чанке
        int transitionArrayInitialLength = 0;
        int indexInTransitionArray = 0;
        long[] transitionArray = new long[] {};

        while (currentPrime < (chunkIndex + 1) * (long)chunkSize && currentPrime <= maxNumber) {

            int ii = (int)((chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex);
            if (data[ii] == 1) {

                if (indexInTransitionArray >= transitionArray.length) {

                    transitionArrayInitialLength = transitionArraySize(currentPrime);

                    if (transitionArray.length > 0) {
                        resultPrimesCounter += transitionArray.length;
                        if (primesOut == null) {
                            if (maxNumber < 1000) resultPrimes.add(transitionArray);
                        } else {
//                            log.debug("[{}]put transitionArray = {}", chunkIndex, Arrays.toString(transitionArray));
                            primesOut.put(transitionArray);
                        }
                    }
                    transitionArray = new long[transitionArrayInitialLength];
                    indexInTransitionArray = 0;
                }

                if(chunkIndex == 0 ) {
                    for(long i = currentPrime; i < maxNumber && i < chunkSize; i += currentPrime) {
                        data[(int)i] = 0;
                    }
                 } else {
                     
                     if (currentPrime < chunkSize) {
                         for(long i = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex; chunkSizeMultipliedByChunkIndex + i < maxNumber && i < chunkSize; i += currentPrime) {
                             data[(int)i] = 0;
                         }
                     } else {
                         long l = (chunkSizeMultipliedByChunkIndex / currentPrime + 1) * currentPrime - chunkSizeMultipliedByChunkIndex;
                         if (l < chunkSize) {
                             data[(int)l] = 0;
                         }
                     }
                      
                 }                
                
                transitionArray[indexInTransitionArray] = currentPrime;
//                log.debug("[{}]transitionArray[{}] = {};", chunkIndex, indexInTransitionArray, currentPrime);
                indexInTransitionArray++;
            }

            currentPrime++;

        }

        long[] finalArray = new long[indexInTransitionArray];

        if (indexInTransitionArray <= transitionArray.length) {
            System.arraycopy(transitionArray, 0, finalArray, 0, indexInTransitionArray );
            resultPrimesCounter += finalArray.length;
            if (primesOut == null) {
                if (maxNumber < 1000) resultPrimes.add(finalArray);
            } else {
                primesOut.put(finalArray);
//                log.debug("[{}]put finalArray = {}", chunkIndex, Arrays.toString(finalArray));
            }
        }

        if (primesOut != null) {
            primesOut.put(new long[0]);
//            log.debug("put [null] array");
        }

        List<Long> resultPrimesList= resultPrimes.stream().flatMapToLong(Arrays::stream).boxed().filter(l -> l != 0).collect(Collectors.toList());
        
        setResult(new ChunkResult(resultPrimesList, resultPrimesCounter));
         
         
        if (getResult().size() > 0 && getResult().size() < 1000) {
            log.debug("result = {}", getResult());
//            log.debug("result.size() = {}", getResult().size());
        } else if (getResult().size() > 0) {
//            log.debug("result.size() = {}", getResult().size());
        }
        return result;
    }

    /**
     * @param currentPrime
     * @return
     */
    public static int transitionArraySize(long currentPrime) {
        if (currentPrime < 100l) {
            return Sieve.QUEUE_ARRAY_SIZES_MAP.get(100l);
        } else if (currentPrime < 1_000l) { 
            return Sieve.QUEUE_ARRAY_SIZES_MAP.get(1_000l);
        } else if (currentPrime < 10_000l) { 
            return Sieve.QUEUE_ARRAY_SIZES_MAP.get(10_000l);
        } else if (currentPrime < 100_000l) { 
            return Sieve.QUEUE_ARRAY_SIZES_MAP.get(100_000l);
        } else return Sieve.QUEUE_ARRAY_SIZES_MAP.get(0l);
    }

    public synchronized ChunkResult getResult() {
        return result;
    }

    private synchronized void setResult(ChunkResult result) {
        this.result = result;
    }

    public void run() {
        
    }

    @Override
    public String toString() {
        return "SieveChunk [maxNumber=" + maxNumber + ", chunkIndex=" + chunkIndex + ", chunkSize=" + chunkSize + ", primesIn=" + primesIn
                + ", primesOut=" + primesOut + "]";
    }

    @Override
    public ChunkResult call() throws Exception {
        try {
            ChunkResult result = countPrimes();
                if (result.size() < 100) {
                    log.debug("chunk[#{}/{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunksTotal, chunkSize, maxNumber, result);
                } else {
                    log.debug("chunk[#{}/{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunksTotal, chunkSize, maxNumber, result.size());
                }

        } catch (InterruptedException e) {
            log.error("{} interrupted", Thread.currentThread().getName());
        }
        return result;
    }


}
