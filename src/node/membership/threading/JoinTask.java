package node.membership.threading;

import message.messages.JoinMessage;
import node.membership.view.View;

public class JoinTask extends Thread {
    private final View view;
    private final String joinMessageString;

    public JoinTask(View view, String joinMessage) {
        this.view = view;
        this.joinMessageString = joinMessage;
    }

    @Override
    public void run() {
        JoinMessage joinMessage = new JoinMessage(joinMessageString);
    }
}
