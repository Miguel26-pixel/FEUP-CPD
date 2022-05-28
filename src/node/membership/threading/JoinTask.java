package node.membership.threading;

import message.messages.JoinMessage;
import message.messages.MembershipMessage;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsTCP;

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

        long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        this.view.addEntry(joinMessage.getOriginId(), new ViewEntry("Foda-se", "Isto não devia estar aqui", joinMessage.getCounter(), secondsSinceEpoch), true);

        MembershipMessage reply = new MembershipMessage(this.view);

        try {
            Socket socket = new Socket(joinMessage.getOriginId(), joinMessage.getPort());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            UtilsTCP.sendTCPMessage(output, reply);
        } catch (IOException ignored) {
        }
    }

    private boolean isCounterCorrect(int counter) {
        return counter % 2 == 0;
    }
}
