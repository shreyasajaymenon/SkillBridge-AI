package com.skillbridge.service;

import com.skillbridge.algorithm.EdmondsKarp;
import com.skillbridge.algorithm.RandomizedQuickselect;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BenchmarkService {
    public Map<String, Object> run() {
        List<Map<String, Object>> results = new ArrayList<>();
        for (int size : List.of(100, 500, 1000)) {
            double[] scores = new double[size];
            Random seeded = new Random(2026L + size);
            for (int i = 0; i < size; i++) scores[i] = seeded.nextDouble() * 100.0;

            List<Double> selectionInput = new ArrayList<>(size);
            for (double score : scores) selectionInput.add(score);
            long quickStart = System.nanoTime();
            double cutoff = RandomizedQuickselect.selectKthLargest(
                    selectionInput, Math.max(1, size / 5), Double::doubleValue);
            long quickNanos = System.nanoTime() - quickStart;

            long sortStart = System.nanoTime();
            double[] sorted = scores.clone();
            Arrays.sort(sorted);
            double sortedCutoff = sorted[size - Math.max(1, size / 5)];
            long sortNanos = System.nanoTime() - sortStart;

            // A deterministic layered network with a known feasible assignment workload.
            int students = size;
            int jobs = Math.max(5, size / 20);
            int source = 0, firstStudent = 1, firstJob = firstStudent + students;
            int sink = firstJob + jobs;
            EdmondsKarp network = new EdmondsKarp(sink + 1);
            for (int i = 0; i < students; i++) network.addEdge(source, firstStudent + i, 1);
            for (int j = 0; j < jobs; j++) network.addEdge(firstJob + j, sink, 4);
            // Each student can connect to up to three deterministic job nodes.
            int[][] eligibleJobs = new int[students][Math.min(3, jobs)];
            for (int i = 0; i < students; i++) {
                for (int offset = 0; offset < Math.min(3, jobs); offset++) {
                    int job = (i + offset) % jobs;
                    eligibleJobs[i][offset] = job;
                    network.addEdge(firstStudent + i, firstJob + job, 1);
                }
            }
            long flowStart = System.nanoTime();
            int flow = network.maxFlow(source, sink);
            long flowNanos = System.nanoTime() - flowStart;

            // Simple first-fit greedy baseline over the same candidate-job edges.
            int[] remainingSeats = new int[jobs];
            Arrays.fill(remainingSeats, 4);
            long greedyStart = System.nanoTime();
            int greedyPlacements = 0;
            for (int i = 0; i < students; i++) {
                for (int job : eligibleJobs[i]) {
                    if (remainingSeats[job] > 0) {
                        remainingSeats[job]--;
                        greedyPlacements++;
                        break;
                    }
                }
            }
            long greedyNanos = System.nanoTime() - greedyStart;

            results.add(Map.of(
                    "candidates", size,
                    "quickselectMs", nanosToMs(quickNanos),
                    "fullSortMs", nanosToMs(sortNanos),
                    "quickselectCutoff", round(cutoff),
                    "sortCutoff", round(sortedCutoff),
                    "maximumFlowPlacements", flow,
                    "greedyPlacements", greedyPlacements,
                    "flowVertices", sink + 1,
                    "flowMs", nanosToMs(flowNanos),
                    "greedyMs", nanosToMs(greedyNanos)
            ));
        }
        return Map.of(
                "note", "Single-run illustrative measurements; JVM warm-up and machine load affect timings. Repeat trials for report-quality evidence.",
                "results", results
        );
    }

    private double nanosToMs(long nanos) { return Math.round(nanos / 1_000.0) / 1_000.0; }
    private double round(double value) { return Math.round(value * 100.0) / 100.0; }
}
