all: server client

server:
	javac Store.java node/Node.java node/membership/MembershipService.java node/membership/log/Log.java node/membership/log/LogEntry.java client/Services.java

client:
	javac TestClient.java client/Services.java

clean:
	find . -name \*.class -type f -delete