/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author kkrushnyakov
 *
 */
public class SieveTest {

    @Test
    public void countChunkSizeTest() {
        assertEquals(256 * 1024 * 1024, Sieve.countChunkSize(8_000_000_000l, 4));
        assertEquals(256 * 1024 * 1024, Sieve.countChunkSize(8_000_000_000l, 8));
        assertEquals(128 * 1024 * 1024, Sieve.countChunkSize(1_000_000_000l, 8));
        assertEquals(16 , Sieve.countChunkSize(100l, 8));
    }
    
    @Test
    public void primeNumbersPerMaxNumberTest() {
        assertEquals(26, Sieve.primeNumbersPerMaxNumber(100));
        assertEquals(500_000_000l, Sieve.primeNumbersPerMaxNumber(10_000_000_000l));
    }
}
