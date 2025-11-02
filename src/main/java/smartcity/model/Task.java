package smartcity.model;

import java.util.List;

public class Task {
    private int id;
    private String name;
    private int duration; // Node duration in time units
    private List<Integer> dependencies;

    public Task(int id, String name, int duration, List<Integer> dependencies) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.dependencies = dependencies;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public List<Integer> getDependencies() { return dependencies; }
    public void setDependencies(List<Integer> dependencies) { this.dependencies = dependencies; }

    @Override
    public String toString() {
        return String.format("Task{id=%d, name='%s', duration=%d, dependencies=%s}",
                id, name, duration, dependencies);
    }
}
