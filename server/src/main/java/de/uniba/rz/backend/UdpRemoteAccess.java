package de.uniba.rz.backend;

import java.io.IOException;
import java.net.*;
import java.util.List;

import de.uniba.rz.entities.*;

public class UdpRemoteAccess implements RemoteAccess {

    String address;
    int port;
    private DatagramSocket serverSocket;
    private TicketStore ticket;
    private byte[] receiveData;
    private byte[] sendData;

    public UdpRemoteAccess(String address, int port) {
        this.address = address;
        this.port = port;
        try {
            serverSocket = new DatagramSocket(port, InetAddress.getByName(address));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!serverSocket.isClosed()) {
            new Thread() {
                public void run() {
                    try {
                        receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        PacketWrapper receivedPacket = (PacketWrapper) Util.byteStreamToObject(receivePacket.getData());

                        // Process the receive packet
                        //Added by Shavez Hameed
                        switch (receivedPacket.getMessageType()) {
                            case "createNewTicket":
                                Ticket receivedNewTicket = (Ticket) receivedPacket.getMessage();
                                Ticket storedTicket = ticket.storeNewTicket(receivedNewTicket.getReporter(),
                                        receivedNewTicket.getTopic(), receivedNewTicket.getDescription(),
                                        receivedNewTicket.getType(), receivedNewTicket.getPriority());
                                break;

                            case "getAllTickets":
                                List<Ticket> allTickets = ticket.getAllTickets();
                                byte[] objToBytes = Util.objectToStream(allTickets);
                                DatagramPacket sendPacket = new DatagramPacket(objToBytes, objToBytes.length,
                                        receivePacket.getAddress(), receivePacket.getPort());
                                serverSocket.send(sendPacket);
                                break;

                            case "getTicketById":

                                int receivedTicketId = (Integer) receivedPacket.getMessage();
                                for (Ticket t : ticket.getAllTickets()) {
                                    if (t.getId() == receivedTicketId) {
                                        sendData = Util.objectToStream(t);
                                        sendPacket = new DatagramPacket(sendData, sendData.length,
                                                receivePacket.getAddress(), receivePacket.getPort());
                                        serverSocket.send(sendPacket);
                                    }
                                }
                                break;
                            //Added by Deepika
                            case "acceptTicket":
                                receivedTicketId = (int) receivedPacket.getMessage();
                                for (Ticket t : ticket.getAllTickets()) {
                                    if (t.getId() == receivedTicketId) {
                                        t.setStatus(Status.ACCEPTED);
                                        sendData = Util.objectToStream(t);
                                        sendPacket = new DatagramPacket(sendData, sendData.length,
                                                receivePacket.getAddress(), receivePacket.getPort());
                                        serverSocket.send(sendPacket);
                                        System.out.println(t.toString());
                                    }
                                }
                                break;
                            //Added by Deepika
                            case "rejectTicket":
                                receivedTicketId = (int) receivedPacket.getMessage();
                                for (Ticket t : ticket.getAllTickets()) {
                                    if (t.getId() == receivedTicketId) {
                                        t.setStatus(Status.REJECTED);
                                        sendData = Util.objectToStream(t);
                                        sendPacket = new DatagramPacket(sendData, sendData.length,
                                                receivePacket.getAddress(), receivePacket.getPort());
                                        serverSocket.send(sendPacket);
                                        System.out.println(t.toString());
                                    }
                                }
                                break;
                            //Added by Deepika
                            case "closeTicket":
                                receivedTicketId = (int) receivedPacket.getMessage();
                                for (Ticket t : ticket.getAllTickets()) {
                                    if (t.getId() == receivedTicketId) {
                                        t.setStatus(Status.CLOSED);
                                        sendData = Util.objectToStream(t);
                                        sendPacket = new DatagramPacket(sendData, sendData.length,
                                                receivePacket.getAddress(), receivePacket.getPort());
                                        serverSocket.send(sendPacket);
                                        System.out.println(t.toString());
                                    }
                                }
                                break;
                        }
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                }
            }.run();
        }
    }

    @Override
    public void prepareStartup(TicketStore ticketStore) {
        // TODO Auto-generated method stub
        this.ticket = ticketStore;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        serverSocket.close();
    }

}
