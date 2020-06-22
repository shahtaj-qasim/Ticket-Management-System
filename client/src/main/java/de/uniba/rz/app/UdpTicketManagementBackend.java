package de.uniba.rz.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.uniba.rz.entities.PacketWrapper;
import de.uniba.rz.entities.Priority;
import de.uniba.rz.entities.Ticket;
import de.uniba.rz.entities.TicketException;
import de.uniba.rz.entities.Type;
import de.uniba.rz.entities.Util;

public class UdpTicketManagementBackend implements TicketManagementBackend {

    String host;
    int port;
    DatagramSocket serverSocket;
    AtomicInteger ticketId;
    byte[] receivedBuff, sendBuff;

    public UdpTicketManagementBackend(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            serverSocket = new DatagramSocket();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ticketId = new AtomicInteger(1);
    }

    @Override
    public void triggerShutdown() {
        // TODO Auto-generated method stub
        serverSocket.close();
    }
    //Added by Muhammad
    // Create new tickets
    @Override
    public Ticket createNewTicket(String reporter, String topic, String description, Type type, Priority priority)
            throws TicketException {

        Ticket tempTicket = new Ticket(ticketId.getAndIncrement(), reporter, topic, description, type, priority);
        PacketWrapper packetWrapper = new PacketWrapper("createNewTicket", tempTicket);
        try {
            sendPacket(packetWrapper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (Ticket) tempTicket.clone();
    }
    // Author: Shavez Hameed
    //Fetches all tickets
    @Override
    public List<Ticket> getAllTickets() throws TicketException {
        // TODO Auto-generated method stub
        PacketWrapper wrapper = new PacketWrapper("getAllTickets", null);
        List<Ticket> allTickets = new ArrayList<>();
        try {
            sendPacket(wrapper);
            List<Ticket> received = (List<Ticket>) receivePacket();
            assert received != null;
            System.out.println(received.size());
            return received;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allTickets;
    }
    // Author: Shavez Hameed
    // Fetch ticket by ID
    /*param : id*/
    @Override
    public Ticket getTicketById(int id) throws TicketException {
        // TODO Auto-generated method stub
        PacketWrapper wrapper = new PacketWrapper("getTicketById", new Integer(id));
        Ticket ticket = null;
        try {
            // Sending Packet to server
            sendPacket(wrapper);
            // Received Ticket from server
            ticket = (Ticket) receivePacket();
            return ticket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //Added by Deepika
    @Override
    public Ticket acceptTicket(int id) throws TicketException {
        // TODO Auto-generated method stub
        PacketWrapper wrapper = new PacketWrapper("acceptTicket", id);
        Ticket ticket = null;
        try {
            // Sending packet to server
            sendPacket(wrapper);
            // receiving back from server
            ticket = (Ticket) receivePacket();
            return ticket;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    //Added by Deepika
    @Override
    public Ticket rejectTicket(int id) throws TicketException {
        // TODO Auto-generated method stub
        PacketWrapper wrapper = new PacketWrapper("rejectTicket", id);
        Ticket ticket = null;
        try {
            // Send Packet
            sendPacket(wrapper);
            // Receive Packet
            ticket = (Ticket) receivePacket();
            return ticket;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    //Added by Deepika
    @Override
    public Ticket closeTicket(int id) throws TicketException {
        // TODO Auto-generated method stub
        PacketWrapper wrapper = new PacketWrapper("closeTicket", id);
        Ticket ticket = null;
        try {
            // Send Packet
            sendPacket(wrapper);
            // Receive Packet
            ticket = (Ticket) receivePacket();
            return ticket;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Object receivePacket() {
        // TODO Auto-generated method stub
        try {
            receivedBuff = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receivedBuff, receivedBuff.length);
            serverSocket.receive(receivePacket);
            return Util.byteStreamToObject(receivePacket.getData());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void sendPacket(PacketWrapper wrapper) {
        // TODO Auto-generated method stub
        try {
            sendBuff = Util.objectToStream(wrapper);
            DatagramPacket packet = new DatagramPacket(sendBuff, sendBuff.length, InetAddress.getByName(host), port);
            serverSocket.send(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
