package node.membership;

import message.messages.JoinMessage;
import message.messages.LeaveMessage;
import node.comms.UDPAgent;
import node.membership.threading.JoinTask;
import node.membership.threading.LeaveTask;
import node.membership.threading.MembershipTask;
import node.membership.view.View;
import threading.ThreadPool;

import java.io.IOException;

public class MembershipService {
    private final View view;
    private final ThreadPool workers;
    private int membershipCounter;
    private final String identifier;

    public View getView() {
        return view;
    }

    public MembershipService(String identifier, ThreadPool workers) {
        this.identifier = identifier;
        this.workers = workers;

        this.view = new View();
        this.membershipCounter = 0;
    }

    public boolean join(UDPAgent udpAgent, int tcpPort) {
        if (this.membershipCounter % 2 == 0) {
            try {
                udpAgent.send(new JoinMessage(this.membershipCounter, tcpPort, this.identifier));
            } catch (IOException e) {
                return false;
            }

            this.membershipCounter++;
            return true;
        }

        return false;
    }

    public boolean leave(UDPAgent udpAgent) {
        if (this.membershipCounter % 2 != 0) {
            try {
                udpAgent.send(new LeaveMessage(this.membershipCounter, this.identifier));
            } catch (IOException e) {
                return false;
            }

            this.membershipCounter++;
            return true;
        }

        return false;
    }

    public void processJoin(String joinMessageString) {
        workers.execute(new JoinTask(this.view, joinMessageString));
    }

    public void processLeave(String leaveMessageString) {
        workers.execute(new LeaveTask(this.view, leaveMessageString));
    }

    public void processMembership(String membershipMessageString) {
        workers.execute(new MembershipTask(this.view, membershipMessageString));
    }
}
