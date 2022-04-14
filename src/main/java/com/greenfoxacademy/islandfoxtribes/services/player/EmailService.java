package com.greenfoxacademy.islandfoxtribes.services.player;

import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import com.greenfoxacademy.islandfoxtribes.models.player.ConfirmationToken;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.repositories.player.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;
    private final ConfirmationTokenRepository tokenRepo;


    @Autowired
    public EmailService(JavaMailSender javaMailSender, ConfirmationTokenRepository tokenRepo) {
        this.javaMailSender = javaMailSender;
        this.tokenRepo = tokenRepo;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void sendVerificationEmail(Player player) {
        ConfirmationToken confirmationToken = new ConfirmationToken(player);
        tokenRepo.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject("Welcome knight! Complete your registration!");
        mailMessage.setFrom("islandfoxjava@gmail.com");
        mailMessage.setText("To confirm your registration, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
        sendEmail(mailMessage);
    }

    public void sendBattleReport(Player player, Message message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject(message.getObject());
        mailMessage.setFrom("islandfoxjava@gmail.com");
        mailMessage.setText(message.getText());
        sendEmail(mailMessage);
    }
}
