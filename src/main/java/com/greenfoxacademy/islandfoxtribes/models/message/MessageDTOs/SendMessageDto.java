package com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs;

import lombok.Data;

@Data
public class SendMessageDto {

    private String object;

    private String text;

    private String receiverName;

    public SendMessageDto(String text, String object, String receiverName) {
        this.text = text;
        this.object = object;
        this.receiverName = receiverName;
    }

}
