package node.membership.threading;

import message.header.FieldType;
import message.messages.CoupMessage;
import message.messages.LeadershipMessage;
import node.membership.MembershipService;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class CoupTask extends Thread {
    private final View view;
    private final String nodeId;
    private final MembershipService membershipService;
    private final String coupMessageString;

    public CoupTask(View view, String nodeId, MembershipService membershipService, String coupMessageString) {
        this.view = view;
        this.nodeId = nodeId;
        this.membershipService = membershipService;
        this.coupMessageString = coupMessageString;
    }

    @Override
    public void run() {
        CoupMessage coupMessage = new CoupMessage(coupMessageString);
        String couperId = coupMessage.getCouperId();

        if (couperId != null) {
            if (couperId.equals(nodeId)) {
                this.membershipService.setLeader(true);
                return;
            }

            if (this.membershipService.isLeader()) {
                this.membershipService.setLeader(false);
            }

            ViewEntry nextNodeInfo = this.view.getNextUpEntry(UtilsHash.hashSHA256(this.nodeId));

            CoupMessage redirectionMessage = new CoupMessage(nodeId, couperId);

            try {
                Socket socket = new Socket(nextNodeInfo.getAddress(), nextNodeInfo.getPort());
                UtilsTCP.sendTCPMessage(socket.getOutputStream(), redirectionMessage);
            } catch (IOException ignored) {}
        }
    }
}
