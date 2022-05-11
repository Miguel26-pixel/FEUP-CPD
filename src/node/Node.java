package node;

import node.membership.log.Log;

public class Node {
    private int membership_counter;
    private Log log;

    public Node() {
        this.membership_counter = 0;
        this.log = new Log();
    }

    public boolean join() {
        if (!this.canJoin()) {
            return false;
        }

        return true;
    }

    public boolean leave() {
        if (!this.canLeave()) {
            return false;
        }

        return true;
    }

    private boolean canJoin() {
        return this.membership_counter % 2 == 0;
    }

    private boolean canLeave() {
        return this.membership_counter % 2 != 0;
    }
}
