# Smart City Scheduling System

## Technical Implementation

### System Architecture
```
DAA-Task-4/
├── src/main/java/smartcity/
│   ├── graph/
│   │   ├── scc/TarjanSCC.java        // O(V+E) cycle detection
│   │   ├── topo/TopologicalSort.java // O(V+E) Kahn's algorithm  
│   │   ├── dagsp/DAGShortestPath.java // O(V+E) critical path
│   │   └── Metrics.java              // Performance instrumentation
│   ├── model/Task.java               // Node duration model
│   ├── generator/DatasetGenerator.java // Synthetic data generation
│   └── Main.java                     // Pipeline execution
├── src/test/java/smartcity/
│   └── GraphAlgorithmsTest.java      // Validation suite
├── data/                             // Generated datasets
└── pom.xml                          // Build configuration
```

## Algorithm Specifications

### 1. Strongly Connected Components (Tarjan)
- **Time Complexity**: O(|V| + |E|)
- **Space Complexity**: O(|V|)
- **Operations**: DFS visits, stack operations, low-link calculations
- **Output**: SCC list, condensation graph DAG

**Implementation Details**:
```java
// Core operations per node
indices[v] = index;
lowLinks[v] = index;
index++;
stack.push(v);
onStack[v] = true;
```

### 2. Topological Sort (Kahn)
- **Time Complexity**: O(|V| + |E|) 
- **Space Complexity**: O(|V|)
- **Operations**: In-degree calculation, queue operations
- **Output**: Valid linear ordering or cycle detection

**Implementation Details**:
```java
// In-degree computation: O(E)
for neighbors: inDegree[neighbor]++
// Queue processing: O(V)
while !queue.empty(): process zero-in-degree nodes
```

### 3. DAG Shortest/Longest Paths
- **Time Complexity**: O(|V| + |E|)
- **Space Complexity**: O(|V|)  
- **Operations**: Edge relaxations, distance updates
- **Output**: Critical path, shortest distances from source

## Performance Metrics

### Measured Parameters
- **Execution Time**: System.nanoTime() precision
- **DFS Visits**: Node traversals in SCC detection
- **Edge Relaxations**: Distance updates in path algorithms  
- **Queue Operations**: Enqueue/dequeue counts in topological sort
- **Memory Usage**: Estimated from graph size and auxiliary structures

### Expected Performance Ranges

| Graph Size | Nodes | Edges | SCC Time (ms) | Topo Time (ms) | Critical Path (ms) |
|------------|-------|-------|---------------|----------------|-------------------|
| Small      | 6-10  | 15-30 | 0.1-0.5       | 0.05-0.2       | 0.05-0.2          |
| Medium     | 10-20 | 30-80 | 0.2-1.0       | 0.1-0.5        | 0.1-0.5           |
| Large      | 20-50 | 80-300| 0.5-3.0       | 0.3-1.5        | 0.3-1.5           |

## Dataset Specifications

### Generation Parameters
- **Node Count**: 6-50 vertices
- **Edge Density**: 25-40% connectivity  
- **Cycle Injection**: 1-5 intentional cycles per graph
- **Duration Range**: 1-10 time units per task

### Structural Variants
1. **Acyclic**: Pure DAG (0 cycles)
2. **Cyclic**: 2-5 strongly connected components  
3. **Mixed**: Hybrid structure with both cyclic and acyclic components

## Validation Criteria

### Algorithm Correctness
- **SCC**: All nodes partitioned, cycles correctly identified
- **Topological Sort**: All edges respect ordering, cycle detection functional
- **Critical Path**: Longest path correctly identified, durations summed accurately

### Performance Benchmarks
- **Time Complexity**: Linear scaling with graph size
- **Space Usage**: Linear auxiliary storage requirements
- **Operation Counts**: Consistent with theoretical complexity

## Build & Execution

### Compilation
```bash
mvn clean compile
# OR
javac -d target/classes src/main/java/smartcity/*.java src/main/java/smartcity/**/*.java
```

### Execution
```bash
mvn exec:java
# OR  
java -cp target/classes smartcity.Main
```

### Testing
```bash
java -cp target/classes smartcity.GraphAlgorithmsTest
```

## Output Format

### Processing Results
```
DATASET: small_acyclic
SCC: 5 components, sizes=[1,1,1,1,1], time=0.45ms, visits=45
TOPO: order=[0,1,2,3,4], time=0.22ms, queue_ops=15  
CRITICAL_PATH: length=28, path=[0,2,4], time=0.18ms, relaxations=12
```

### Performance Summary
- **Total Processing Time**: Sum of all algorithm executions
- **Memory Footprint**: Estimated from graph representation size
- **Operation Efficiency**: Actual vs theoretical complexity ratios

## Technical Constraints

### System Requirements
- **Java Version**: 11+ (module system compatibility)
- **Memory**: Minimum 512MB heap for large graphs
- **Storage**: 1MB for generated datasets

### Algorithm Limitations
- **Graph Size**: Practical limit ~10,000 nodes due to recursion depth
- **Cycle Detection**: Maximum cycle length limited by stack size
- **Path Reconstruction**: Linear space in path length

## Error Conditions

### Expected Failures
- **File I/O**: Missing data directory, permission denied
- **Memory**: Stack overflow for deep recursion
- **Validation**: Invalid graph structures, negative durations

### Recovery Procedures
- Automatic fallback to synthetic test graphs
- Graceful degradation with error reporting
- Resource cleanup on exception conditions

## Extension Interfaces

### Custom Implementations
Override core algorithms while maintaining:
- Metrics collection interface
- Graph input/output formats
- Result validation protocols

### Integration Points
- Alternative SCC algorithms (Kosaraju)
- Different topological sort implementations
- Custom pathfinding heuristics

---

*Technical documentation v1.0 - Performance-optimized graph processing system*
