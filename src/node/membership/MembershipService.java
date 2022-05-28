package node.membership;

import node.membership.threading.JoinTask;
import node.membership.threading.LeaveTask;
import node.membership.threading.MembershipTask;
import node.membership.view.View;
import threading.ThreadPool;

public class MembershipService {
    private final View view;
    private final ThreadPool workers;

    public MembershipService() {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        this.workers = new ThreadPool(numberOfCores, numberOfCores);

        this.view = new View();
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
