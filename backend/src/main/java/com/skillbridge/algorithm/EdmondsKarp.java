package com.skillbridge.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Edmonds-Karp maximum flow implementation using BFS augmenting paths.
 * Time complexity: O(V * E^2), where E counts directed residual-network edges
 * (the usual bound is stated in terms of the original network edges).
 */
public final class EdmondsKarp {
    private final List<List<Edge>> adjacency;

    public EdmondsKarp(int vertices) {
        if (vertices <= 0) throw new IllegalArgumentException("vertices must be positive");
        adjacency = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
    }

    public Edge addEdge(int from, int to, int capacity) {
        checkVertex(from);
        checkVertex(to);
        if (capacity < 0) throw new IllegalArgumentException("capacity cannot be negative");

        Edge forward = new Edge(to, adjacency.get(to).size(), capacity, capacity);
        Edge reverse = new Edge(from, adjacency.get(from).size(), 0, 0);
        adjacency.get(from).add(forward);
        adjacency.get(to).add(reverse);
        return forward;
    }

    public int maxFlow(int source, int sink) {
        checkVertex(source);
        checkVertex(sink);
        if (source == sink) throw new IllegalArgumentException("source and sink must differ");

        int totalFlow = 0;
        int[] parentVertex = new int[adjacency.size()];
        int[] parentEdgeIndex = new int[adjacency.size()];

        while (bfs(source, sink, parentVertex, parentEdgeIndex)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int vertex = sink; vertex != source; vertex = parentVertex[vertex]) {
                Edge edge = adjacency.get(parentVertex[vertex]).get(parentEdgeIndex[vertex]);
                pathFlow = Math.min(pathFlow, edge.capacity);
            }

            for (int vertex = sink; vertex != source; vertex = parentVertex[vertex]) {
                int previous = parentVertex[vertex];
                Edge edge = adjacency.get(previous).get(parentEdgeIndex[vertex]);
                edge.capacity -= pathFlow;
                adjacency.get(vertex).get(edge.reverseIndex).capacity += pathFlow;
            }
            totalFlow = Math.addExact(totalFlow, pathFlow);
        }
        return totalFlow;
    }

    private boolean bfs(int source, int sink, int[] parentVertex, int[] parentEdgeIndex) {
        Arrays.fill(parentVertex, -1);
        boolean[] visited = new boolean[adjacency.size()];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.remove();
            List<Edge> edges = adjacency.get(current);
            for (int index = 0; index < edges.size(); index++) {
                Edge edge = edges.get(index);
                if (!visited[edge.to] && edge.capacity > 0) {
                    visited[edge.to] = true;
                    parentVertex[edge.to] = current;
                    parentEdgeIndex[edge.to] = index;
                    if (edge.to == sink) return true;
                    queue.add(edge.to);
                }
            }
        }
        return false;
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IllegalArgumentException("Invalid vertex: " + vertex);
        }
    }

    public static final class Edge {
        private final int to;
        private final int reverseIndex;
        private final int originalCapacity;
        private int capacity;

        private Edge(int to, int reverseIndex, int capacity, int originalCapacity) {
            this.to = to;
            this.reverseIndex = reverseIndex;
            this.capacity = capacity;
            this.originalCapacity = originalCapacity;
        }

        public int flow() {
            return originalCapacity - capacity;
        }

        public int originalCapacity() {
            return originalCapacity;
        }

        public int residualCapacity() {
            return capacity;
        }
    }
}
