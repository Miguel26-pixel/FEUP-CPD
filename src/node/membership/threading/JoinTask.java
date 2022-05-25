package node.membership.threading;

import message.messages.JoinMessage;
import message.messages.MembershipMessage;
import node.membership.view.View;

import java.io.*;
import java.net.Socket;

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

        if (!this.isCounterCorrect(joinMessage.getCounter())) {
            return;
        }

        MembershipMessage reply = new MembershipMessage(this.view);

        try {
            Socket socket = new Socket(joinMessage.getIp(), joinMessage.getPort());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.write(reply.assemble());
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private boolean isCounterCorrect(int counter) {
        return counter % 2 == 0;
    }
}
