Performance Analysis
Algorithm Characteristics
Algorithm	Time Complexity	Space Complexity	Best For
Tarjan's SCC	O(V + E)	O(V)	Cycle detection in dense graphs
Kahn's Topo Sort	O(V + E)	O(V)	Sparse DAGs with queue efficiency
DAG Critical Path	O(V + E)	O(V)	Scheduling and timeline analysis
Key Findings
SCC Performance:

Most efficient for cycle detection in dependency graphs

Memory usage scales linearly with graph size

Handles dense cyclic structures well

Topological Sort:

Kahn's algorithm provides stable O(V + E) performance

Automatically detects cycles (returns partial order)

Ideal for task scheduling applications

Critical Path:

Essential for project timeline optimization

Identifies bottleneck tasks that impact overall duration

Works efficiently on the condensation DAG

Testing
Test Coverage
SCC Detection: Simple cycles, complex graphs, empty graphs

Topological Sort: DAG validation, order correctness

Critical Path: Length calculation, path reconstruction

Edge Cases: Empty graphs, disconnected components

Running Specific Tests
bash
# Run all algorithms through full dataset processing
java -cp target/classes smartcity.Main

# Run unit tests only
java -cp target/classes smartcity.GraphAlgorithmsTest

# Run specific test method
# Edit GraphAlgorithmsTest.main() to call specific tests
Configuration
Data Generation Parameters
Modify DatasetGenerator.java to adjust:

Graph sizes (nodes)

Edge density

Cycle frequency

Node duration ranges

Algorithm Parameters
Metrics: Track performance counters and timing

Graph Representation: Adjacency lists for efficiency

Node Durations: Configurable task execution times

Practical Applications
City Service Scheduling
Dependency Resolution: Use SCC to detect circular dependencies

Execution Ordering: Apply topological sort for valid task sequences

Timeline Optimization: Use critical path for bottleneck identification

Resource Allocation: Shortest path analysis for resource distribution

Algorithm Selection Guide
Use Case	Recommended Algorithm	Notes
Cycle Detection	Tarjan's SCC	Memory-efficient, handles dense graphs
Task Scheduling	Kahn's Topological Sort	Predictable performance, cycle detection
Timeline Analysis	DAG Critical Path	Identifies scheduling bottlenecks
Full Pipeline	All three sequentially	SCC → Topo Sort → Critical Path
Troubleshooting
Common Issues
File Not Found Errors:

Ensure data directory exists

Check file permissions in project directory

Memory Issues:

For large graphs, increase JVM heap size: java -Xmx2g -cp ...

Use -Xss to increase stack size for deep recursion

Performance Problems:

Large dense graphs may require optimized data structures

Consider iterative algorithms for very large graphs

Test Failures:

Verify Java version compatibility (requires Java 11+)

Check that all source files are in correct packages

Debug Mode
Enable debug output by modifying Main.java:

java
// Add debug flags
boolean DEBUG = true;
Extension Possibilities
Visualization: Add graph visualization using JavaFX or web interfaces

Real-time Updates: Implement incremental algorithms for dynamic graphs

Parallel Processing: Use parallel DFS for very large graphs

Constraint Satisfaction: Add resource constraints to scheduling

Web Interface: Create REST API for remote graph processing

Contributing
Code Structure
Follow Java naming conventions

Add Javadoc comments for public methods

Include unit tests for new functionality

Maintain package structure consistency

Testing Guidelines
Write tests for all public methods

Include edge cases and error conditions

Use descriptive test method names

Ensure tests are independent and repeatable
