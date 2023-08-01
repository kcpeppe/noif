package com.kodewerk.branchless;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NoBranch {

    @Benchmark
    public void largerIntOverflows(Blackhole blackhole, BenchmarkState state) {
        int returnedValue = largerIntOverflows(state.testList.get(state.index++),state.testList.get(state.index++));
        blackhole.consume(returnedValue);
    }

    public int largerIntOverflows(int a, int b) {
        int greater = (a - b) >>> 63;
        return (a * (1 - greater)) + (b * greater);
    }

    @Benchmark
    public void largerIntNoCast(Blackhole blackhole, BenchmarkState state) {
        int returnedValue = largerIntNoCast(state.testList.get(state.index++),state.testList.get(state.index++));
        blackhole.consume(returnedValue);
    }

    public int largerIntNoCast(int a, int b) {
        int greater = ((~((a ^ b) >>> 31) & 0x00000001) * ((b - a) >>> 31)) ^ (((a ^ b) >>> 31) * (b >>> 31));
        return (a * (1 - greater)) + (b * greater);
    }

    @Benchmark
    public void largerIntCast(Blackhole blackhole, BenchmarkState state) {
        int returnedValue = largerIntCast(state.testList.get(state.index++),state.testList.get(state.index++));
        blackhole.consume(returnedValue);
    }

    public int largerIntCast(int a, int b) {
        int greater = (int)(((long)a - (long)b) >>> 63);
        return (a * (1 - greater)) + (b * greater);
    }

    @Benchmark
    public void testLargerWithIf(Blackhole blackhole, BenchmarkState state) {
        int returnedValue = largerWithIf(state.testList.get(state.index++),state.testList.get(state.index++));
        blackhole.consume(returnedValue);
    }

    public int largerWithIf(int a, int b) {
        return (a < b) ? b : a;
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"100","0"})
        public int listSize;
        public int index;

        public List<Integer> testList;

        @Setup(Level.Trial)
        public void setUp() {
            if (testList != null) return;
            testList = new Random()
                    .ints()
                    .limit(listSize)
                    .boxed()
                    .collect(Collectors.<Integer>toList());
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            //testList = null;
        }
    }
}

