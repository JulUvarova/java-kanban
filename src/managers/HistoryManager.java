package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();
    void remove(int id);

    void add(int id, Task task);
}
