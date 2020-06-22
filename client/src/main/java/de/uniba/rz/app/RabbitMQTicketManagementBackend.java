package de.uniba.rz.app;

import de.uniba.rz.app.RabbitmqConfiguration.RabbitMqConfiguration;
import de.uniba.rz.app.RabbitmqConfiguration.Receiver;
import de.uniba.rz.app.RabbitmqConfiguration.Sender;
import de.uniba.rz.entities.*;
import de.uniba.rz.ui.swing.SwingMainModel;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RabbitMQTicketManagementBackend implements TicketManagementBackend {

    AtomicInteger ticketId;
    HashMap<Integer, Ticket> localTicketStore = new HashMap<>();
    private SwingMainModel model;

    public RabbitMQTicketManagementBackend() throws IOException, TimeoutException {
        ticketId = new AtomicInteger(1);

    }

    @Override
    public Ticket createNewTicket(String reporter, String topic, String description, Type type, Priority priority) throws TicketException {
        Ticket ticket = new Ticket(ticketId.getAndIncrement(), reporter, topic, description, type, priority);
        ticket.setStatus(Status.NEW);
        localTicketStore.put(ticket.getId(), ticket);

        Sender.SenderMethod(ticket, "create");

        return (Ticket) ticket.clone();

    }

    @Override
    public List<Ticket> getAllTickets() throws TicketException {
        return localTicketStore.entrySet().stream().map(entry -> (Ticket) entry.getValue().clone())
                .collect(Collectors.toList());

    }

    @Override
    public Ticket getTicketById(int id) throws TicketException {
        if (!localTicketStore.containsKey(id)) {
            throw new TicketException("Ticket ID is unknown");
        }

        return (Ticket) getTicketByIdInteral(id).clone();


    }

    private Ticket getTicketByIdInteral(int id) throws TicketException {
        if (!localTicketStore.containsKey(id)) {
            throw new TicketException("Ticket ID is unknown");
        }

        return localTicketStore.get(id);
    }

    @Override
    public Ticket acceptTicket(int id) throws TicketException {
        Ticket ticket = getTicketById(id);
        ticket.setStatus(Status.ACCEPTED);
        Sender.SenderMethod(ticket, "update");

        return (Ticket) ticket.clone();

    }

    @Override
    public Ticket rejectTicket(int id) throws TicketException {
        Ticket ticket = getTicketById(id);
        ticket.setStatus(Status.REJECTED);
        Sender.SenderMethod(ticket, "update");

        return (Ticket) ticket.clone();

    }

    @Override
    public Ticket closeTicket(int id) throws TicketException {
        Ticket ticket = getTicketById(id);
        ticket.setStatus(Status.CLOSED);
        Sender.SenderMethod(ticket, "update");
        return (Ticket) ticket.clone();

    }

    @Override
    public void triggerShutdown() {

    }
}
