/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkrushnyakov
 *
 */
public class SieveChunk implements Future<List<Long>>, Runnable {

    private static Logger log = LoggerFactory.getLogger(SieveChunk.class);

    private long maxNumber = 0;

    private int chunkIndex = 0;
    
    private int chunkSize = 0;
    
    private BlockingQueue<Long> primesIn;
    
    private final BlockingQueue<Long> primesOut;

    private int[] data;

    /**
     * 
     * @param primes
     * @param chunkIndex
     * @param chunkSize должна быть степенью двойки!
     * @param maxNumber
     */
    
    public SieveChunk(BlockingQueue<Long> primesIn, BlockingQueue<Long> primesOut, int chunkIndex, int chunkSize, long maxNumber, int[] dataSample) {
        this.data = dataSample;
        this.chunkSize = chunkSize;
        this.chunkIndex = chunkIndex;
        this.maxNumber = maxNumber;
        if(primesIn == null) throw new IllegalArgumentException("PrimesIn can't be null!");
//        if(primesOut == null) throw new IllegalArgumentException("PrimesOut can't be null!");
        this.primesIn = primesIn;
        this.primesOut = primesOut;
//        java.util.concurrent.BlockingQueue<Integer> out = new ;
        
    }

    public List<Long> countPrimes() throws InterruptedException {
        
        List<Long> resultPrimes = new ArrayList<>(); 
        if (primesOut == null) { 
            resultPrimes = new ArrayList<>((int)Sieve.primeNumbersPerMaxNumber(maxNumber));
        }
        
// Отмечаем в решете простые числа из предыдущего чанка
        while(true) {
           long currentPrime = primesIn.take();
//        for(long currentPrime: primesIn) {
            if (currentPrime == 0 ) break;
            if(primesOut == null) {
                resultPrimes.add(currentPrime);
            } else {
                primesOut.put(currentPrime);
            }
            if (currentPrime == 2) continue;
//            log.debug("<< (((chunkLength[{}] * (long) chunkIndex[{}]) / currentPrime[{}])[{}] * currentPrime[{}] + 1 ) = {}", chunkLength, chunkIndex, currentPrime, (chunkLength * (long) chunkIndex) / currentPrime, currentPrime, (int) (((chunkLength * (long) chunkIndex) / currentPrime) * currentPrime) % chunkLength);
            
            for (int i = (int) (((chunkSize * (long) chunkIndex ) / currentPrime + 1) * currentPrime) - chunkIndex * chunkSize; i < chunkSize && (chunkSize * (long) chunkIndex) + i < maxNumber; i += (int) currentPrime) { 
                data[i] = 0;
//                log.debug(" << {}: {}({}) = 0", currentPrime, i, (chunkLength * (long) chunkIndex) + i);
            }
        }
        
        
        long currentPrime = (chunkIndex == 0 ? 3 : (chunkSize * (long) chunkIndex) + 1);

        
// Ищем новые простые числа в текущем чанке        
        while (currentPrime < (chunkIndex + 1) * chunkSize && currentPrime < maxNumber) {
            if (data[(int) (currentPrime) % chunkSize] == 1) {
                if(primesOut == null) {
                    resultPrimes.add(currentPrime);
                } else {
                    primesOut.put(currentPrime);
                }

                for (int i = (int) (currentPrime) % chunkSize; i < chunkSize; i += (int) currentPrime) {
                    data[i] = 0;
//                    log.debug(" >> {}({}) = 0", i, chunkLength * ((long) chunkIndex ) + i);
                }
//                log.debug(">>(currentPrime[{}] - 1) % chunkLength[{}] = {}", currentPrime, chunkLength, (currentPrime) % chunkLength);
            }
            currentPrime++;
            
        }
        if(primesOut != null) {
            primesOut.put(0l);
        }

        


        
        return resultPrimes;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Long> get() throws InterruptedException, ExecutionException {
        
        return countPrimes();
    }

    @Override
    public List<Long> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();      
//        return countPrimes();
    }

    @Override
    public void run() {
        try {
            List<Long> result = countPrimes();
            if (!result.isEmpty()) {
                if(result.size() < 100) {
                    log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunkSize, maxNumber, result);
                } else
                {
                    log.debug("chunk[#{} chunkSize={}, maxNumber={}] = {}", chunkIndex, chunkSize, maxNumber, result.size());
                }
            }
            
        } catch (InterruptedException e) {
            log.error("{} interrupted", Thread.currentThread().getName());
        }
    }
}
