package node.membership;

import message.messages.JoinMessage;
import message.messages.JoinReplyMessage;
import message.messages.LeadershipMessage;
import message.messages.LeaveMessage;
import node.Node;
import node.comms.UDPAgent;
import node.membership.threading.*;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import node.store.KeyValueStore;
import threading.ThreadPool;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MembershipService {
    private final Node parent;
    private final View view;
    private final ThreadPool workers;
    private int membershipCounter;
    private final String identifier;
    private final AtomicBoolean isLeader;
    private ScheduledExecutorService membershipSender;

    public View getView() {
        return view;
    }

    public MembershipService(Node parent, String identifier, String folderPath, ThreadPool workers) {
        this.parent = parent;
        this.identifier = identifier;
        this.workers = workers;

        this.view = new View();
        this.view.createLog(folderPath);
        this.view.copyView(new View(this.view.getLog().getLog()), true);

        this.membershipCounter = Integer.max(0, this.getClosestPairCeil(this.view.getCounter(UtilsHash.hashSHA256(this.identifier)) + 1));

        this.isLeader = new AtomicBoolean(false);
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
    }

    public int getMembershipCounter() {
        return membershipCounter;
    }

    public void setMembershipCounter(int membershipCounter) {
        this.membershipCounter = membershipCounter;
    }

    public boolean isLeader() {
        return this.isLeader.get();
    }

    public void setLeader() {
        this.isLeader.set(true);
        this.membershipSender.shutdown();
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.membershipSender.scheduleAtFixedRate(new LeaderManagement(this), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void removeLeader() {
        this.isLeader.set(false);
        this.membershipSender.shutdown();
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.membershipSender.scheduleAtFixedRate(new LeaderSearch(this.identifier, this), 1000, 10000, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        this.membershipSender.shutdown();
    }

    public void flagNodeDown(String nodeId) {
        this.view.setDown(nodeId, true);
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean join(UDPAgent udpAgent, int tcpPort) {
        if (this.membershipCounter % 2 == 0) {
            try {
                udpAgent.send(new JoinMessage(this.membershipCounter, tcpPort, this.identifier));
            } catch (IOException e) {
                return false;
            }

            long secondsSinceEpoch = System.currentTimeMillis() / 1000;
            this.view.addEntry(this.identifier, new ViewEntry(tcpPort, this.identifier, this.membershipCounter, secondsSinceEpoch), true);

            this.removeLeader();

            this.membershipCounter++;
            return true;
        }

        return false;
    }

    public boolean leave(UDPAgent udpAgent) {
        if (this.membershipCounter % 2 != 0) {
            this.shutdown();
            try {
                LeaveMessage message = new LeaveMessage(this.membershipCounter, this.identifier);
                udpAgent.send(message);
            } catch (IOException e) {
                return false;
            }

            long secondsSinceEpoch = System.currentTimeMillis() / 1000;
            this.view.addEntry(this.identifier, new ViewEntry(ViewEntry.INVALID_INT, this.identifier, this.membershipCounter, secondsSinceEpoch), true);

            this.membershipCounter++;
            return true;
        }

        return false;
    }

    private int getClosestPairCeil(int counter) {
        if ((counter % 2) == 0) {
            return counter;
        } else {
            return counter + 1;
        }
    }

    public void processJoin(String joinMessageString, KeyValueStore keyValueStore, String myId) {
        workers.execute(new JoinTask(this.view, joinMessageString, keyValueStore, workers, myId));
    }

    public void processJoinReply(String joinReplyMessage) {
        workers.execute(new JoinReplyTask(this, joinReplyMessage, this.parent.getUdpAgent(), this.parent.getTcpAgent().getPort()));
    }

    public void processLeave(String leaveMessageString) {
        workers.execute(new LeaveTask(this.view, leaveMessageString));
    }

    public void processMembership(String membershipMessageString) {
        workers.execute(new MembershipTask(this.view, membershipMessageString));
    }

    public void processLeadership(String leadershipMessageString) {
        workers.execute(new LeadershipTask(this.view, this.identifier, leadershipMessageString));
    }

    public void processCoup(String coupMessageString) {
        workers.execute(new CoupTask(this.view, this.identifier, this, coupMessageString));
    }
}
