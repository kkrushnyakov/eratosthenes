/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkrushnyakov
 *
 */
public class SieveChunk implements Runnable {

    private static Logger log = LoggerFactory.getLogger(SieveChunk.class);

    private long maxNumber = 0;

    private int chunkIndex = 0;

    private int chunkSize = 0;

    private BlockingQueue<long[]> primesIn;

    private final BlockingQueue<long[]> primesOut;

    private int[] data;

    /**
     * 
     * @param primes
     * @param chunkIndex
     * @param chunkSize должна быть степенью двойки!
     * @param maxNumber
     */

    public SieveChunk(BlockingQueue<long[]> primesIn, BlockingQueue<long[]> primesOut, int chunkIndex, int chunkSize, long maxNumber,
            int[] dataSample) {
        this.data = dataSample;
        this.chunkSize = chunkSize;
        this.chunkIndex = chunkIndex;
        this.maxNumber = maxNumber;
        if (primesIn == null)
            throw new IllegalArgumentException("PrimesIn can't be null!");
        // if(primesOut == null) throw new IllegalArgumentException("PrimesOut can't be
        // null!");
        this.primesIn = primesIn;
        this.primesOut = primesOut;
        // java.util.concurrent.BlockingQueue<Integer> out = new ;

    }

    public List<Long> countPrimes() throws InterruptedException {

        List<long[]> resultPrimes = new ArrayList<>();
        if (primesOut == null) {
            resultPrimes = new ArrayList<>((int) Sieve.primeNumbersPerMaxNumber(maxNumber));
        }

        // Отмечаем в решете простые числа из предыдущего чанка
        long currentPrime = 2;
        while (true) {
            long[] currentPrimesPortion = primesIn.take();

            log.debug("took {}", Arrays.toString(currentPrimesPortion));
            
            if (currentPrimesPortion.length == 0)
                break;
            if (primesOut == null) {
                resultPrimes.add(currentPrimesPortion);
            } else {
                    primesOut.put(currentPrimesPortion);
            }
            for (int arrayIndex = 0; arrayIndex < currentPrimesPortion.length; arrayIndex++) {
                currentPrime = currentPrimesPortion[arrayIndex];
                if (currentPrime == 2)
                    continue;

                for (int i = (int) (((chunkSize * (long) chunkIndex) / currentPrime + 1) * currentPrime)
                        - chunkIndex * chunkSize; i < chunkSize
                                && (chunkSize * (long) chunkIndex) + i < maxNumber; i += (int) currentPrime) {
                    data[i] = 0;
                    log.debug(" - <{}>> {}({}) = 0", currentPrime, i, chunkSize * ((long) chunkIndex) + i);
                }
            }
        }

//        long currentPrime = (chunkIndex == 0 ? 3 : (chunkSize * (long) chunkIndex) + 1);

        // Ищем новые простые числа в текущем чанке
        int transitionArrayLength = 0;
        int indexInTransitionArray = 0;
        long[] transitionArray = new long[] {};

        while (currentPrime < (chunkIndex + 1) * chunkSize && currentPrime < maxNumber) {

            if (data[(int) (currentPrime) % chunkSize] == 1) {

                if (indexInTransitionArray == transitionArrayLength) {

                    if (currentPrime < 100) {
                        transitionArrayLength = 2;
                    } else {
                        transitionArrayLength = 32;
                    }

                    if (transitionArray.length > 0) {
                        if (primesOut == null) {
                            resultPrimes.add(transitionArray);
                        } else {
                            primesOut.put(transitionArray);
                            log.debug("put transitionArray = {}", Arrays.toString(transitionArray));
                        }
                    }
                    transitionArray = new long[transitionArrayLength];
                    indexInTransitionArray = 0;
                }

                for (int i = (int) (currentPrime) % chunkSize; i < chunkSize; i += (int) currentPrime) {
                    data[i] = 0;
                    log.debug(" + <{}>> {}({}) = 0", currentPrime, i, chunkSize * ((long) chunkIndex) + i);
                }
                transitionArray[indexInTransitionArray] = currentPrime;
                log.debug("transitionArray[{}] = {};", indexInTransitionArray, currentPrime);
                indexInTransitionArray++;
                log.debug(">>(currentPrime[{}]) % chunkSize[{}] = {}", currentPrime, chunkSize, (currentPrime) % chunkSize);
            }

            currentPrime++;

        }

        long[] finalArray = new long[indexInTransitionArray + 1];

        if (indexInTransitionArray < transitionArray.length - 1) {
            System.arraycopy(transitionArray, 0, finalArray, 0, indexInTransitionArray + 1);
            if (primesOut == null) {
                resultPrimes.add(finalArray);
            } else {
                primesOut.put(finalArray);
            }
        }

        if (primesOut != null) {
            primesOut.put(new long[0]);
        }
       
        List<Long> result = resultPrimes.stream().flatMapToLong(Arrays::stream).boxed().collect(Collectors.toList());
        if (result.size() > 0 && result.size() < 1000) {
            log.debug("result = {}", result);
        } else if (result.size() > 0) {
            log.debug("result.size() = {}", result.size());
        }
        return result;
    }

    @Override
    public void run() {
        try {
            List<Long> result = countPrimes();
            if (!result.isEmpty()) {
                if (result.size() < 100) {
                    log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunkSize, maxNumber, result);
                } else {
                    log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunkSize, maxNumber, result.size());
                }
            }

        } catch (InterruptedException e) {
            log.error("{} interrupted", Thread.currentThread().getName());
        }
    }

    @Override
    public String toString() {
        return "SieveChunk [maxNumber=" + maxNumber + ", chunkIndex=" + chunkIndex + ", chunkSize=" + chunkSize + ", primesIn=" + primesIn
                + ", primesOut=" + primesOut + "]";
    }
    
    
}
