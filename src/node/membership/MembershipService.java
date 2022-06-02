package node.membership;

import message.messages.JoinMessage;
import message.messages.LeadershipMessage;
import message.messages.LeaveMessage;
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
    private final View view;
    private final ThreadPool workers;
    private int membershipCounter;
    private final String identifier;
    private final AtomicBoolean isLeader;
    private ScheduledExecutorService membershipSender;

    public View getView() {
        return view;
    }

    public MembershipService(String identifier, ThreadPool workers) {
        this.identifier = identifier;
        this.workers = workers;

        this.view = new View();
        this.membershipCounter = 0;
        this.isLeader = new AtomicBoolean(false);
    }

    public boolean isLeader() {
        return this.isLeader.get();
    }

    public void setLeader() {
        this.isLeader.set(true);
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.membershipSender.schedule(new LeaderManagement(this), 1000, TimeUnit.MILLISECONDS);
    }

    public void removeLeader() {
        this.isLeader.set(false);
        this.membershipSender.shutdown();
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
                LeaveMessage message = new LeaveMessage(this.membershipCounter, this.identifier);
                udpAgent.send(message);
            } catch (IOException e) {
                return false;
            }

            this.membershipCounter++;
            return true;
        }

        return false;
    }

    public void processJoin(String joinMessageString, KeyValueStore keyValueStore) {
        workers.execute(new JoinTask(this.view, joinMessageString, keyValueStore, workers));
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
