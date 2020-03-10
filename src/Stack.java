public class Stack {
    private Object[] elems;
    private int top;

    public Stack(int stackSize) {
        elems = new Object[stackSize];
        top = -1;
    }

    public void push(Object obj) {
        if (!isFull()) {
            top++;
            elems[top] = obj;
        } else {
            System.out.println("Stack full.");
        }
    }

    public Object pop() {
        if (!isEmpty()) {
            Object rtr = elems[top];
            top--;
            return rtr;
        } else {
            return null;
        }
    }

    public Object peek() {
        if (!isEmpty()) {
            return elems[top];
        } else {
            return null;
        }
    }

    public int size() {
        return top + 1;
    }

    public boolean isEmpty() {
        if (top == -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFull() {
        if (top + 1 == elems.length) {
            return true;
        } else {
            return false;
        }
    }
}