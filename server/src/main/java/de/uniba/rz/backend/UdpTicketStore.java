package de.uniba.rz.backend;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.HashMap;
import de.uniba.rz.entities.Priority;
import de.uniba.rz.entities.Status;
import de.uniba.rz.entities.Ticket;
import de.uniba.rz.entities.Type;

public class UdpTicketStore implements TicketStore {

	HashMap<Integer, Ticket> tickets = new HashMap<>();
	private AtomicInteger ticketId;
	
	public UdpTicketStore() {
		ticketId = new AtomicInteger(1);
	}

	//Added by Muhammad

	@Override
	public Ticket storeNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
		// TODO Auto-generated method stub
		Ticket newTicket = new Ticket(ticketId.getAndIncrement(), reporter, topic, description, type, priority);
		tickets.put(newTicket.getId(), newTicket);
		//System.out.println("Created new Ticket from Reporter: " + reporter + " with the topic \"" + topic + "\"");
		System.out.println(newTicket.toString());
		return (Ticket) newTicket.clone();
	}

	//Added by Deepika

	@Override
	public void updateTicketStatus(int ticketId, Status newStatus)
			throws UnknownTicketException, IllegalStateException {
		// TODO Auto-generated method stub
		tickets.forEach((key, value) -> {
			if (key == ticketId) {
				value.setStatus(newStatus);
			}
		});
	}

	//Added By Shavez

	@Override
	public List<Ticket> getAllTickets() {
		// TODO Auto-generated method stub
		List<Ticket> noOfTickets = new ArrayList<>();
		tickets.forEach((key, value) -> noOfTickets.add(value));
		return noOfTickets;
	}

}
