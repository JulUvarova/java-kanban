package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private HashMap<Integer, Node<Task>> customLinkedList = new HashMap<>();

    @Override
    public void add(int id, Task task) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
        }
        Node newNode = linkLast(task);
        customLinkedList.put(id, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove(id);
        }
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(oldTail, task, null);
        tail = newTail;
        if (oldTail == null)
            head = newTail;
        else
            oldTail.next = newTail;
        return tail;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        for (Node<Task> x = head; x != null; x = x.next) {
            history.add(x.data);
        }
        return history;
    }

    private void removeNode(Node x) {
        Node next = x.next;
        Node prev = x.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.data = null;
    }

    private class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node prev, T data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
