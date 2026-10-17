/**
 * Snake Doubly Linked List Implementation with Head and Tail Pointers.
 * 
 * DSA Structure: Doubly Linked List
 * null <-> Head <-> Node <-> Node <-> Tail <-> null
 * 
 * Performance Optimizations:
 *   - Head Insertion: O(1)
 *   - Tail Removal:   O(1) (Optimized from O(N) by introducing tail & prev pointers)
 *   - Tail Access:    O(1)
 */
public class Snake {

    private SnakeNode head;
    private SnakeNode tail;
    private int length;

    public Snake(int startX, int startY, int initialLength) {
        this.head = new SnakeNode(startX, startY);
        this.tail = this.head;
        this.length = 1;

        // Build initial body segments extending to the left with two-way links
        SnakeNode current = this.head;
        for (int i = 1; i < initialLength; i++) {
            SnakeNode nextNode = new SnakeNode(startX - i, startY);
            current.next = nextNode;
            nextNode.prev = current;
            current = nextNode;
            this.length++;
        }
        this.tail = current; // Direct pointer to tail
    }

    public SnakeNode getHead() {
        return head;
    }

    /**
     * Returns the tail node directly via tail pointer.
     * Time Complexity: O(1) constant time
     */
    public SnakeNode getTail() {
        return tail;
    }

    public int getLength() {
        return length;
    }

    /**
     * Inserts a new head node at the front of the doubly linked list.
     * Time Complexity: O(1)
     */
    public void addHead(int x, int y) {
        SnakeNode newHead = new SnakeNode(x, y);
        if (head == null) {
            head = newHead;
            tail = newHead;
        } else {
            newHead.next = head;
            head.prev = newHead;
            head = newHead;
        }
        length++;
    }

    /**
     * Removes the last node (tail) of the doubly linked list using the tail pointer.
     * Time Complexity: O(1) (Instant tail unlinking without traversing)
     * 
     * @return The removed SnakeNode
     */
    public SnakeNode removeTail() {
        if (tail == null) {
            return null;
        }

        SnakeNode removed = tail;

        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
            removed.prev = null;
        }

        length--;
        return removed;
    }

    /**
     * Moves the snake by adding a head and conditionally trimming the tail.
     * Time Complexity: O(1)
     * 
     * @param dx change in x
     * @param dy change in y
     * @param grow whether the snake ate food
     * @return removed tail node if not growing, or null if growing
     */
    public SnakeNode move(int dx, int dy, boolean grow) {
        int newX = head.x + dx;
        int newY = head.y + dy;

        addHead(newX, newY);

        if (!grow) {
            return removeTail();
        }
        return null;
    }

    /**
     * Checks if the snake's head collides with any part of its body.
     * Time Complexity: O(N)
     */
    public boolean checkSelfCollision() {
        if (head == null || head.next == null) {
            return false;
        }

        SnakeNode current = head.next;
        while (current != null) {
            if (current.x == head.x && current.y == head.y) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Checks if any node occupies the specified coordinates.
     * Time Complexity: O(N)
     */
    public boolean contains(int x, int y) {
        SnakeNode current = head;
        while (current != null) {
            if (current.x == x && current.y == y) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Visualizes the Doubly Linked List state in console.
     */
    public void printLinkedList() {
        System.out.print("Snake Doubly Linked List: Head");
        SnakeNode current = head;
        while (current != null) {
            System.out.print(" <-> (" + current.x + "," + current.y + ")");
            current = current.next;
        }
        System.out.println(" <-> null (Tail: (" + (tail != null ? tail.x + "," + tail.y : "none")
                + "), Length: " + length + ", Tail Removal: O(1))");
    }
}
