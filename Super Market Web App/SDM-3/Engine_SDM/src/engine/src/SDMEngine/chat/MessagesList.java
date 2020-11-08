package engine.src.SDMEngine.chat;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MessagesList {

    private final List<Message> messagesList;
    private int indexOfNextMessageToGet;

    public MessagesList() {
        messagesList = new LinkedList();
        indexOfNextMessageToGet = 0;
    }

    public void addNewMessage(String message, String writerName) {
        if(message != null && writerName != null && !writerName.equals("")) {
            messagesList.add(new Message(message, writerName));
        }
    }

    public Message getNextMessage() {
        if(indexOfNextMessageToGet < messagesList.size()) {
            Message messageToReturn = messagesList.get(indexOfNextMessageToGet);
            indexOfNextMessageToGet++;
            return messageToReturn;
        }
        return null;
    }

    public List<Message> getAllNewMessages() {
        List<Message> messageListToReturn = new LinkedList<>();

        if(indexOfNextMessageToGet < messagesList.size()) {
            while (indexOfNextMessageToGet < messagesList.size()) {
                messageListToReturn.add(messagesList.get(indexOfNextMessageToGet));
                indexOfNextMessageToGet++;
            }
            return messageListToReturn;
        }
        return null;
    }

    public List<Message> getMessagesList() { return messagesList; }

    public int getIndexOfNextMessageToGet() { return indexOfNextMessageToGet; }

    public void setIndexOfNextMessageToGet(int indexOfNextMessageToGet) { this.indexOfNextMessageToGet = indexOfNextMessageToGet; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagesList that = (MessagesList) o;
        return indexOfNextMessageToGet == that.indexOfNextMessageToGet &&
                Objects.equals(messagesList, that.messagesList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messagesList, indexOfNextMessageToGet);
    }

    @Override
    public String toString() {
        return "MessagesList{" +
                "messagesList=" + messagesList +
                ", indexOfNextMessageToGet=" + indexOfNextMessageToGet +
                '}';
    }
}
