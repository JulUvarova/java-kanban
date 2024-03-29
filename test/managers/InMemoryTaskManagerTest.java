package managers;

import managers.taskManagers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void getManager() {
        manager = (InMemoryTaskManager) Managers.getDefaultTaskManager(); // реализация T manager родителя
    }
}