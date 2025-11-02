import smartcity.graph.*;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.topo.TopologicalSort;
import smartcity.graph.dagsp.DAGShortestPath;

import java.util.*;

class TestAssert {
    static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Got: " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
}

/**
 * JUnit-style tests with custom assertions
 */
public class GraphAlgorithmsTest {
    private Metrics metrics;

    public void setUp() {
        metrics = new Metrics();
    }

    public void testSCCSimpleCycle() {
        System.out.println("=== Test SCC Simple Cycle ===");
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1));
        graph.put(1, Arrays.asList(2));
        graph.put(2, Arrays.asList(0));
        graph.put(3, Arrays.asList(4));
        graph.put(4, new ArrayList<>());

        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        TestAssert.assertEquals(3, sccs.size(), "Should find 3 SCCs");

        List<Integer> cycleComponent = null;
        for (List<Integer> comp : sccs) {
            if (comp.size() == 3) {
                cycleComponent = comp;
                break;
            }
        }

        TestAssert.assertNotNull(cycleComponent, "Cycle component should not be null");
        TestAssert.assertTrue(cycleComponent.containsAll(Arrays.asList(0, 1, 2)),
                "Cycle component should contain 0,1,2");
        System.out.println("✓ PASSED");
    }

    public void testTopologicalSortDAG() {
        System.out.println("=== Test Topological Sort DAG ===");
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, new ArrayList<>());

        TopologicalSort topo = new TopologicalSort(metrics);
        List<Integer> order = topo.kahnTopologicalSort(graph);

        TestAssert.assertEquals(4, order.size(), "Order should have 4 elements");

        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }

        TestAssert.assertTrue(position.get(0) < position.get(1), "0 should come before 1");
        TestAssert.assertTrue(position.get(0) < position.get(2), "0 should come before 2");
        TestAssert.assertTrue(position.get(1) < position.get(3), "1 should come before 3");
        TestAssert.assertTrue(position.get(2) < position.get(3), "2 should come before 3");
        System.out.println("✓ PASSED");
    }

    public void testCriticalPath() {
        System.out.println("=== Test Critical Path ===");
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

        TestAssert.assertEquals(9, result.length, "Critical path length should be 9");
        TestAssert.assertTrue(result.path.equals(Arrays.asList(0, 1, 3)),
                "Critical path should be [0, 1, 3]");
        System.out.println("✓ PASSED");
    }

    public void runAllTests() {
        System.out.println("=== Running JUnit-style Tests ===\n");
        setUp();

        try {
            testSCCSimpleCycle();
            testTopologicalSortDAG();
            testCriticalPath();
            System.out.println("\n=== ALL JUNIT TESTS PASSED ===");
        } catch (AssertionError e) {
            System.out.println("\n=== TEST FAILED: " + e.getMessage() + " ===");
        }
    }

    public static void main(String[] args) {
        new GraphAlgorithmsTest().runAllTests();
    }
}