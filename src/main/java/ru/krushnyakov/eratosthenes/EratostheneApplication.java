package ru.krushnyakov.eratosthenes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class EratostheneApplication implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(EratostheneApplication.class);

    public final static int DEFAULT_THREADS = 1;// 1024 * 1024;

    public final static long SIEVE_CHUNKS_SUMMARY_SIZE_LIMIT = 128 * 1024 * 1024l;


    public static void main(String[] args) {
        SpringApplication.run(EratostheneApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        if (args.getNonOptionArgs().size() != 2) {
            System.out.println("Usage: EratostheneApplication <N> <threads>");

        } else {

            Sieve sieve = new Sieve(Long.valueOf(args.getNonOptionArgs().get(0)), Integer.valueOf(args.getNonOptionArgs().get(1)),
                    SIEVE_CHUNKS_SUMMARY_SIZE_LIMIT);
            SieveResult result = sieve.sieve();
            stopWatch.stop();
            log.info("Execution time of Sieve(MAXIMUM_NUMBER = {}, THREADS = {}, ResultSize = {}) = {} ms", sieve.getMaxNumber(),
                    sieve.getThreadsNumber(),
                    result.size(), stopWatch.getTotalTimeMillis());
        }
    }

}
