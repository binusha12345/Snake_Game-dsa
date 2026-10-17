/**
 * Hand-Built Generic FIFO Queue Data Structure.
 * 
 * DSA Component: Custom Queue
 * Implemented from scratch using linked nodes (front and rear pointers).
 * 
 * Performance:
 *   - enqueue(): O(1)
 *   - dequeue(): O(1)
 *   - peek():    O(1)
 *   - isEmpty(): O(1)
 *   - size():    O(1)
 * 
 * Game Application:
 *   Used as an Input Buffer Queue to store rapid player directional inputs
 *   and execute them sequentially on game ticks, preventing dropped keystrokes.
 */
public class CustomQueue<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public CustomQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Inserts an element at the rear of the queue.
     * Time Complexity: O(1)
     */
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the front element from the queue.
     * Time Complexity: O(1)
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Retrieves but does not remove the front element.
     * Time Complexity: O(1)
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /**
     * Clears all elements from the queue.
     */
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
}
