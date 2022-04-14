package com.greenfoxacademy.islandfoxtribes.services.message;

import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageListDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.SendMessageDto;


public interface MessageService {

    MessageListDto getAllMessagesDto(Long kingdomId);

    MessageDto getMessageDtoById(Long kingdomId, Long messageId);

    void saveMessageFromDto(SendMessageDto sendMessageDto, Long kingdomId);

}

