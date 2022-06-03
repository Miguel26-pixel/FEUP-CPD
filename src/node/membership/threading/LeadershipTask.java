package node.membership.threading;

import message.Message;
import message.messages.CoupMessage;
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
        String wannabeLeaderId = leadershipMessage.getLeaderId();

        if (wannabeLeaderId != null) {
            Message message;

            if (wannabeLeaderId.equals(nodeId)) {
                message = new CoupMessage(this.nodeId, this.nodeId);
                System.out.println("[M]Informing everyone that " + this.nodeId + " is the new leader.");
            } else {
                int comparison = this.view.compareTo(leadershipMessage.getView());

                if (comparison > 0 || (comparison == 0 && this.nodeId.compareTo(wannabeLeaderId) < 0)) {
                    System.out.println("[M]Rejecting " + wannabeLeaderId + " proposal as the new leader.");
                    return;
                }

                message = new LeadershipMessage(this.nodeId, wannabeLeaderId, leadershipMessage.getView());

                System.out.println("[M]Approved " + wannabeLeaderId + " as the new leader.");
            }

            ViewEntry nextNodeInfo = this.view.getNextUpEntry(UtilsHash.hashSHA256(this.nodeId));

            try {
                Socket socket = new Socket(nextNodeInfo.getAddress(), nextNodeInfo.getPort());
                socket.setSoTimeout(3000);
                UtilsTCP.sendTCPMessage(socket.getOutputStream(), message);
                System.out.println("[M]Informing " + nextNodeInfo.getAddress() + " about the leadership decision.");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

        }

    }
}
