package de.uniba.rz.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

public class TicketServerMain {
    static TicketStore simpleTestStore;

    public static void main(String[] args) throws IOException, NamingException {
        switch (args[2]) {

            case "udp":
                simpleTestStore = new UdpTicketStore();
            case "rmq":
                simpleTestStore = new RabbitMQTicketStore();
        }

        List<RemoteAccess> remoteAccessImplementations = getAvailableRemoteAccessImplementations(args);

        // Starting remote access implementations:
        for (RemoteAccess implementation : remoteAccessImplementations) {
            implementation.prepareStartup(simpleTestStore);
            new Thread(implementation).start();
        }

        try (BufferedReader shutdownReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Press enter to shutdown the server.");
            shutdownReader.readLine();
            System.out.println("Shutting down . . .");

            // Shutting down all remote access implementations
            for (RemoteAccess implementation : remoteAccessImplementations) {
                implementation.shutdown();
            }
            System.out.println("Shut down successful. Bye!");
        }
    }

    private static List<RemoteAccess> getAvailableRemoteAccessImplementations(String[] args) {
        List<RemoteAccess> implementations = new ArrayList<>();

        // TODO Add your implementations of the RemoteAccess interface
        // Added by Muhammad

        switch (args[2]) {

            case "udp":
                implementations.add(new UdpRemoteAccess(args[0], Integer.parseInt(args[1])));
                return implementations;
            case "rmq":
                implementations.add(new RabbitMQRemoteAccess());
                return implementations;
            default:
                System.out.println("No backend argument is specified!");
                return null;
        }
        //return implementations;
    }
}
