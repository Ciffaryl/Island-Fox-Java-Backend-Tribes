package com.greenfoxacademy.islandfoxtribes.services.message;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageListDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.SendMessageDto;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.message.MessageRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final KingdomRepository kingdomRepository;
    private final MessageRepository messageRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public MessageServiceImpl(KingdomRepository kingdomRepository, MessageRepository messageRepository,
                              PlayerRepository playerRepository) {
        this.kingdomRepository = kingdomRepository;
        this.messageRepository = messageRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public MessageListDto getAllMessagesDto(Long kingdomId) {
        Kingdom kingdom = kingdomRepository.getById(kingdomId);
        Player player = kingdom.getPlayer();

        List<MessageDto> listOfMessagesDto = new ArrayList<>();

        for (Message message : player.getMessageList()) {
            listOfMessagesDto.add(new MessageDto(message));
        }

        MessageListDto messageListDto = new MessageListDto();
        messageListDto.setMessages(listOfMessagesDto);

        return messageListDto;
    }

    @Override
    public MessageDto getMessageDtoById(Long kingdomId, Long messageId) {

        Kingdom kingdom = kingdomRepository.getById(kingdomId);
        Player player = kingdom.getPlayer();
        Message message = messageRepository.findByPlayerAndId(player, messageId);

        return new MessageDto(message);
    }

    @Override
    public void saveMessageFromDto(SendMessageDto sendMessageDto, Long kingdomId) {

        Player sender = kingdomRepository.getById(kingdomId).getPlayer();
        Player receiver = playerRepository.findPlayerByUserName(sendMessageDto.getReceiverName());
        Message message = new ModelMapper().map(sendMessageDto, Message.class);
        message.setSender(sender.getUserName());
        message.setPlayer(receiver);

        messageRepository.save(message);
    }
}
