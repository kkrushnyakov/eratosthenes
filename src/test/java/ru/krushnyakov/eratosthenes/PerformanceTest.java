/**
 * 
 */
package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void blockingQueueTest() throws InterruptedException {

        StopWatch watch = new StopWatch();
        watch.start();

        int chunksNumber = 4;

        List<BlockingQueue<Long>> queues = Collections
                .unmodifiableList(Stream.generate(() -> new ArrayBlockingQueue<Long>(1)).limit(chunksNumber - 1).collect(Collectors.toList()));
        log.debug("queues = {}", queues);
        
        List<BlockingQueueClient> chunks = new ArrayList<>();

        chunks.add(new BlockingQueueClient(null, queues.get(0)));

        for (int i = 0; i < chunksNumber - 2; i++) {
            chunks.add(new BlockingQueueClient(queues.get(i), queues.get(i + 1)));
        }

        chunks.add(new BlockingQueueClient(queues.get(queues.size() - 1), null));

        log.debug("chunks = {}", chunks);

        ExecutorService pool = Executors.newFixedThreadPool(8);

        // for (int i = 0; i < chunks.size(); i++) {
        Thread lastThread = null;
        for (int i = 0; i < chunks.size(); i++) {
            // pool.execute(chunks.get(i));
            lastThread = new Thread(chunks.get(i));
            lastThread.start();
        }

        lastThread.join();
        watch.stop();
        log.debug(" watch = {} seconds", watch.getTotalTimeSeconds());

    }

    private static class BlockingQueueClient implements Runnable {

        private final BlockingQueue<Long> in;

        private final BlockingQueue<Long> out;

//         public static final long MAX = 1024 * 1024 * 1024;
        public static final long MAX = 320000;

        public static final Logger log = LoggerFactory.getLogger(BlockingQueueClient.class);

        public BlockingQueueClient(BlockingQueue<Long> in, BlockingQueue<Long> out) {
            super();
            this.in = in;
            this.out = out;
        }

        public void run() {
            log.debug("Started");

            long l = -1;
            try {

                if (in != null) {
                    if (out != null) {
                        while (l != 0) {
                            l = in.take();
//                            log.debug("..took {}", l);
                            out.put(l);
//                            log.debug("..put {}", l);
                        }
//                        log.debug(".. finished with 0");
                    } else {
//                        log.debug("last l = {}", l);
                        while (l != 0) {
                            l = in.take();
//                            log.debug("...took {}", l);
                            // out.put(l);
                        }
//                        log.debug("... finished with 0");
                    }

                    log.debug("{} exited with {}.", Thread.currentThread().getName(), l);
                    // System.exit(0);
                } else

                {
                    log.debug("1st started!");

                    for (l = 1; l < MAX; l++) {
                        out.put(l);
//                        log.debug(".put {}", l);
                    }
                    out.put(0l);
                    log.debug(". finished with 0");
                }
                log.debug("{} >> {}", Thread.currentThread().getName(), l);

            } catch (InterruptedException e) {
                log.error("Interrupted!");
            }
        }

        @Override
        public String toString() {
            return "BlockingQueueClient [in=" + in + ", out=" + out + "]";
        }

    }

}