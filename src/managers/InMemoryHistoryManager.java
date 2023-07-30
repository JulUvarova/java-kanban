package managers;

import tasks.Task;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private Map<Integer, Node<Task>> customLinkedList = new HashMap<>();

    @Override
    public void add(int id, Task task) {
        removeNode(customLinkedList.get(id));
        Node newNode = linkLast(task);
        customLinkedList.put(id, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
       removeNode(customLinkedList.get(id));
       customLinkedList.remove(id);
    }

    private Node<Task> linkLast(Task task) {
        if (task != null) {
            final Node<Task> oldTail = tail;
            final Node<Task> newTail = new Node<>(oldTail, task, null);
            tail = newTail;
            if (oldTail == null)
                head = newTail;
            else
                oldTail.next = newTail;
            return tail;
        }
        return null;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        for (Node<Task> x = head; x != null; x = x.next) {
            history.add(x.data);
        }
        return history;
    }

    private void removeNode(Node x) {
       if (x != null) {
           Node next = x.next;
           Node prev = x.prev;

           if (prev == null) {
               head = next;
               head.prev = null;
           } else {
               prev.next = next;
           }

           if (next == null) {
               tail = prev;
               tail.next = null;
           } else {
               next.prev = prev;
           }
       }
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
