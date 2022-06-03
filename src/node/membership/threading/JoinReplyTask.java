package node.membership.threading;

import message.messages.JoinMessage;
import message.messages.JoinReplyMessage;
import node.comms.UDPAgent;
import node.membership.MembershipService;

import java.io.IOException;

public class JoinReplyTask extends Thread {
    private final MembershipService membershipService;
    private final String joinReplyMessageString;
    private final UDPAgent udpAgent;
    private final int tcpPort;

    public JoinReplyTask(MembershipService membershipService, String joinReplyMessageString, UDPAgent agent, int tcpPort) {
        this.membershipService = membershipService;
        this.joinReplyMessageString = joinReplyMessageString;
        this.udpAgent = agent;
        this.tcpPort = tcpPort;
    }

    @Override
    public void run() {
        JoinReplyMessage joinReplyMessage = new JoinReplyMessage(joinReplyMessageString);
        int receivedCounter = joinReplyMessage.getCounter();

        if (this.membershipService.getMembershipCounter() < receivedCounter) {
            System.out.println("[M]" + joinReplyMessage.getOriginId() + " just informed of wrong counter");
            if ((receivedCounter % 2) != 0) {
                receivedCounter += 1;
            }

            this.membershipService.setMembershipCounter(receivedCounter);

            try {
                this.udpAgent.send(new JoinMessage(this.membershipService.getMembershipCounter(), this.tcpPort, this.membershipService.getIdentifier()));
            } catch (IOException ignored) {}
        }
    }
}
