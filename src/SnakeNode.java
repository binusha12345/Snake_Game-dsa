/**
 * Represents a single node in the snake body.
 * 
 * Upgraded to Doubly Linked List Node:
 * Contains pointers to both 'next' and 'prev' segments to enable O(1) tail removal.
 */
public class SnakeNode {

    int x;
    int y;
    SnakeNode next;
    SnakeNode prev; // Pointer to previous node for O(1) Doubly Linked List tail removal

    public SnakeNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.next = null;
        this.prev = null;
    }
}
