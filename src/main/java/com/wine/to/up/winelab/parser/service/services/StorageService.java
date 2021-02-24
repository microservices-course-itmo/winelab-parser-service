package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.min;
import static java.lang.StrictMath.ceil;

@Service
public class StorageService {
    private List<Wine> wines;
    private LocalDateTime lastParsed;
    private int chunkCount, chunkSize, lastChunk;

    public StorageService() {}

    public void clearWines() {
        this.wines = new ArrayList<>();
        this.onChange();
    }

    public void putWines(List<Wine> newWines) {
        this.wines.addAll(newWines);
        this.onChange();
        this.lastParsed = LocalDateTime.now();
    }

    public List<Wine> getAll() {
        return this.wines;
    }

    public long size() {
        return this.wines.size();
    }

    public void resetLastChunk() {
        lastChunk = 0;
    }

    public boolean allWinesFetched() {
        return lastChunk == wines.size();
    }

    public LocalDateTime getLastParsed() {
        return lastParsed;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        if(chunkCount <= 0) {
            throw new IllegalArgumentException("Chunk count can not be non-positive");
        }
        this.chunkCount = chunkCount;
        this.setChunkSizeFromCount();
        this.lastChunk = 0;
    }

    public List<Wine> getNextChunk() throws IllegalArgumentException, IndexOutOfBoundsException {
        if(this.chunkCount == 0) {
            throw new IllegalStateException("Chunk count is not set");
        }
        if(this.allWinesFetched()) {
            throw new IndexOutOfBoundsException("All wines are already fetched");
        }
        int from = this.chunkSize * (this.lastChunk - 1);
        int to = min(from + this.chunkSize, this.wines.size());
        this.lastChunk++;
        return this.wines.subList(from, to);
    }

    private void setChunkSizeFromCount() {
        if (chunkCount != 0) {
            this.chunkSize = (int) ceil((double) this.wines.size() / chunkCount);
        } else {
            this.chunkSize = 0;
        }
    }

    private void onChange() {
        this.lastChunk = 0;
        this.setChunkSizeFromCount();
        this.lastParsed = null;
    }
}
