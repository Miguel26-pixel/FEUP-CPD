package node.membership;

public class MembershipService {
    private final String mcastIP;
    private final String mcastPort;
    private final String membershipPort;
    private int membership_counter;

    public MembershipService(String mcastIP, String mcastPort, String membershipPort) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membershipPort = membershipPort;
        this.membership_counter = 0;
    }

    public boolean join() {
        if (!this.canJoin()) {
            membership_counter++;
            return false;
        }
        membership_counter++;
        return true;
    }

    public boolean leave() {
        if (!this.canLeave()) {
            membership_counter++;
            return false;
        }
        membership_counter++;
        return true;
    }

    private boolean canJoin() {
        return this.membership_counter % 2 == 0;
    }

    private boolean canLeave() {
        return this.membership_counter % 2 != 0;
    }

    //for testing TCP communication
    public boolean sayHello(String nodeIP, String nodePort) {
        return false;
    }
}
