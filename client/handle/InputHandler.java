package client.handle;

import client.Client;
import client.gui.App;
import client.io.ClientMessageReceiver;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.EOFException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputHandler extends RecursiveAction {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    AtomicBoolean isShowing = new AtomicBoolean(false);

    @Override
    protected void compute() {
        while (true) {
            try {
                while (Client.channel == null || !Client.channel.isOpen()
                        || Client.selector == null || !Client.selector.isOpen()) ;

                while (Client.selector.select(100000) == 0) ;

                Iterator<SelectionKey> it = Client.selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isReadable()) {

                        Client.processor.handle(new ClientMessageReceiver(Client.channel).receiveMessage());
                        Client.log.info("message read");
                    }
                }

            } catch (EOFException ex) {
                if (!isShowing.get())
                    Platform.runLater(() -> {
                        isShowing.set(true);

                        if (alert.isShowing()) return;

                        alert.setContentText("There is no connection to the server");
                        alert.getButtonTypes().clear();
                        ButtonType wait = new ButtonType("Wait for the connection");
                        ButtonType disconnect = new ButtonType("Disconnect");
                        alert.getButtonTypes().addAll(wait, disconnect);

                        ButtonType pressed = alert.showAndWait().orElse(null);

                        if (pressed == null || pressed == disconnect) {
                            App.quit();
                        } else {
                            Client.establishConnection();
                        }
                        isShowing.set(false);
                    });
            } catch (Exception e) {
                Client.log.warning(e.toString());
            }
        }
    }
}