package de.uniba.rz.ui.swing;

import java.util.List;

import de.uniba.rz.app.Shutdown;
import de.uniba.rz.app.RabbitmqConfiguration.Sender;

import de.uniba.rz.entities.*;

public class SwingMainController {

    private SwingMainModel model;
    private MainFrame mainFrame;
    private Shutdown connector;
    private String argumentRMQ;

    public SwingMainController(Shutdown shutdownConnector, String argu) {
        this.connector = shutdownConnector;
        this.argumentRMQ = argu;
        System.out.println(argumentRMQ);
    }

    public SwingMainController(SwingMainModel model, MainFrame mf) {
        this.model = model;
        this.mainFrame = mf;

    }

    public void setSwingMainModel(SwingMainModel model) {
        this.model = model;
    }

    public void setMainFrame(MainFrame mf) {
        this.mainFrame = mf;
    }

    public void start() {
        if (mainFrame != null && model != null) {
            mainFrame.showUI();
        }
    }

    public void getAndShowTicketById(int id) {
        try {
            mainFrame.showTicketDetails(model.getTicket(id));
        } catch (TicketException e) {
            mainFrame.clearTicketDetails();
            mainFrame.showErrorDialog("Ticket with Id " + id + "does not exist!", e);

        }
    }

    public void acceptTicket(int id) {
        try {
            model.acceptTicket(id);

            Ticket ticket = model.getTicket(id);
            mainFrame.showTicketDetails(ticket);
            if (argumentRMQ.contains("rmq")) {
                Sender.SenderMethod(ticket, "update");
            }
        } catch (TicketException exec) {
            mainFrame.showErrorDialog("Invalid status change.", exec);
        }
    }

    public void closeTicket(int id) {
        try {
            model.closeTicket(id);
            Ticket ticket = model.getTicket(id);
            mainFrame.showTicketDetails(ticket);
            if (argumentRMQ.contains("rmq")) {
                Sender.SenderMethod(ticket, "update");
            }
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Invalid status change.", e);
        }
    }

    public void rejectTicket(int id) {
        try {
            model.rejectTicket(id);
            Ticket ticket = model.getTicket(id);
            mainFrame.showTicketDetails(ticket);
            if (argumentRMQ.contains("rmq")) {
                Sender.SenderMethod(ticket, "update");
            }
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Invalid status change", e);
        }
    }

    public void createNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
        try {
            Ticket ticket = model.createNewTicket(reporter, topic, description, type, priority);
            mainFrame.showTicketDetails(ticket);

            if (argumentRMQ.contains("rmq")) {
                Sender.SenderMethod(ticket, "create");
            }
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Invalid status change", e);
        }
    }

    public void searchTicket(String name, Type type) {
        try {
            List<Ticket> tickets = model.searchTicket(name, type);
            mainFrame.updateTable(tickets);
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Could not perform ticket search", e);
        }
    }

    public void refreshTicketList() {
        try {
            mainFrame.updateTable(model.getAllTickets());
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Error refreshing list of tickets", e);
        }
    }

    public void triggerApplicationShutdown() {
        connector.triggerShutdown();
        mainFrame.dispose();
    }
}
