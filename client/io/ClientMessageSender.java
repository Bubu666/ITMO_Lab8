package client.io;

import client.Client;
import client.gui.App;
import network.command.Command;
import network.message.CommandMessage;
import network.message.Message;
import network.message.MessageSender;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientMessageSender implements MessageSender {
    private final SocketChannel channel;

    public ClientMessageSender(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        if (message == null) return;

        if (message instanceof CommandMessage) {
            App.addHistory(((CommandMessage) message).command.toString());
        }
        channel.write(ByteBuffer.wrap(serializeMessage(message)));
    }
}
