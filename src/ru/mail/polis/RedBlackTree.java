package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Random;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {

    protected static final boolean BLACK = false;
    protected static final boolean RED = true;
    private final Node nil = new Node(null);

    class Node {

        Node(E value) {
            this.value = value;
        }

        E value;
        Node left = nil;
        Node right = nil;
        Node parent = nil;
        boolean color = BLACK;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != nil) {
                sb.append(", l=").append(left);
            }
            if (right != nil) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private Node root = nil;
    private int size;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != nil) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != nil) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == nil) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == nil;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != nil) {
            Node curr = root;
            while (curr != nil) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        boolean res;
        Node nodeToAdd = new Node(value);
        Node compNode = root;
        if (root != nil) {
            while (true) {
                if (compare(nodeToAdd.value, compNode.value) < 0) { //nodeToAdd.value < compNode.value
                    if (compNode.left == nil) {
                        compNode.left = nodeToAdd;
                        nodeToAdd.parent = compNode;
                        nodeToAdd.color = RED;
                        res = true;
                        break;
                    } else {
                        compNode = compNode.left;
                    }
                } else if (compare(nodeToAdd.value, compNode.value) > 0) { //nodeToAdd.value > compNode.value
                    if (compNode.right == nil) {
                        compNode.right = nodeToAdd;
                        nodeToAdd.parent = compNode;
                        nodeToAdd.color = RED;
                        res = true;
                        break;
                    } else { //nodeToAdd.value == compNode.value
                        compNode = compNode.right;
                    }
                } else {
                    res = false;
                    break;
                }
            }
            if(res)
                fixUpOnAdd(nodeToAdd);
        } else {
            root = nodeToAdd;
            nodeToAdd.color = BLACK;
            nodeToAdd.parent = nil;
            res = true;
        }
        if(res)
            size++;
        return res;
    }

    private void fixUpOnAdd(Node node) {
        while (node.parent.color != BLACK) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                if (uncle.color == RED && uncle != nil) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right){
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;
                if (uncle.color == RED && uncle != nil) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left){
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    void rotateLeft(Node n) {
        if (n.parent != nil) {
            if (n == n.parent.left) {
                n.parent.left = n.right;
            } else {
                n.parent.right = n.right;
            }
            n.right.parent = n.parent;
            n.parent = n.right;
            if (n.right.left != nil) {
                n.right.left.parent = n;
            }
            n.right = n.right.left;
            n.parent.left = n;
            return;
        }
        Node rootRight = root.right;
        root.right = rootRight.left;
        rootRight.left.parent = root;
        root.parent = rootRight;
        rootRight.left = root;
        rootRight.parent = nil;
        root = rootRight;
    }

    void rotateRight(Node n) {
        if (n.parent != nil) {
            if (n == n.parent.left) {
                n.parent.left = n.left;
            } else {
                n.parent.right = n.left;
            }
            n.left.parent = n.parent;
            n.parent = n.left;
            if (n.left.right != nil) {
                n.left.right.parent = n;
            }
            n.left = n.left.right;
            n.parent.right = n;
            return;
        }
        Node rootLeft = root.left;
        root.left = root.left.right;
        rootLeft.right.parent = root;
        root.parent = rootLeft;
        rootLeft.right = root;
        rootLeft.parent = nil;
        root = rootLeft;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        Node nodeForRemove, n, k;
        nodeForRemove = nodeForRemove(value, root);
        if(nodeForRemove == nil){
            return false;
        }
        boolean isFixupOnRemoveNeeded;
        if(nodeForRemove.color == BLACK){
            isFixupOnRemoveNeeded = true;
        } else {
            isFixupOnRemoveNeeded = false;
        }

        if(nodeForRemove.left == nil) {
            n = nodeForRemove.right;
            performTransplant(nodeForRemove, nodeForRemove.right);
        } else if(nodeForRemove.right == nil){
            n = nodeForRemove.left;
            performTransplant(nodeForRemove, nodeForRemove.left);
        } else {
            Node tempRoot = nodeForRemove.right;
            while(tempRoot.left != nil) {
                tempRoot = tempRoot.left;
            }
            k = tempRoot;
            if(k.color == BLACK){
                isFixupOnRemoveNeeded = true;
            } else {
                isFixupOnRemoveNeeded = false;
            }
            n = k.right;
            if(k.parent == nodeForRemove) {
                n.parent = k;
            } else {
                performTransplant(k, k.right);
                k.right = nodeForRemove.right;
                k.right.parent = k;
            }
            performTransplant(nodeForRemove, k);
            k.left = nodeForRemove.left;
            k.left.parent = k;
            k.color = nodeForRemove.color;
        }

        if(isFixupOnRemoveNeeded){
            fixUpOnRemove(n);
        }

        size--;
        return true;
    }

    private Node nodeForRemove(E value, Node n) {
        if (isEmpty()) {
            return nil;
        }
        if (compare(value, n.value) < 0) { //value < n.value
            if (n.left != nil) {
                return nodeForRemove(value, n.left);
            }
        } else if (compare(value, n.value) > 0) { //value > n.value
            if (n.right != nil) {
                return nodeForRemove(value, n.right);
            }
        } else { //value == n.value
            return n;
        }
        return nil;
    }

    void performTransplant(Node n, Node k) {
        if(n.parent == nil) {
            root = k;
        } else if(n == n.parent.left) {
            n.parent.right = k;
        } else {
            n.parent.left = k;
        }
        k.parent = n.parent;
    }

    void fixUpOnRemove(Node node) {
        while(node != root && node.color == BLACK) {
            if(node == node.parent.left) {
                Node w = node.parent.right;
                if(w.color == RED) {
                    w.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    w = node.parent.right;
                }
                if(w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    node = node.parent;
                    continue;
                } else if(w.right.color == BLACK) {
                    w.left.color = BLACK;
                    w.color = RED;
                    rotateRight(w);
                    w = node.parent.right;
                }
                if(w.right.color == RED) {
                    w.color = node.parent.color;
                    node.parent.color = BLACK;
                    w.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                Node w = node.parent.left;
                if(w.color == RED) {
                    w.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    w = node.parent.left;
                }
                if(w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    node = node.parent;
                    continue;
                } else if(w.left.color == BLACK) {
                    w.right.color = BLACK;
                    w.color = RED;
                    rotateLeft(w);
                    w = node.parent.left;
                }
                if(w.left.color == RED) {
                    w.color = node.parent.color;
                    node.parent.color = BLACK;
                    w.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "BST{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new RedBlackTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        tree = new RedBlackTree<>();
        for (int i = 0; i < 30; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }
}