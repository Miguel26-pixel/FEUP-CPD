all:
	javac (find ./* | grep .java)

server:
	javac Store.java node/Node.java node/membership/MembershipService.java node/membership/log/Log.java node/membership/log/LogEntry.java node/store/KeyValueStore.java client/Services.java

client:
	javac TestClient.java client/Services.java

clean:
	find . -name \*.class -type f -delete