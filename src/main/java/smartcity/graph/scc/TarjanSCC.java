package smartcity.graph.scc;

import smartcity.graph.Metrics;
import java.util.*;


public class TarjanSCC {
    private Map<Integer, List<Integer>> graph;
    private Metrics metrics;
    private int index;
    private int[] indices;
    private int[] lowLinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> components;

    public TarjanSCC(Map<Integer, List<Integer>> graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.size();
        indices = new int[n];
        lowLinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        components = new ArrayList<>();
        index = 0;

        Arrays.fill(indices, -1);

        metrics.startTimer();
        for (int i = 0; i < n; i++) {
            if (indices[i] == -1) {
                strongConnect(i);
            }
        }
        metrics.stopTimer();

        return components;
    }

    private void strongConnect(int v) {
        metrics.incrementDfsVisits();
        indices[v] = index;
        lowLinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (int neighbor : graph.getOrDefault(v, new ArrayList<>())) {
            metrics.incrementDfsVisits();
            if (indices[neighbor] == -1) {
                strongConnect(neighbor);
                lowLinks[v] = Math.min(lowLinks[v], lowLinks[neighbor]);
            } else if (onStack[neighbor]) {
                lowLinks[v] = Math.min(lowLinks[v], indices[neighbor]);
            }
        }

        if (lowLinks[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            components.add(component);
        }
    }

    public Map<Integer, List<Integer>> buildCondensationGraph() {
        List<List<Integer>> sccs = findSCCs();
        Map<Integer, Integer> componentMap = new HashMap<>();
        Map<Integer, List<Integer>> condensation = new HashMap<>();

        for (int compId = 0; compId < sccs.size(); compId++) {
            for (int node : sccs.get(compId)) {
                componentMap.put(node, compId);
            }
        }

        for (int i = 0; i < sccs.size(); i++) {
            condensation.put(i, new ArrayList<>());
        }

        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            int fromNode = entry.getKey();
            int fromComp = componentMap.get(fromNode);

            for (int toNode : entry.getValue()) {
                int toComp = componentMap.get(toNode);
                if (fromComp != toComp) {
                    List<Integer> neighbors = condensation.get(fromComp);
                    if (!neighbors.contains(toComp)) {
                        neighbors.add(toComp);
                    }
                }
            }
        }

        return condensation;
    }

    public List<Integer> getSCCSizes() {
        List<List<Integer>> sccs = findSCCs();
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sizes.add(scc.size());
        }
        return sizes;
    }
}