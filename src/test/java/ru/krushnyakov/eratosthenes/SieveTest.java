/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkrushnyakov
 *
 */
public class SieveTest {

    public static final Logger log = LoggerFactory.getLogger(SieveTest.class);


 

    @Test
    public void longArraysToLongList() {

        List<long[]> arraysList = Arrays.asList(

                new long[] { 1l, 2l, 3l }, new long[] { 4l, 5l, 6l }, new long[] { 7l, 8l, 9l, 10l });
        
        

        long[] flatArray = arraysList.stream()
                .flatMapToLong(Arrays::stream)
                .toArray();
        
//        List<Long> result = arraysList.stream().map( a -> {return Arrays.asList(Arrays.asList(a).toArray(new Long[a.length])); }).reduce(new ArrayList<Long>(), (l, r) -> {l.addAll(r); return l;});
        
        
        assertEquals (Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l), arraysList.stream().flatMapToLong(Arrays::stream).boxed().collect(Collectors.toList()));
    }

    
    @Test
    public void countPrimesTest() throws InterruptedException, ExecutionException {
//        assertEquals(50847534, new Sieve(1_000_000_000l, 4).sieve().size());
//        log.debug("result1 = {}", result1);
//        assertEquals(25, new Sieve(100l, 1, 1024 * 1024 * 1024).sieve().size());
//        assertEquals(25, new Sieve(100l, 4, 1024 * 1024 * 1024).sieve().size());
        assertEquals(25, new Sieve(100l, 4, 64l).sieve().size());
//        assertEquals(25, new Sieve(100l, 24, 16).sieve().size());
        assertEquals(50847534, new Sieve(1_000_000_000l, 24, 1024 * 1024 * 1024l).sieve().size());
        assertEquals(102886526, new Sieve(2_100_000_000l, 24, 1024 * 1024 * 1024l).sieve().size());
        assertEquals(144449537, new Sieve(3_000_000_000l, 24, 1024 * 1024 * 1024l).sieve().size());
        
    }
    
    
    @Test
    public void computeChunksLengthsTest() {
        assertEquals(List.of(24, 24, 24, 24, 24),  Sieve.computeChunksLengths(100, 4, 100));
        assertEquals(List.of(24, 24, 24, 24, 24),  Sieve.computeChunksLengths(100, 4, 1073741824));
        assertEquals(List.of(12, 12, 12, 12, 12, 12, 12, 12, 12),  Sieve.computeChunksLengths(100, 4, 50));
        assertEquals(List.of(100, 100),  Sieve.computeChunksLengths(101, 1, 100));
        assertEquals(List.of(50, 50, 50),  Sieve.computeChunksLengths(100, 1, 50));
        assertEquals(Collections.nCopies(7, 16),  Sieve.computeChunksLengths(100, 4, 64));
        assertEquals(Collections.nCopies(56, 44739242),  Sieve.computeChunksLengths(2_500_000_000l, 24, 1 * 1024 * 1024 * 1024));
//        assertEquals(Collections.nCopies(26, 4),  Sieve.computeChunksLengths(100l, 24, 16));
    }
    
}
