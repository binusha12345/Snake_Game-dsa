/**
 * Hand-Built Generic LIFO Stack Data Structure.
 * 
 * DSA Component: Custom Stack
 * Implemented from scratch using linked nodes (top pointer).
 * 
 * Performance:
 *   - push():    O(1)
 *   - pop():     O(1)
 *   - peek():    O(1)
 *   - isEmpty(): O(1)
 *   - size():    O(1)
 * 
 * Game Application:
 *   Used as an Action / Score History Stack to record game milestones,
 *   bonus events, and state changes.
 */
public class CustomStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> top;
    private int size;

    public CustomStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Pushes an element onto the top of the stack.
     * Time Complexity: O(1)
     */
    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /**
     * Removes and returns the top element from the stack.
     * Time Complexity: O(1)
     */
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Retrieves but does not remove the top element.
     * Time Complexity: O(1)
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    /**
     * Returns an array representation of stack contents from top to bottom.
     */
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<T> curr = top;
        int idx = 0;
        while (curr != null) {
            arr[idx++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }
}
