package node.membership.threading;

import message.messages.JoinMessage;
import message.messages.LeaveMessage;
import message.messages.MembershipMessage;
import node.membership.view.View;
import utils.UtilsTCP;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

        // Update view to reflect changes
    }

    private boolean isCounterCorrect(int counter) {
        return (counter % 2) != 0;
    }
}
