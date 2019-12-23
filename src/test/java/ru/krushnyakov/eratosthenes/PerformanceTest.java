/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * @author kkrushnyakov
 *
 */
public class PerformanceTest {

    long[] dataSample;
    
    @Before
    public void init() {
        dataSample = new long[EratostheneApplication.SIEVE_CHUNK_SIZE_LIMIT];
    }

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void arrayLoopTest() {

        for (int step = 2; step < EratostheneApplication.SIEVE_CHUNK_SIZE_LIMIT / 10; step *= 2) {

            StopWatch watch = new StopWatch();
            watch.start();
            for (int i = 0; i < dataSample.length; i += step * 3) {
                this.dataSample[i] = 1;
            }

            watch.stop();
            log.debug("step = {}, time = {}", step, watch.prettyPrint());

        }
    }

}
