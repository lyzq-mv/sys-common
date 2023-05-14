package com.morv.syscommon.lang.test;

import com.morv.syscommon.lang.Pipeline;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;

public class PipelineTest {
    @Test
    public void basicTest() {
        List<AtomicInteger> list = Pipeline
                .of(new AtomicInteger(1)
                        , new AtomicInteger(2)
                        , new AtomicInteger(3)
                        , new AtomicInteger(4)
                        , new AtomicInteger(5))
                .filter(it -> it.get() > 2)
                .map(it -> new AtomicInteger(it.get() + 1))
                .peek(it -> it.getAndAdd(3))
                .toList();
        assertEquals(list.size(), 3);
        assertEquals(list.get(0).get(), 7);
        assertEquals(list.get(1).get(), 8);
        assertEquals(list.get(2).get(), 9);
    }
}
