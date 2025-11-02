package smartcity.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {
    private Random random = new Random(42);

    public void generateAllDatasets() {
        System.out.println("Generating all datasets...");

        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Created data directory");
        }

        generateDataset("small_acyclic", 8, 0.3, false, 1);
        generateDataset("small_cyclic", 10, 0.4, true, 2);
        generateDataset("small_mixed", 9, 0.35, true, 1);

        generateDataset("medium_acyclic", 15, 0.3, false, 1);
        generateDataset("medium_cyclic", 20, 0.4, true, 3);
        generateDataset("medium_mixed", 18, 0.35, true, 2);

        generateDataset("large_acyclic", 35, 0.25, false, 1);
        generateDataset("large_cyclic", 50, 0.3, true, 5);
        generateDataset("large_mixed", 45, 0.28, true, 3);

        System.out.println("All datasets generated successfully!");
    }

    private void generateDataset(String name, int nodes, double density, boolean allowCycles, int expectedCycles) {
        try {
            String jsonContent = generateJSON(name, nodes, density, allowCycles, expectedCycles);
            File file = new File("data/" + name + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonContent);
                System.out.printf("Generated %s: %d nodes%n", name, nodes);
            }
        } catch (IOException e) {
            System.err.println("Error writing dataset " + name + ": " + e.getMessage());
        }
    }

    private String generateJSON(String name, int nodes, double density, boolean allowCycles, int expectedCycles) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(name).append("\",\n");
        json.append("  \"nodes\": ").append(nodes).append(",\n");
        json.append("  \"density\": ").append(density).append(",\n");
        json.append("  \"allowCycles\": ").append(allowCycles).append(",\n");

        // Generate graph
        Map<Integer, List<Integer>> graph = generateGraph(nodes, density, allowCycles, expectedCycles);
        json.append("  \"graph\": {\n");
        boolean firstNode = true;
        for (int i = 0; i < nodes; i++) {
            if (!firstNode) json.append(",\n");
            json.append("    \"").append(i).append("\": [");
            List<Integer> neighbors = graph.getOrDefault(i, new ArrayList<>());
            boolean firstNeighbor = true;
            for (Integer neighbor : neighbors) {
                if (!firstNeighbor) json.append(", ");
                json.append(neighbor);
                firstNeighbor = false;
            }
            json.append("]");
            firstNode = false;
        }
        json.append("\n  },\n");

        // Generate durations
        json.append("  \"durations\": {\n");
        boolean firstDuration = true;
        for (int i = 0; i < nodes; i++) {
            if (!firstDuration) json.append(",\n");
            json.append("    \"").append(i).append("\": ").append(random.nextInt(10) + 1);
            firstDuration = false;
        }
        json.append("\n  }\n");
        json.append("}");

        return json.toString();
    }

    private Map<Integer, List<Integer>> generateGraph(int nodes, double density, boolean allowCycles, int numCycles) {
        Map<Integer, List<Integer>> graph = new HashMap<>();

        // Initialize all nodes
        for (int i = 0; i < nodes; i++) {
            graph.put(i, new ArrayList<>());
        }

        // Generate random edges based on density
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (i != j && random.nextDouble() < density) {
                    graph.get(i).add(j);
                }
            }
        }

        // Add cycles if allowed
        if (allowCycles && nodes >= 3) {
            addCycles(graph, nodes, numCycles);
        }

        return graph;
    }

    private void addCycles(Map<Integer, List<Integer>> graph, int nodes, int numCycles) {
        for (int c = 0; c < numCycles; c++) {
            int cycleLength = Math.min(random.nextInt(3) + 3, nodes); // 3-5 nodes per cycle
            if (cycleLength < 2) continue;

            Set<Integer> cycleNodes = new HashSet<>();
            while (cycleNodes.size() < cycleLength) {
                cycleNodes.add(random.nextInt(nodes));
            }

            List<Integer> nodeList = new ArrayList<>(cycleNodes);
            Collections.shuffle(nodeList, random);

            // Create the cycle
            for (int i = 0; i < nodeList.size(); i++) {
                int from = nodeList.get(i);
                int to = nodeList.get((i + 1) % nodeList.size());
                List<Integer> neighbors = graph.get(from);
                if (!neighbors.contains(to)) {
                    neighbors.add(to);
                }
            }
        }
    }
}