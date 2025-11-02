package smartcity.graph;

public class Metrics {
    private long startTime;
    private long endTime;
    private int dfsVisits;
    private int edgeRelaxations;
    private int queueOperations;

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementDfsVisits() {
        dfsVisits++;
    }

    public void incrementEdgeRelaxations() {
        edgeRelaxations++;
    }

    public void incrementQueueOperations() {
        queueOperations++;
    }

    public int getDfsVisits() { return dfsVisits; }
    public int getEdgeRelaxations() { return edgeRelaxations; }
    public int getQueueOperations() { return queueOperations; }

    public void reset() {
        dfsVisits = 0;
        edgeRelaxations = 0;
        queueOperations = 0;
    }

    @Override
    public String toString() {
        return String.format("Metrics{time=%d ns, dfsVisits=%d, edgeRelaxations=%d, queueOperations=%d}",
                getElapsedTime(), dfsVisits, edgeRelaxations, queueOperations);
    }
}