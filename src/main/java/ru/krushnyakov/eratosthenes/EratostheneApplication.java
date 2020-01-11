package ru.krushnyakov.eratosthenes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class EratostheneApplication implements CommandLineRunner {
    
    
    private static Logger log = LoggerFactory.getLogger(EratostheneApplication.class);

//    public final static long MAXIMUM_NUMBER = 120; // 1024 * 1024 * 64;
//    public final static long MAXIMUM_NUMBER =  1_000_000_000l;
//    public final static long MAXIMUM_NUMBER =  500_000_000;
//    public final static long MAXIMUM_NUMBER =  3_000_000_000l;
//    public final static long MAXIMUM_NUMBER =  2_100_000_000l;
//    public final static long MAXIMUM_NUMBER =  Integer.MAX_VALUE - 1024;
    public final static long MAXIMUM_NUMBER =  100;

//    public final static int SIEVE_CHUNK_SIZE_LIMIT = 32; 
//    public final static int SIEVE_CHUNK_SIZE_LIMIT = 256 * 1024 * 1024;
    
    
    public final static int THREADS = 4;//1024 * 1024;
    
//    public final static long SIEVE_CHUNKS_SUMMARY_SIZE_LIMIT = 256 * 1024 * 1024;
    public final static long SIEVE_CHUNKS_SUMMARY_SIZE_LIMIT = 16;
    
    
//    @SuppressWarnings("unused")
//    private static Logger log = LoggerFactory.getLogger(EratosfenApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EratostheneApplication.class, args);
	}


    @Override
    public void run(String... args) throws Exception {

//        Sieve sieve = new Sieve(1000000000, 1000000000, 1);
        
        final StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
 
        
        Sieve sieve = new Sieve(MAXIMUM_NUMBER, THREADS, SIEVE_CHUNKS_SUMMARY_SIZE_LIMIT);
        SieveResult result = sieve.sieve();
        stopWatch.stop();
        log.info("Execution time of Sieve(MAXIMUM_NUMBER = {}, THREADS = {}, ResultSize = {}) = {} ms", sieve.getMaxNumber(), sieve.getThreadsNumber()/*, result.getChunks().stream().map(ChunkResult::size).collect(Collectors.toList())*/, result.size(), stopWatch.getTotalTimeMillis());
    }

}
