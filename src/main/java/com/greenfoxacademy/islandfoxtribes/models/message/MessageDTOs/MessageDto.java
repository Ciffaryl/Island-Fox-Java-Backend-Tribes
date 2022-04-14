package com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs;

import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import lombok.Data;

@Data
public class MessageDto {

    private String receiver;

    private String sender;

    private String object;

    private String text;

    private String sentAt;

    public MessageDto(Message message) {
        this.receiver = message.getPlayer().getUserName();
        this.sender = message.getSender();
        this.object = message.getObject();
        this.text = message.getText();
        this.sentAt = message.getSentAt();
    }
}
