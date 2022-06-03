package node.membership.threading;

import message.messages.LeadershipMessage;
import node.membership.MembershipService;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.net.Socket;

public class LeaderSearch implements Runnable {
    private final String nodeId;
    private final MembershipService membershipService;

    public LeaderSearch(String nodeId, MembershipService membershipService) {
        this.nodeId = nodeId;
        this.membershipService = membershipService;
    }

    @Override
    public void run() {
        View view = this.membershipService.getView();
        LeadershipMessage leadershipMessage = new LeadershipMessage(nodeId, nodeId, view);

        while (true) {
            ViewEntry nextNode = view.getNextUpEntry(UtilsHash.hashSHA256(nodeId));
            if (nextNode == null) {
                return;
            }
            try {
                Socket socket = new Socket(nextNode.getAddress(), nextNode.getPort());

                UtilsTCP.sendTCPMessage(socket.getOutputStream(), leadershipMessage);
                System.out.println("[M]Asking to become the leader. Redirected to " + nextNode.getAddress());
                break;
            } catch (Exception ignored) {
                this.membershipService.flagNodeDown(nextNode.getAddress());
            }
        }
    }
}
