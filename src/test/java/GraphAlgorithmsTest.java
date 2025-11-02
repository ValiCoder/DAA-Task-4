import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

// Import our classes (adjust package as needed)
import smartcity.graph.*;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.topo.TopologicalSort;
import smartcity.graph.dagsp.DAGShortestPath;

/**
 * Comprehensive tests for graph algorithms
 */
public class GraphAlgorithmsTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void testSCCSimpleCycle() {
        // Graph: 0->1->2->0 (cycle) + 3->4 (separate)
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1));
        graph.put(1, Arrays.asList(2));
        graph.put(2, Arrays.asList(0));
        graph.put(3, Arrays.asList(4));
        graph.put(4, new ArrayList<>());

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        // Should find 3 components: [0,1,2], [3], [4]
        assertEquals(3, sccs.size());

        // Find the cycle component
        List<Integer> cycleComponent = sccs.stream()
                .filter(comp -> comp.size() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(cycleComponent);
        assertTrue(cycleComponent.containsAll(Arrays.asList(0, 1, 2)));
    }

    @Test
    void testTopologicalSortDAG() {
        // Simple DAG: 0->1->3, 0->2->3
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, new ArrayList<>());

        TopologicalSort topo = new TopologicalSort(metrics);
        List<Integer> order = topo.kahnTopologicalSort(graph);

        assertEquals(4, order.size());

        // Verify topological order property
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }

        // Check all edges go forward in the order
        assertTrue(position.get(0) < position.get(1));
        assertTrue(position.get(0) < position.get(2));
        assertTrue(position.get(1) < position.get(3));
        assertTrue(position.get(2) < position.get(3));
    }

    @Test
    void testCriticalPath() {
        // Graph: 0->1->3, 0->2->3
        // Durations: 0=2, 1=3, 2=1, 3=4
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, new ArrayList<>());

        int[] durations = {2, 3, 1, 4};
        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);

        DAGShortestPath dagsp = new DAGShortestPath(metrics);
        DAGShortestPath.CriticalPathResult result =
                dagsp.findCriticalPath(graph, durations, topoOrder);

        // Critical path should be 0->1->3 with total duration 2+3+4=9
        assertEquals(9, result.length);
        assertEquals(Arrays.asList(0, 1, 3), result.path);
    }

    @Test
    void testShortestPaths() {
        // Simple linear graph: 0->1->2
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1));
        graph.put(1, Arrays.asList(2));
        graph.put(2, new ArrayList<>());

        int[] durations = {1, 2, 3};
        List<Integer> topoOrder = Arrays.asList(0, 1, 2);

        DAGShortestPath dagsp = new DAGShortestPath(metrics);
        int[] dist = dagsp.shortestPaths(graph, durations, topoOrder, 0);

        assertEquals(1, dist[0]);  // Source itself
        assertEquals(3, dist[1]);  // 0->1: 1+2
        assertEquals(6, dist[2]);  // 0->1->2: 1+2+3
    }

    @Test
    void testEmptyGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            graph.put(i, new ArrayList<>());
        }

        // Test SCC on empty graph
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();
        assertEquals(3, sccs.size()); // Each node is its own SCC

        // Test topological sort on empty graph
        TopologicalSort topo = new TopologicalSort(metrics);
        List<Integer> order = topo.kahnTopologicalSort(graph);
        assertEquals(3, order.size());
    }
}