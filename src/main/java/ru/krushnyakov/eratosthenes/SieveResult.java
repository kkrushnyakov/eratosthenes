package ru.krushnyakov.eratosthenes;

import java.util.List;

public class SieveResult {

    private List<SieveChunkResult> chunkResults;
    
    public SieveResult(List<SieveChunkResult> chunkResults) {
        super();
        this.chunkResults = chunkResults;
    }

    public long size() {
        return chunkResults.stream().mapToLong(SieveChunkResult::getPrimesNumber).sum();
    }

    
}