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

    private boolean[] dataSample;

    private static final int ARRAY_SIZE = 5592404; 
    
    @Before
    public void init() {
        
       dataSample = new boolean[ARRAY_SIZE];
    }

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void arrayLoopTest() {

        for (int step : new int[] {2,3,5,7,11,13}) {

            StopWatch watch = new StopWatch();
            watch.start();
            for (int i = 0; i < dataSample.length; i += step) {
                this.dataSample[i] = true;
            }

            watch.stop();
            log.debug("step = {}, time = {}", step, watch.getTotalTimeMillis());

        }
    }

   

}