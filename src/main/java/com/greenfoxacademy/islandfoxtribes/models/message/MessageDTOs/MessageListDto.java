package com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs;


import lombok.Data;

import java.util.List;

@Data
public class MessageListDto {
    private List<MessageDto> messages;
}
