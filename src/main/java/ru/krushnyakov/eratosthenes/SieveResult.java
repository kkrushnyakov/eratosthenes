package ru.krushnyakov.eratosthenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SieveResult {

    private long[][] primes;
    
    public SieveResult(long[][] primes) {
        super();
        this.primes = primes;
    }

    public List<Long> getPrimes() {
        if(size() > 1000) return null;
        
        return Stream.of(primes).map(arr -> Arrays.stream(arr).boxed().collect(Collectors.toList())).reduce(new ArrayList<Long>(), (List<Long> l, List<Long> r) -> { l.addAll(r); return l;});
    }

    public long size() {

        return Arrays.stream(primes).mapToInt(a -> a.length).sum();
    }

    
}