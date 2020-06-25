package client.io;

import client.Client;
import network.message.Message;
import network.message.MessageReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class ClientMessageReceiver implements MessageReceiver {
    private final SocketChannel channel;

    public ClientMessageReceiver(SocketChannel channel) {
        this.channel = channel;
    }

    private byte[] readAllBytes() throws IOException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            ByteBuffer messageBuf = ByteBuffer.allocate(1024 * 32);

            while (channel.read(messageBuf) == 0);

            do {
                messageBuf.flip();

                while (messageBuf.hasRemaining()) {
                    byteOut.write(messageBuf.get());
                }
                messageBuf.clear();

            } while (channel.read(messageBuf) > 0);

            return byteOut.toByteArray();
        }
    }

    @Override
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return deserializeMessage(readAllBytes());
    }

    public List<Message> receiveMessages() throws IOException {
        return deserializeMessages(readAllBytes());
    }

    private List<Message> deserializeMessages(byte[] bytes) {
        List<Message> messages = new LinkedList<>();
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            while (true) {
                messages.add((Message) objIn.readObject());
            }
        } catch (Exception e) {
            Client.log.warning(e::toString);
            return messages;
        }
    }
}
