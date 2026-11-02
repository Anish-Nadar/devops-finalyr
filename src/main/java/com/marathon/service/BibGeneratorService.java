package com.marathon.service;

import com.marathon.model.RaceCategory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BibGeneratorService {

    private final ConcurrentHashMap<RaceCategory, AtomicInteger> bibCounters = new ConcurrentHashMap<>();

    public BibGeneratorService() {
        bibCounters.put(RaceCategory.FULL_MARATHON, new AtomicInteger(1001));
        bibCounters.put(RaceCategory.HALF_MARATHON, new AtomicInteger(2001));
        bibCounters.put(RaceCategory.TEN_K, new AtomicInteger(3001));
        bibCounters.put(RaceCategory.FIVE_K, new AtomicInteger(4001));
    }

    public synchronized String generateBibNumber(RaceCategory category) {
        AtomicInteger counter = bibCounters.computeIfAbsent(category, k -> new AtomicInteger(1001));
        int nextVal = counter.getAndIncrement();
        return category.getBibPrefix() + nextVal;
    }

    public int getCurrentBibCounter(RaceCategory category) {
        AtomicInteger counter = bibCounters.get(category);
        return (counter != null) ? counter.get() : 1001;
    }
}

