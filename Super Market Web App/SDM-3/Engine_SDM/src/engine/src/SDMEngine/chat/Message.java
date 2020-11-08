package engine.src.SDMEngine.chat;

import java.util.Objects;

public class Message {

    private final String message;
    private final String messageWriter;

    public Message(String message, String messageWriter) {
        this.message = message;
        this.messageWriter = messageWriter;
    }

    public String getMessage() { return message; }

    public String getMessageWriter() { return messageWriter; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) &&
                Objects.equals(messageWriter, message1.messageWriter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, messageWriter);
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", messageWriter='" + messageWriter + '\'' +
                '}';
    }
}
