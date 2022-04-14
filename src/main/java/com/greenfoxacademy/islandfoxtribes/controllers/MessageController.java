package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.MessageListDto;
import com.greenfoxacademy.islandfoxtribes.models.message.MessageDTOs.SendMessageDto;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.services.message.MessageService;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerService;
import com.greenfoxacademy.islandfoxtribes.utilities.Utilities;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Import(SecurityConfig.class)

@RestController
public class MessageController {

    private final PlayerService playerService;
    private final MessageService messageService;
    private final PlayerRepository playerRepository;

    @Autowired
    public MessageController(PlayerService playerService, MessageService messageService,
                             PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.messageService = messageService;
        this.playerRepository = playerRepository;

    }

    @PostMapping("/kingdom/{id}/message")
    public ResponseEntity sendMessage(@PathVariable Long id, @RequestBody SendMessageDto sendMessageDto) {

        if (playerService.validation(id)) {
            if (Utilities.isStringEmpty(sendMessageDto.getObject())) {
                return Utilities.createBadRequestResponse("The object must be filled.");
            } else if (Utilities.isStringEmpty(sendMessageDto.getText())) {
                return Utilities.createBadRequestResponse("Empty message cannot be sent.");
            } else if (Utilities.isStringEmpty(sendMessageDto.getReceiverName())) {
                return Utilities.createBadRequestResponse("The receiver of the message must be filled in.");
            } else if (!playerRepository.existsPlayerByUserName(sendMessageDto.getReceiverName())) {
                return Utilities.createBadRequestResponse("The receiver of the message does not exist.");
            }
            messageService.saveMessageFromDto(sendMessageDto, id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Your message has been sent successfully.");
        }
        return Utilities.createErrorResponse(HttpStatus.UNAUTHORIZED,
                "You have to log in to your account to send a message.");
    }

    @GetMapping("/kingdom/{id}/messages")
    public ResponseEntity showAllMessages(@PathVariable Long id) {

        if (playerService.validation(id)) {
            MessageListDto allMessages = messageService.getAllMessagesDto(id);
            return ResponseEntity.status(HttpStatus.OK).body(allMessages);
        }
        return Utilities.createErrorResponse(HttpStatus.UNAUTHORIZED,
                "You don't have permission to view all messages.");
    }

    @GetMapping("/kingdom/{kingdomId}/message/{messageId}")
    public ResponseEntity showMessage(@PathVariable Long kingdomId, @PathVariable Long messageId) {

        if (playerService.validation(kingdomId)) {
            MessageDto messageDto = messageService.getMessageDtoById(kingdomId, messageId);
            return ResponseEntity.status(HttpStatus.OK).body(messageDto);
        }
        return Utilities.createErrorResponse(HttpStatus.UNAUTHORIZED,
                "You don't have permission to view this message.");
    }
}



