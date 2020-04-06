class Queue {
    private Object[] queue;
    private int front;
    private int rear;

    public Queue(int capacity) {
        queue = new Object[capacity];
        front = 0;
        rear = -1;
    }

    public void enqueue(Object obj) {
        if (!isFull()) {
            rear++;
            queue[rear] = obj;
        }
    }

    public Object dequeue() {
        if (!isEmpty()) {
            Object rtr = queue[front];
            queue[front] = null;
            front++;
            return rtr;
        } else {
            return null;
        }
    }

    public Object peek() {
        if(!isEmpty()){
            return queue[front];
        }
        else{
            return null;
        }
    }

    public int size() {
        return rear - front + 1;
    }

    boolean isFull() {
        return rear + 1 == queue.length;
    }

    public boolean isEmpty() {
        return rear < front;
    }
}