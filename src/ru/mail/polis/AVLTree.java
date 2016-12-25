package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Random;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
        }

        E value;
        Node left;
        Node right;
        Node parent;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
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
        while (curr.right != null) {
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
        if (curr == null) {
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
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
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

        Node nodeToAdd = new Node(value);
        boolean res = addAVLNode(root, nodeToAdd);
        if(res)
            size++;
        return res;
    }

    private boolean addAVLNode(Node p, Node q) {
        boolean result;
        if (p == null) {
            root = q;
            result = true;
        } else {
            if (compare(q.value, p.value) < 0){ //q < p.value
                if(p.left == null) {
                    p.left = q;
                    p.left.parent = p;

                    checkBalance(p);
                    result = true;
                } else {
                    result = addAVLNode(p.left, q);
                }
            } else if(compare(q.value, p.value) > 0){ //q > p.value
                if(p.right == null) {
                    p.right = q;
                    p.right.parent = p;

                    checkBalance(p.right);
                    result = true;
                } else {
                    result = addAVLNode(p.right, q);
                }
            } else {
                result = false; //узел уже имеется
            }
        }
        return result;
    }


    private void checkBalance(Node current) {
        int currentBalanceFactor = balanceFactor(current);
        if (currentBalanceFactor < -1) {
            if (height(current.left.left) < height(current.left.right)) {
                current = rotateLeftRight(current);
            } else {
                current = rotateRight(current);
            }
        } else if (currentBalanceFactor > 1) {
            if (height(current.right.right) < height(current.right.left)) {
                current = rotateRightLeft(current);
            } else {
                current = rotateLeft(current);
            }
        }
        if (current.parent != null) {
            checkBalance(current.parent);
        } else {
            this.root = current;
        }
    }

    private int height(Node current) {
        if (current == null) {
            return -1;
        }
        if (current.left == null && current.right == null) {
            return 0;
        } else if (current.left == null) {
            return 1 + height(current.right);
        } else if (current.right == null) {
            return 1 + height(current.left);
        } else {
            return 1 + Math.max(height(current.left), height(current.right));
        }
    }

    private int balanceFactor(Node current) {
        return height(current.right) - height(current.left);
    }

    private Node rotateLeft(Node n) {
        Node v = n.right;
        n.right = v.left;

        if (v.left != null) {
            v.left.parent = n;
        }

        v.parent = n.parent;

        if (n != root) {
            if (n.parent.left == n) {
                n.parent.left = v;
            } else {
                n.parent.right = v;
            }
        }
        v.left = n;
        n.parent = v;

        return n;
    }

    private Node rotateRight(Node n) {
        Node v = n.left;
        n.left = v.right;

        if (v.right != null) {
            v.right.parent = n;
        }

        v.parent = n.parent;

        if (n != root) {
            if (n.parent.right == n) {
                n.parent.right = v;
            } else {
                n.parent.left = v;
            }
        }

        v.right = n;
        n.parent = v;

        return n;
    }

    private Node rotateLeftRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    private Node rotateRightLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        boolean res = removeByValue(this.root, value);
        if(res)
            size--;
        return res;
    }

    public boolean removeByValue(Node p, E value) {
        boolean result;
        if(p == null) {
            result = false;
        } else {
            if(compare(p.value, value) > 0)  {
                result = removeByValue(p.left, value);
            } else if(compare(p.value, value) < 0) {
                result = removeByValue(p.right, value);
            } else { //value == p.value
                removeAVLNode(p);
                result = true;
            }
        }
        return result;
    }

    public void removeAVLNode(Node q) {
        Node r;
        if(q.left == null || q.right == null) {
            if(q.parent == null) {
                if(q.left != null){
                    root = q.left;
                    root.parent = null;
                }else if(q.right != null){
                    root = q.right;
                    root.parent = null;
                }else{
                    root = null;
                }
                q = null;
                return;
            }
            r = q;
        } else {
            r = successor(q);
            q.value = r.value;
        }

        Node p;
        if(r.left != null) {
            p = r.left;
        } else {
            p = r.right;
        }

        if(p != null) {
            p.parent = r.parent;
        }

        if(r.parent == null) {
            root = p;
        } else {
            if(r == r.parent.left) {
                r.parent.left = p;
            } else {
                r.parent.right = p;
            }
            checkBalance(r.parent);
        }
    }

    public Node successor(Node q) {
        if(q.right != null) {
            Node r = q.right;
            while(r.left!=null) {
                r = r.left;
            }
            return r;
        } else {
            Node p = q.parent;
            while(p != null && q == p.right) {
                q = p;
                p = q.parent;
            }
            return p;
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "BST{" + root + "}";
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
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
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        tree = new AVLTree<>();
        for (int i = 0; i < 30; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        ISortedSet<Integer> set = new AVLTree<Integer>();
        for (int i = 0; i < 1000; i++) {
            set.add(rnd.nextInt(1000));
        }
        for (int i = 0; i < 1000; i++) {
            set.remove(rnd.nextInt(1000));
        }
    }
}