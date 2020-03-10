class Queue {
    private Object[] queue;
    private int front;
    private int rear;

    public Queue(int capacity) {
        queue = new Object[capacity];
        front = 0;
        rear = -1;
    }

    void enqueue(Object obj) {
        if (!isFull()) {
            rear++;
            queue[rear] = obj;
        }
    }

    Object dequeue() {
        if (!isEmpty()) {
            Object rtr = queue[front];
            queue[front] = null;
            front++;
            return rtr;
        } else {
            return null;
        }
    }

    Object peek() {
        if(!isEmpty()){
            return queue[front];
        }
        else{
            return null;
        }
    }

    int size() {
        return rear - front + 1;
    }

    boolean isFull() {
        return rear + 1 == queue.length;
    }

    boolean isEmpty() {
        return rear < front;
    }
}