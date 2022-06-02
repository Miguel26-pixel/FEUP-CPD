package node.membership.threading;

import message.header.FieldType;
import message.messages.LeadershipMessage;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class LeadershipTask extends Thread {
    private final View view;
    private final String nodeId;
    private final String leadershipMessageString;

    public LeadershipTask(View view, String nodeId, String leadershipMessageString) {
        this.view = view;
        this.leadershipMessageString = leadershipMessageString;
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        LeadershipMessage leadershipMessage = new LeadershipMessage(leadershipMessageString);
        String leadershipMessageOrigin = leadershipMessage.getFieldValue(FieldType.ORIGINID);

        if (leadershipMessageOrigin != null) {
            if (leadershipMessageOrigin.equals(nodeId)) {
                System.out.println("I'M THE LEADER BITCHES");
                //TODO: send coup message
            }
        }

        ViewEntry nextNodeInfo = this.view.getNextUpEntry(UtilsHash.hashSHA256(this.nodeId));

        LeadershipMessage redirectionMessage = new LeadershipMessage(nextNodeInfo.getAddress(), leadershipMessage.getView());

        try {
            Socket socket = new Socket(nextNodeInfo.getAddress(), nextNodeInfo.getPort());
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), redirectionMessage);
        } catch (IOException ignored) {}
    }
}
