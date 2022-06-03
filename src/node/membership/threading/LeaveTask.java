package node.membership.threading;

import message.messages.LeaveMessage;
import node.membership.view.View;
import node.membership.view.ViewEntry;

public class LeaveTask extends Thread {
    private final View view;
    private final String leaveMessageString;

    public LeaveTask(View view, String leaveMessage) {
        this.view = view;
        this.leaveMessageString = leaveMessage;
    }

    @Override
    public void run() {
        LeaveMessage leaveMessage = new LeaveMessage(leaveMessageString);

        if (!this.isCounterCorrect(leaveMessage.getCounter())) {
            return;
        }

        long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        this.view.addEntry(leaveMessage.getOriginId(), new ViewEntry(ViewEntry.INVALID_INT, leaveMessage.getOriginId(), leaveMessage.getCounter(), secondsSinceEpoch), true);

        System.out.println("[M]Received a leave message from " + leaveMessage.getOriginId());
    }

    private boolean isCounterCorrect(int counter) {
        return (counter % 2) != 0;
    }
}
