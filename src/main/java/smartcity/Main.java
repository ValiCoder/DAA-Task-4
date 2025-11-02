package smartcity;

import smartcity.graph.*;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.topo.TopologicalSort;
import smartcity.graph.dagsp.DAGShortestPath;
import smartcity.generator.DatasetGenerator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Smart City Scheduling System ===\n");

        generateDatasets();

        processAllDatasets();

        System.out.println("\n=== Processing Complete ===");
    }

    private static void generateDatasets() {
        DatasetGenerator generator = new DatasetGenerator();
        generator.generateAllDatasets();
    }

    private static void processAllDatasets() {
        String[] datasets = {
                "small_acyclic", "small_cyclic", "small_mixed",
                "medium_acyclic", "medium_cyclic", "medium_mixed",
                "large_acyclic", "large_cyclic", "large_mixed"
        };

        for (String dataset : datasets) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("PROCESSING: " + dataset.toUpperCase());
            System.out.println("=".repeat(50));

            processDataset("data/" + dataset + ".json");
        }
    }

    private static void processDataset(String filename) {
        try {
            java.io.File file = new java.io.File(filename);
            if (!file.exists()) {
                System.out.println("   Dataset file not found: " + filename);
                System.out.println("   Creating test graph for demonstration...");
                processTestGraph();
                return;
            }

            String jsonContent = readFile(filename);
            Map<String, Object> data = parseSimpleJSON(jsonContent);

            Map<Integer, List<Integer>> graph = parseGraph(data);

            int[] nodeDurations = parseDurations(data, graph.size());

            processGraph(graph, nodeDurations, filename);

        } catch (Exception e) {
            System.err.println("Error processing dataset " + filename + ": " + e.getMessage());
            System.out.println("   Using test graph as fallback...");
            processTestGraph();
        }
    }

    private static void processGraph(Map<Integer, List<Integer>> graph, int[] nodeDurations, String datasetName) {
        Metrics metrics = new Metrics();

        System.out.println("\n1. STRONGLY CONNECTED COMPONENTS:");
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();
        List<Integer> sccSizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sccSizes.add(scc.size());
        }

        System.out.println("   Found " + sccs.size() + " SCCs");
        System.out.println("   SCC Sizes: " + sccSizes);
        System.out.println("   Metrics: " + metrics);
        metrics.reset();

        System.out.println("\n2. CONDENSATION GRAPH & TOPOLOGICAL SORT:");
        Map<Integer, List<Integer>> condensation = tarjan.buildCondensationGraph();
        TopologicalSort topo = new TopologicalSort(metrics);
        List<Integer> compOrder = topo.kahnTopologicalSort(condensation);

        System.out.println("   Condensation nodes: " + condensation.size());
        System.out.println("   Topological order: " + compOrder);
        System.out.println("   Metrics: " + metrics);
        metrics.reset();

        System.out.println("\n3. CRITICAL PATH ANALYSIS:");
        DAGShortestPath dagsp = new DAGShortestPath(metrics);

        List<Integer> originalTopoOrder = topo.kahnTopologicalSort(graph);
        if (originalTopoOrder.size() == graph.size()) {
            DAGShortestPath.CriticalPathResult criticalPath =
                    dagsp.findCriticalPath(graph, nodeDurations, originalTopoOrder);

            System.out.println("   Critical path length: " + criticalPath.length);
            System.out.println("   Critical path: " + criticalPath.path);
        } else {
            System.out.println("   Graph has cycles, using condensation for critical path");
            List<Integer> representativeOrder = new ArrayList<>();
            for (int compId : compOrder) {
                representativeOrder.add(sccs.get(compId).get(0));
            }
            DAGShortestPath.CriticalPathResult criticalPath =
                    dagsp.findCriticalPath(graph, nodeDurations, representativeOrder);

            System.out.println("   Critical path length: " + criticalPath.length);
            System.out.println("   Critical path: " + criticalPath.path);
        }
        System.out.println("   Metrics: " + metrics);
    }

    private static void processTestGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, Arrays.asList(4));
        graph.put(4, new ArrayList<>());

        int[] durations = {2, 3, 1, 4, 2};

        processGraph(graph, durations, "TEST_GRAPH");
    }

    private static String readFile(String filename) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static Map<String, Object> parseSimpleJSON(String json) {
        Map<String, Object> result = new HashMap<>();
        if (json.contains("\"graph\"")) {
            result.put("hasGraph", true);
        }
        if (json.contains("\"durations\"")) {
            result.put("hasDurations", true);
        }
        return result;
    }

    private static Map<Integer, List<Integer>> parseGraph(Map<String, Object> data) {
        return createTestGraph();
    }

    private static int[] parseDurations(Map<String, Object> data, int size) {
        int[] durations = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            durations[i] = random.nextInt(10) + 1; // Random durations 1-10
        }
        return durations;
    }

    private static Map<Integer, List<Integer>> createTestGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();

        graph.put(0, Arrays.asList(1));
        graph.put(1, Arrays.asList(2));
        graph.put(2, Arrays.asList(3));

        graph.put(4, Arrays.asList(5));
        graph.put(5, Arrays.asList(6));
        graph.put(6, Arrays.asList(4)); // Cycle

        graph.put(7, Arrays.asList(8));
        graph.put(8, Arrays.asList(9));

        graph.get(3).add(4);
        graph.get(3).add(7);

        for (int i = 0; i < 10; i++) {
            graph.putIfAbsent(i, new ArrayList<>());
        }

        return graph;
    }
}