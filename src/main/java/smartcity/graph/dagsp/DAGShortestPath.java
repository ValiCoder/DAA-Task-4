package smartcity.graph.dagsp;

import smartcity.graph.Metrics;
import java.util.*;

/**
 * Shortest and longest path algorithms for Directed Acyclic Graphs
 * Uses node durations instead of edge weights
 */
public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Compute single-source shortest paths in a DAG
     * @param graph The DAG as adjacency list
     * @param nodeDurations Array of node durations/times
     * @param topoOrder Topological order of the graph
     * @param source Source node ID
     * @return Array of shortest distances from source to all nodes
     */
    public int[] shortestPaths(Map<Integer, List<Integer>> graph,
                               int[] nodeDurations,
                               List<Integer> topoOrder,
                               int source) {
        metrics.startTimer();

        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = nodeDurations[source]; // Include source node duration

        // Process nodes in topological order
        for (int node : topoOrder) {
            metrics.incrementEdgeRelaxations();
            if (dist[node] != Integer.MAX_VALUE) {
                for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                    int newDist = dist[node] + nodeDurations[neighbor];
                    if (newDist < dist[neighbor]) {
                        dist[neighbor] = newDist;
                        metrics.incrementEdgeRelaxations();
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    /**
     * Find the critical path (longest path) in the DAG
     * @param graph The DAG as adjacency list
     * @param nodeDurations Array of node durations
     * @param topoOrder Topological order of the graph
     * @return CriticalPathResult containing the path and its length
     */
    public CriticalPathResult findCriticalPath(Map<Integer, List<Integer>> graph,
                                               int[] nodeDurations,
                                               List<Integer> topoOrder) {
        metrics.startTimer();

        int n = graph.size();
        int[] longest = new int[n];
        int[] prev = new int[n];
        Arrays.fill(prev, -1);

        // Initialize with node durations
        System.arraycopy(nodeDurations, 0, longest, 0, n);

        // Calculate longest paths using topological order
        for (int node : topoOrder) {
            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                metrics.incrementEdgeRelaxations();
                int newLength = longest[node] + nodeDurations[neighbor];
                if (newLength > longest[neighbor]) {
                    longest[neighbor] = newLength;
                    prev[neighbor] = node;
                }
            }
        }

        // Find the endpoint with maximum distance
        int maxDist = 0;
        int endNode = 0;
        for (int i = 0; i < n; i++) {
            if (longest[i] > maxDist) {
                maxDist = longest[i];
                endNode = i;
            }
        }

        // Reconstruct critical path
        List<Integer> criticalPath = reconstructPath(prev, endNode);

        metrics.stopTimer();
        return new CriticalPathResult(criticalPath, maxDist);
    }

    private List<Integer> reconstructPath(int[] prev, int endNode) {
        List<Integer> path = new ArrayList<>();
        int current = endNode;

        // Backtrack to build the path
        while (current != -1) {
            path.add(0, current);
            current = prev[current];
        }

        return path;
    }

    /**
     * Reconstruct an optimal path from source to target
     * @param dist Distance array from shortestPaths
     * @param prev Predecessor array
     * @param target Target node
     * @return List of nodes forming the path
     */
    public List<Integer> reconstructOptimalPath(int[] dist, int[] prev, int target) {
        if (dist[target] == Integer.MAX_VALUE) {
            return new ArrayList<>(); // No path exists
        }

        List<Integer> path = new ArrayList<>();
        int current = target;

        while (current != -1) {
            path.add(0, current);
            current = prev[current];
        }

        return path;
    }

    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }

        @Override
        public String toString() {
            return String.format("CriticalPath{length=%d, path=%s}", length, path);
        }
    }
}