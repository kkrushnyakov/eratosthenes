/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class SieveChunk implements Future<List<Long>> {

    private static Logger log = LoggerFactory.getLogger(SieveChunk.class);

    
    public final static int INIT_PIECE_SIZE = 8;
    // 1024 * 1024;
    
    private long maxNumber = 0;

    private int chunkIndex = 0;
    
    private int chunkLength = 0;
    
    private List<Long> primes;

    private int[] data;

    /**
     * 
     * @param primes
     * @param chunkIndex
     * @param chunkLength должна быть степенью двойки!
     * @param maxNumber
     */
    
    public SieveChunk(List<Long> primes, int chunkIndex, int chunkLength, long maxNumber, int[] dataSample) {
        this.data = new int[chunkLength];
        this.chunkLength = chunkLength;
        this.chunkIndex = chunkIndex;
        this.maxNumber = maxNumber;
        if(primes == null) throw new IllegalArgumentException("Primes can't be null!");
        this.primes = new ArrayList<>(primes);

        System.arraycopy(dataSample, 0, data, 0, dataSample.length);
        if(data.length < 1000) 
            log.debug("data = {}", Arrays.toString(data));
    }

    public List<Long> countPrimes() {
        
        List<Long> newPrimes = new ArrayList<>(chunkLength / 64);
        
// Отмечаем в решете простые числа из предыдущего чанка
        
        for(long currentPrime: primes) {
            if (currentPrime == 2) continue;
//            log.debug("<< (((chunkLength[{}] * (long) chunkIndex[{}]) / currentPrime[{}])[{}] * currentPrime[{}] + 1 ) = {}", chunkLength, chunkIndex, currentPrime, (chunkLength * (long) chunkIndex) / currentPrime, currentPrime, (int) (((chunkLength * (long) chunkIndex) / currentPrime) * currentPrime) % chunkLength);
            for (int i = (int) (((chunkLength * (long) chunkIndex ) / currentPrime + 1) * currentPrime) - chunkIndex * chunkLength; i < chunkLength && (chunkLength * (long) chunkIndex) + i < maxNumber; i += (int) currentPrime) { 
                data[i] = 0;
//                log.debug(" << {}: {}({}) = 0", currentPrime, i, (chunkLength * (long) chunkIndex) + i);
            }
        }
        
        
        long currentPrime = (chunkIndex == 0 ? 3 : (chunkLength * (long) chunkIndex) + 1);

        
// Ищем новые простые числа в текущем чанке        
        while (currentPrime < (chunkIndex + 1) * chunkLength && currentPrime < maxNumber) {
            if (data[(int) (currentPrime) % chunkLength] == 1) {
                newPrimes.add(currentPrime);

                for (int i = (int) (currentPrime) % chunkLength; i < chunkLength; i += (int) currentPrime) {
                    data[i] = 0;
//                    log.debug(" >> {}({}) = 0", i, chunkLength * ((long) chunkIndex ) + i);
                }
//                log.debug(">>(currentPrime[{}] - 1) % chunkLength[{}] = {}", currentPrime, chunkLength, (currentPrime) % chunkLength);
            }
            currentPrime++;
            
        }
        return newPrimes;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Long> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }
}
