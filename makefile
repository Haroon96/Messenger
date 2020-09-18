JC = javac
RM = rm

default: clean_client clean_server server client

clean_server:
	rm -rf build/server

clean_client:
	rm -rf build/client

server: clean_server
	mkdir -p build/server
	javac -cp src src/com/haroon96/server/Server.java -d build/server

client: clean_client
	mkdir -p build/client
	cp -r res/* build/client/
	javac -cp src src/com/haroon96/client/Client.java -d build/client
