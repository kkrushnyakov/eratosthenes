package ru.krushnyakov.eratosthenes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class EratosfenApplication implements CommandLineRunner {
    
    
    private static Logger log = LoggerFactory.getLogger(EratosfenApplication.class);

//    public final static long MAXIMUM_NUMBER = 120; // 1024 * 1024 * 64;
    public final static long MAXIMUM_NUMBER =  1000 * 1000 * 1000 ;
//    public final static long MAXIMUM_NUMBER =  100 ;

//    public final static int SIEVE_CHUNK_SIZE = 32; 
    public final static int SIEVE_CHUNK_SIZE = 1024 * 1024 * 512;

    
    
    public final static int THREADS = 1;//1024 * 1024;
    
//    @SuppressWarnings("unused")
//    private static Logger log = LoggerFactory.getLogger(EratosfenApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EratosfenApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {

//        Sieve sieve = new Sieve(1000000000, 1000000000, 1);
        
        final StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
 
        
        Sieve sieve = new Sieve(MAXIMUM_NUMBER, SIEVE_CHUNK_SIZE, THREADS);
        sieve.run();
        stopWatch.stop();
        log.info("Execution time of Sieve(MAXIMUM_NUMBER = {}, SIEVE_CHUNK_SIZE = {}, THREADS = {}) = {} ms", MAXIMUM_NUMBER, SIEVE_CHUNK_SIZE, THREADS, stopWatch.getTotalTimeMillis());
    }

}
