package node.membership.threading;

import message.messages.MembershipMessage;
import node.membership.MembershipService;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsTCP;

import java.net.Socket;

public class LeaderManagement implements Runnable {
    private final MembershipService membershipService;

    public LeaderManagement(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public void run() {
        View view = membershipService.getView();
        for (ViewEntry node: view.getUpEntries().values()) {
            try {
                Socket socket = new Socket(node.getAddress(), node.getPort());

                UtilsTCP.sendTCPMessage(socket.getOutputStream(), new MembershipMessage(view));
            } catch (Exception ignored) {}
        }
    }
}
