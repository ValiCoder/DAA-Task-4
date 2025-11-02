package smartcity.graph.topo;

import smartcity.graph.Metrics;
import java.util.*;

/**
 * Implementation of Kahn's algorithm for topological sorting
 */
public class TopologicalSort {
    private Metrics metrics;

    public TopologicalSort(Metrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Perform topological sort using Kahn's algorithm
     * @param graph The input graph as adjacency list
     * @return Topological order of vertices, or empty list if cycle detected
     */
    public List<Integer> kahnTopologicalSort(Map<Integer, List<Integer>> graph) {
        metrics.startTimer();

        int n = graph.size();
        int[] inDegree = new int[n];

        for (List<Integer> neighbors : graph.values()) {
            for (int neighbor : neighbors) {
                inDegree[neighbor]++;
                metrics.incrementQueueOperations();
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementQueueOperations();
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            metrics.incrementQueueOperations();
            topoOrder.add(node);
            visitedCount++;

            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                    metrics.incrementQueueOperations();
                }
            }
        }

        metrics.stopTimer();

        if (visitedCount != n) {
            System.out.println("Warning: Graph contains cycles, topological sort may be incomplete");
        }

        return topoOrder;
    }

    public List<Integer> deriveTaskOrder(List<Integer> componentOrder, List<List<Integer>> sccs) {
        List<Integer> taskOrder = new ArrayList<>();

        for (int compId : componentOrder) {
            taskOrder.addAll(sccs.get(compId));
        }

        return taskOrder;
    }
}