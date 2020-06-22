package de.uniba.rz.app;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import de.uniba.rz.app.RabbitmqConfiguration.Receiver;
import de.uniba.rz.app.RabbitmqConfiguration.RabbitMqConfiguration;

import de.uniba.rz.ui.swing.MainFrame;
import de.uniba.rz.ui.swing.SwingMainController;
import de.uniba.rz.ui.swing.SwingMainModel;

/**
 * Main class to start the TicketManagement5000 client application Currently
 * only a local backend implementation is registered.<br>
 * <<<<<<< Updated upstream
 * <p>
 * =======
 * <p>
 * >>>>>>> Stashed changes
 * To add additional implementations modify the method
 * <code>evaluateArgs(String[] args)</code>
 *
 * @see #evaluateArgs(String[])
 */
public class Main {

    /**
     * Starts the TicketManagement5000 application based on the given arguments
     *
     * <p>
     * <b>TODO No changes needed here - but documentation of allowed args should
     * be updated</b>
     * </p>
     *
     * @param args
     */
    public static void main(String[] args) throws IOException, TimeoutException {
        TicketManagementBackend backendToUse = evaluateArgs(args);
        String argu = args[0];
        SwingMainController control = new SwingMainController(backendToUse, argu);
        SwingMainModel model = new SwingMainModel(backendToUse);
        MainFrame mf = new MainFrame(control, model);
        control.setMainFrame(mf);
        control.setSwingMainModel(model);
        control.start();

        if (args[0] == "rmq") {
            RabbitMqConfiguration.createQueue();
            Receiver.ReceiverMethod(null);
        }


    }

    /**
     * Determines which {@link TicketManagementBackend} should be used by
     * evaluating the given {@code args}.
     * <p>
     * If <code>null</code>, an empty array or an unknown argument String is
     * passed, the default {@code LocalTicketManagementBackend} is used.
     *
     * <p>
     * <b>This method must be modified in order to register new implementations
     * of {@code TicketManagementBackend}.</b>
     * </p>
     *
     * @param args a String array to be evaluated
     * @return the selected {@link TicketManagementBackend} implementation
     * @see TicketManagementBackend
     */
    private static TicketManagementBackend evaluateArgs(String[] args) throws IOException, TimeoutException {
        if (args == null || args.length == 0) {
            System.out.println("No arguments passed. Using local backend implemenation.");
            return new LocalTicketManagementBackend();
        } else {
            switch (args[0]) {
                case "local":
                    return new LocalTicketManagementBackend();
                // TODO Register new backend implementations here as additional cases.
                //Added by Muhammad Sultan
                case "udp":
                    String host = args[1];
                    int port = Integer.parseInt(args[2]);
                    return new UdpTicketManagementBackend(host, port);
//                case "rmq":
//                    return new RabbitMQTicketManagementBackend();

                default:
                    System.out.println("Unknown backend type. Using local backend implementation.");
                    return new LocalTicketManagementBackend();

            }
        }
    }
}


