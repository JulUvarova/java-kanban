package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void remove(int id);

    void add(int id, Task task);

    List<Task> getHistory();
}
