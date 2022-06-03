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
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.removeLeader();
    }

    public boolean isLeader() {
        return this.isLeader.get();
    }

    public void setLeader() {
        this.isLeader.set(true);
        this.membershipSender.shutdown();
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.membershipSender.scheduleAtFixedRate(new LeaderManagement(this), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void removeLeader() {
        this.isLeader.set(false);
        this.membershipSender.shutdown();
        this.membershipSender = Executors.newSingleThreadScheduledExecutor();
        this.membershipSender.scheduleAtFixedRate(new LeaderSearch(this.identifier, this), 0, 10000, TimeUnit.MILLISECONDS);
    }

    public boolean join(UDPAgent udpAgent, int tcpPort) {
        if (this.membershipCounter % 2 == 0) {
            try {
                udpAgent.send(new JoinMessage(this.membershipCounter, tcpPort, this.identifier));
            } catch (IOException e) {
                return false;
            }

            long secondsSinceEpoch = System.currentTimeMillis() / 1000;
            this.view.addEntry(this.identifier, new ViewEntry(tcpPort, this.identifier, this.membershipCounter, secondsSinceEpoch));

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
