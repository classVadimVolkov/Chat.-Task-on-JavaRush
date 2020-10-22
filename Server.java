package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        int port = ConsoleHelper.readInt();
        try (ServerSocket ss = new ServerSocket(port);
        ) {
            ConsoleHelper.writeMessage("Server is started");
            while (true) {
                new Handler(ss.accept()).start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public static void sendBroadcastMessage(Message message) {
        connectionMap.forEach((keyString, valueConnection) -> {
            try {
                valueConnection.send(message);
            } catch (IOException exception) {
                ConsoleHelper.writeMessage("The message wasn't submitted and there is no information about new user");
            }
        });
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Enter your name:"));
                Message userName = connection.receive();
                if (userName.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Try it again! It's not this type");
                    continue;
                }
                if (userName.getData() == null || userName.getData().isEmpty()) {
                    ConsoleHelper.writeMessage("Try it again! The username is empty");
                    continue;
                }
                if (connectionMap.containsKey(userName.getData())) {
                    ConsoleHelper.writeMessage("Try it again! The username is already exist");
                    continue;
                }
                connectionMap.put(userName.getData(), connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED,
                        "It's ok. The name was getting"));
                return userName.getData();
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            connectionMap.forEach((keyString,valueConnection) -> {
                if (!keyString.equals(userName)) {
                    try {
                        connection.send(new Message(MessageType.USER_ADDED, keyString));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": "
                            + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("It's an error in message type! " +
                            connection.getRemoteSocketAddress());
                }
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("It's a new connection with remote address: " +
                    socket.getRemoteSocketAddress().toString());
            String userName = null;

            try (Connection connection = new Connection(socket)) {
                userName = this.serverHandshake(connection);
                Server.sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                this.notifyUsers(connection, userName);
                this.serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException exception) {
                ConsoleHelper.writeMessage("Error in exchange data with remote address");
            }

            if (userName != null)
                connectionMap.remove(userName);

            Server.sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));

            ConsoleHelper.writeMessage("Connection with remote address was closed");

        }
    }
}
