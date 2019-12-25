/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
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
    public void countChunkSizeTest() {
        assertEquals(1024 * 1024 * 1024, Sieve.countChunkSize(8_000_000_000l, 4));
        assertEquals(1024 * 1024 * 1024, Sieve.countChunkSize(8_000_000_000l, 8));
        assertEquals(128 * 1024 * 1024, Sieve.countChunkSize(1_000_000_000l, 8));
        assertEquals(16, Sieve.countChunkSize(100l, 8));
    }

    @Test
    public void primeNumbersPerMaxNumberTest() {
        assertEquals(26, Sieve.primeNumbersPerMaxNumber(100));
        assertEquals(500_000_000l, Sieve.primeNumbersPerMaxNumber(10_000_000_000l));
        assertEquals(60_000_000l, Sieve.primeNumbersPerMaxNumber(1_000_000_000l));
    }

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
    public void countPrimesTest() {
        assertEquals(0, 0);
    }
    
}
