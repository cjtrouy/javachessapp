import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Vector;

class Client {
    private InetAddress servAddress;
    private InetAddress publicAddress;
    private InetAddress privateAddress;
    private String clientIpString;
    private DatagramSocket servSocket;
    private String privateServIPString;
    private String publicServIpString;
    private int publicServPortNum;
    private int privateServPortNum;
    private int servPortNum;
    private int globalTimeout;
    private int packetNum;
    private int packetSize;
    private boolean sendingPacket;
    private Menu menuPointer;
    private Thread clientThread;
    private boolean endConnection = false;
    private Vector<String> onlineUsers;

    // Packets
    DatagramPacket sendPacket;
    DatagramPacket receivePacket;
    byte[] sendBuffer;
    byte[] receiveBuffer;

    // received packet info
    private int opcode;
    private int newPacketNum;
    private int response;
    private boolean acceptedInvite;

    // Client info
    private String clientUserName, clientPassword;
    private String newUserName, newPassword;
    private String opponentUsername;
    private String currMove;
    private int promotionPieceNum;

    Client(Menu menu) {
        privateServIPString = "172.18.222.161";
        privateServPortNum = 6500;
        publicServIpString = "24.237.168.152";
        publicServPortNum = 5500;

        globalTimeout = 800;
        packetNum = 1;
        menuPointer = menu;
        packetSize = 512;
        receiveBuffer = new byte[packetSize];

        try {
            servSocket = new DatagramSocket();
            publicAddress = InetAddress.getByName(publicServIpString);
            privateAddress = InetAddress.getByName(privateServIPString);
            servSocket.setSoTimeout(globalTimeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getClientIp();
    }

    // Returns the IP of the client machine
    /*
     * IP needed to discern if client is on the same network of the server.
     * If on same network, use private IP instead of public.
     */
    private void getClientIp() {
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            clientIpString = reader.readLine();

            if (clientIpString.equals(publicServIpString)) {
                servAddress = privateAddress;
                servPortNum = privateServPortNum;
            } else {
                servAddress = publicAddress;
                servPortNum = publicServPortNum;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startConnection() {
        clientThread = new Thread(serverConnection());
        clientThread.start();
    }

    public DatagramPacket createPacket(int opcode) {
        byte[] buffer = new byte[packetSize];
        DatagramPacket packet;
        ByteBuffer intBuffer = ByteBuffer.allocate(4);
        intBuffer.putInt(packetNum);
        intBuffer.rewind();
        intBuffer.get(buffer, 1, 4);

        switch (opcode) {
            // Creates inital connection packet consisting of opcode 1 and the current
            // packet CNT
            case 1:
                buffer[0] = 1;
                break;
            case 3:
                buffer[0] = 3;
                break;
            case 4:
                buffer[0] = 4;
                buffer = addUserInfo(buffer, clientUserName, clientPassword);
                break;
            case 7:
                buffer[0] = 7;
                buffer = addUserInfo(buffer, newUserName, newPassword);
                break;
            // view online users packet
            case 8:
                buffer[0] = 8;
                break;
            // Termination packet
            case 9:
                buffer[0] = 9;
                break;
            case 10:
                buffer[0] = 10;
                buffer = addOpponentName(buffer, opponentUsername);
                break;
            case 12:
                buffer[0] = 12;
                if (acceptedInvite)
                    buffer[5] = 1;
                else
                    buffer[5] = 0;
                break;
            // Move packets
            case 14:
                buffer[0] = 14;
                buffer = addMove(buffer, currMove);
                break;

            // Castling packet
            case 15:
                buffer[0] = 15;
                buffer = addMove(buffer, currMove);
                break;

            // Pawn promotion Packet
            case 16:
                buffer[0] = 16;
                buffer = addPawnPromo(buffer, currMove);
                break;

            // Default case, should never enter
            default:
                menuPointer.flagError(1);
                break;
        }

        packet = new DatagramPacket(buffer, buffer.length, servAddress, servPortNum);
        return packet;
    }

    private String getMove(byte[] buffer, boolean castling, boolean promotion) {
        int offset = 5;
        byte[] moveByte;
        if (castling) {
            moveByte = new byte[8];
            for (int i = 0; i < 8; i++) {
                moveByte[i] = buffer[i + offset];
            }
        } else if (promotion) {
            moveByte = new byte[4];
            offset = 6;
            for (int i = 0; i < 4; i++) {
                moveByte[i] = buffer[i + offset];
            }
        } else {
            moveByte = new byte[4];
            for (int i = 0; i < 4; i++) {
                moveByte[i] = buffer[i + offset];
            }
        }
        return new String(moveByte);
    }

    private void unpackPacket(DatagramPacket packet) {
        byte[] buffer = packet.getData();
        byte[] bytePacketNum = new byte[4];

        opcode = buffer[0];
        for (int i = 1; i < 5; i++) {
            bytePacketNum[i - 1] = buffer[i];
        }
        newPacketNum = ByteBuffer.wrap(bytePacketNum).getInt();
    }

    private byte[] addMove(byte[] buffer, String move) {
        byte[] moveBuffer = move.getBytes();
        int offset = 5;
        for (int i = 0; i < moveBuffer.length; i++) {
            buffer[i + offset] = moveBuffer[i];
        }

        return buffer;
    }

    private byte[] addPawnPromo(byte[] buffer, String move) {
        byte[] moveByte = move.getBytes();
        int offset = 5;
        buffer[offset] = (byte) promotionPieceNum;
        offset++;

        for (int i = 0; i < moveByte.length; i++) {
            buffer[i + offset] = moveByte[i];
        }
        return buffer;
    }

    public String getOpponent(DatagramPacket packet) {
        byte[] buffer = packet.getData();
        byte[] oppNameByte;
        int offset = 5;
        int length = 0;

        for (int i = 0; buffer[i + offset] != 0; i++) {
            length++;
        }
        oppNameByte = new byte[length];
        for (int i = 0; i < length; i++) {
            oppNameByte[i] = buffer[i + offset];
        }

        return new String(oppNameByte);
    }

    public Runnable serverConnection() {
        return new Runnable() {
            // Thread where the client->server connections take place.
            public void run() {
                sendPacket = createPacket(1);
                String move;
                try {
                    servSocket.send(sendPacket);
                    receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    sendingPacket = false;

                    while (true) {
                        try {
                            servSocket.receive(receivePacket);
                        } catch (SocketTimeoutException e) {
                            if (sendingPacket) {
                                servSocket.send(sendPacket);
                            }
                            continue;
                        }
                        // Received new packet
                        unpackPacket(receivePacket);
                        // Received a packet we should have already responded to:
                        // For now, just continue, later, send appropiate response (might throw into
                        // infinite loop)
                        System.out.println("My Total: " + packetNum + "\nNew Total (From Server): " + newPacketNum);
                        if (newPacketNum <= packetNum) {
                            System.out.println("Repeat packet number. Ignoring. Opcode: " + opcode);
                            continue;
                        }
                        packetNum = newPacketNum;
                        // Analyze opcodes
                        switch (opcode) {
                            // ACK - send reponse-to-ack
                            case 2:
                                // If first packet, change port num
                                if (packetNum == 2)
                                    servPortNum = receivePacket.getPort();

                                packetNum++;
                                sendPacket = createPacket(3);
                                sendingPacket = false;
                                servSocket.send(sendPacket);
                                System.out.println("Sending packet with: " + packetNum);
                                break;
                            // Reponse-To-Ack - DO NOTHING
                            case 3:
                                sendingPacket = false;
                                break;

                            case 5:
                                response = receiveBuffer[5];
                                if (response == 0)
                                    menuPointer.flagError(2);
                                else {
                                    // user login was successful. Proceed to user's page
                                    menuPointer.createLoginMenu(clientUserName);
                                }
                                packetNum++;
                                sendPacket = createPacket(3);
                                sendingPacket = false;
                                servSocket.send(sendPacket);
                                break;

                            // new user acknowledgement
                            case 7:
                                int success = receiveBuffer[5];
                                if (success == 0)
                                    menuPointer.flagError(4);
                                else
                                    menuPointer.createSuccessMessage(0);
                                packetNum++;
                                sendPacket = createPacket(3);
                                sendingPacket = false;
                                servSocket.send(sendPacket);
                                break;

                            // user information
                            case 8:
                                // Send an acknowledment
                                packetNum++;
                                sendPacket = createPacket(3);
                                sendingPacket = false;
                                servSocket.send(sendPacket);

                                // Retrieve the user data
                                if (receiveBuffer[5] == 0) {
                                    menuPointer.flagError(5);
                                } else {
                                    onlineUsers = getUsers(receivePacket);
                                    menuPointer.displayUsers(onlineUsers);
                                }
                                break;
                            // Termination packet
                            case 9:
                                menuPointer.createSuccessMessage(1);
                                clientThread.interrupt();
                                endConnection = true;
                                break;
                            // Received an invitation
                            case 11:
                                packetNum++;
                                sendPacket = createPacket(3);
                                servSocket.send(sendPacket);
                                sendingPacket = false;

                                String opponentName = getOpponent(receivePacket);
                                menuPointer.invitationMessage(opponentName);
                                break;

                            // User accepted invitation, start game
                            case 13:
                                packetNum++;
                                sendPacket = createPacket(3);
                                servSocket.send(sendPacket);
                                sendingPacket = false;

                                menuPointer.startGame(receiveBuffer[5]);
                                break;
                            // Movement packet
                            case 14:
                                packetNum++;
                                sendPacket = createPacket(3);
                                servSocket.send(sendPacket);
                                sendingPacket = false;

                                // Unpack the move and realize it on the client side
                                move = getMove(receiveBuffer, false, false);
                                menuPointer.board.opponentMove(move, false, false);
                                break;

                            // Castling packet
                            case 15:
                                packetNum++;
                                sendPacket = createPacket(3);
                                servSocket.send(sendPacket);
                                sendingPacket = false;

                                move = getMove(receiveBuffer, true, false);
                                menuPointer.board.opponentMove(move, true, false);
                                break;

                            // Pawn promotion packet
                            case 16:
                                packetNum++;
                                sendPacket = createPacket(3);
                                servSocket.send(sendPacket);
                                sendingPacket = false;

                                // Act on the move
                                move = getMove(receiveBuffer, false, true);
                                int oppPromoPieceNum = receiveBuffer[5];
                                menuPointer.board.opponentMove(oppPromoPieceNum + "" + move, false, true);

                                break;

                        }
                        if (endConnection)
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                servSocket.close();
            }
        };
    }

    public void sendUserInfo(String userName, String password) {
        clientUserName = userName;
        clientPassword = password;
        packetNum++;
        sendPacket = createPacket(4);
        sendingPacket = true;
    }

    public void sendNewUser(String userName, String password) {
        newUserName = userName;
        newPassword = password;
        packetNum++;
        sendPacket = createPacket(7);
        sendingPacket = true;
    }

    private byte[] addUserInfo(byte[] thisBuffer, String userName, String password) {
        byte[] userNameBuffer = userName.getBytes();
        byte[] userPasswordBuffer = password.getBytes();
        int offset = 5;
        int i;
        // add to the sending buffer
        for (i = 0; i < userNameBuffer.length; i++) {
            thisBuffer[offset + i] = userNameBuffer[i];
        }
        offset += i;
        thisBuffer[offset] = 0;
        offset++;
        for (i = 0; i < userPasswordBuffer.length; i++) {
            thisBuffer[offset + i] = userPasswordBuffer[i];
        }
        thisBuffer[offset + i] = 0;
        return thisBuffer;
    }

    public void viewUsers() {
        packetNum++;
        sendPacket = createPacket(8);
        sendingPacket = true;
    }

    private Vector<String> getUsers(DatagramPacket packet) {
        Vector<String> users = new Vector<String>();
        byte[] buffer = packet.getData();
        int userCNT = buffer[5];
        // Offset is where the data starts
        int offset = 6;
        int k;
        // Iterate over the amount of users
        for (int i = 0; i < userCNT; i++) {
            byte[] username = new byte[512];
            // Iterate over the bytes
            for (k = 0; buffer[k + offset] != 0; k++) {
                username[k] = buffer[k + offset];
            }
            byte[] usernameProperLength = new byte[k];
            for (int j = 0; j < k; j++) {
                usernameProperLength[j] = username[j];
            }
            offset += k + 1;
            // Add the username to our vector:
            users.add(new String(usernameProperLength));
        }
        users.remove(clientUserName);
        return users;
    }

    public void inviteUser(String username) {
        opponentUsername = username;
        packetNum++;
        sendPacket = createPacket(10);
        sendingPacket = true;
    }

    private byte[] addOpponentName(byte[] buffer, String oppName) {
        byte[] oppNameByte = oppName.getBytes();
        int offset = 5;

        for (int i = 0; i < oppNameByte.length; i++) {
            buffer[offset + i] = oppNameByte[i];
        }
        // By default, buffer should be null terminated.

        return buffer;
    }

    public void logout() {
        packetNum++;
        sendPacket = createPacket(9);
        sendingPacket = true;
    }

    public void sendInviteResponse(boolean accept) {
        packetNum++;
        acceptedInvite = accept;
        sendPacket = createPacket(12);
        sendingPacket = true;
    }

    public void sendMove(String move) {
        packetNum++;
        currMove = move;
        sendPacket = createPacket(14);
        sendingPacket = true;
    }

    // Send move method used when castling
    public void sendMove(String kingMove, String rookMove) {
        packetNum++;
        currMove = kingMove + rookMove;
        sendPacket = createPacket(15);
        sendingPacket = true;
    }

    // Sends the pawn promotion to the opponent
    public void sendPawnPromo(String move, int pieceNum) {
        // opcode 16
        currMove = move;
        promotionPieceNum = pieceNum;
        packetNum++;
        sendPacket = createPacket(16);
        sendingPacket = true;
    }

}