package amata1219.like.bryionake.dsl.argument;

import java.util.ArrayDeque;
import java.util.Queue;

public class ParsedArgumentQueue {

    private final Queue<Object> queue = new ArrayDeque<>();

    public void offer(Object parsedArgument) {
        queue.offer(parsedArgument);
    }

    public <T> T poll() {
        return (T) queue.poll();
    }

    public <T> T peek() {
        return (T) queue.peek();
    }

}
