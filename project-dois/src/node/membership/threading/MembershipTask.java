package node.membership.threading;

import message.messages.MembershipMessage;
import node.membership.view.View;

public class MembershipTask extends Thread {
    private final View view;
    private final String membershipMessageString;

    public MembershipTask(View view, String membershipMessage) {
        this.view = view;
        this.membershipMessageString = membershipMessage;
    }

    @Override
    public void run() {
        MembershipMessage membershipMessage = new MembershipMessage(membershipMessageString);

        this.view.copyView(membershipMessage.getView(), true);
    }
}
