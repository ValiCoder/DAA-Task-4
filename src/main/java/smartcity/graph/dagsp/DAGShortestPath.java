package smartcity.graph.dagsp;

import smartcity.graph.Metrics;
import java.util.*;

public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }

    public int[] shortestPaths(Map<Integer, List<Integer>> graph,
                               int[] nodeDurations,
                               List<Integer> topoOrder,
                               int source) {
        metrics.startTimer();

        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = nodeDurations[source]; // Include source node duration

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

    public CriticalPathResult findCriticalPath(Map<Integer, List<Integer>> graph,
                                               int[] nodeDurations,
                                               List<Integer> topoOrder) {
        metrics.startTimer();

        int n = graph.size();
        int[] longest = new int[n];
        int[] prev = new int[n];
        Arrays.fill(prev, -1);

        System.arraycopy(nodeDurations, 0, longest, 0, n);

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

        int maxDist = 0;
        int endNode = 0;
        for (int i = 0; i < n; i++) {
            if (longest[i] > maxDist) {
                maxDist = longest[i];
                endNode = i;
            }
        }

        List<Integer> criticalPath = reconstructPath(prev, endNode);

        metrics.stopTimer();
        return new CriticalPathResult(criticalPath, maxDist);
    }

    private List<Integer> reconstructPath(int[] prev, int endNode) {
        List<Integer> path = new ArrayList<>();
        int current = endNode;

        while (current != -1) {
            path.add(0, current);
            current = prev[current];
        }

        return path;
    }

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