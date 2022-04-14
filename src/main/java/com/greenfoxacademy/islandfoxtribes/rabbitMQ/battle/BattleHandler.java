package com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle;

import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueSender;
import com.greenfoxacademy.islandfoxtribes.repositories.battle.BattleRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.message.MessageRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.services.player.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;


@Transactional
@Component
public class BattleHandler {

    private BattleRepository battleRepository;
    private KingdomRepository kingdomRepository;
    private PlayerRepository playerRepository;
    private MessageRepository messageRepository;
    private TroopRepository troopRepository;
    private EndBattleHandler endBattleHandler;
    private EmailService emailService;

    @Autowired
    public BattleHandler(BattleRepository battleRepository, KingdomRepository kingdomRepository,
                         PlayerRepository playerRepository, MessageRepository messageRepository,
                         TroopRepository troopRepository, EndBattleHandler endBattleHandler,
                         EmailService emailService) {
        this.battleRepository = battleRepository;
        this.kingdomRepository = kingdomRepository;
        this.playerRepository = playerRepository;
        this.messageRepository = messageRepository;
        this.endBattleHandler = endBattleHandler;
        this.troopRepository = troopRepository;
        this.emailService = emailService;
    }

    public void battleResolve(Long battleId, Long travelTime)
            throws InterruptedException, IOException, TimeoutException {
        String typeOfAttack = this.battleRepository.getById(battleId).getBattleType();
        if ("Spy".equals(typeOfAttack)) {
            spyAttack(battleId, travelTime, false);
        } else if ("Plunder".equals(typeOfAttack)) {
            plunderAttack(battleId, travelTime, false);
        } else if ("TakeOver".equals(typeOfAttack)) {
            takeOverAttack(battleId, travelTime, false);
        }
    }

    public void takeOverAttack(Long battleId, Long travelTime, boolean skip)
            throws InterruptedException, IOException, TimeoutException {
        if (isAttackSuccessful(battleId, travelTime, false)) {
            Battle battle = this.battleRepository.getById(battleId);
            Kingdom attacker = this.kingdomRepository.getById(battle.getAttacker());
            Kingdom target = this.kingdomRepository.getById(battle.getTarget());
            Player attackerPlayer = attacker.getPlayer();
            Player targetPlayer = target.getPlayer();
            List<Troop> army = battle.getTroops();
            int loyaltyLost = countSenators(army) * 5;
            target.setLoyalty(target.getLoyalty() - loyaltyLost);
            this.kingdomRepository.save(target);
            String defenderText = "You have been attacked by " + attackerPlayer.getUserName() + ".";
            String attackerText = "Your attack on " + target.getName() + " was successful.";
            if (target.getLoyalty() <= 0) {
                Player attackingPlayer = attacker.getPlayer();
                Player defendingPlayer = target.getPlayer();
                target.setPlayer(attackingPlayer);
                target.setLoyalty(30);
                attackingPlayer.addKingdom(target);
                defendingPlayer.getKingdomList().remove(target);
                this.playerRepository.save(attackingPlayer);
                this.playerRepository.save(defendingPlayer);
                defenderText = defenderText.concat(" Your kingdom " + target.getName() + " is lost.");
                attackerText = attackerText.concat(" You have captured " + target.getName() + "!");
            } else {
                defenderText = defenderText.concat(" Your kingdom " + target.getName() + "have lost " + loyaltyLost +
                        "loyalty!");
                attackerText = attackerText.concat(" You destroyed " + loyaltyLost + " of kingdom's loyalty");
            }
            this.kingdomRepository.save(attacker);

            createMessage(attackerPlayer, targetPlayer, attackerText, defenderText);

            String message = "" + attacker.getId() + "/" + 0 + "/" + 0 + "/" + travelTime + "/" + battleId;
            if (!skip) {
                rabbitMQ(message);
            }
        }
    }

    public void plunderAttack(Long battleId, Long travelTime, boolean skip)
            throws InterruptedException, IOException, TimeoutException {
        if (isAttackSuccessful(battleId, travelTime, false)) {
            Battle battle = this.battleRepository.getById(battleId);
            List<Troop> army = battle.getTroops();
            Kingdom target = this.kingdomRepository.getById(battle.getTarget());
            Kingdom attacker = this.kingdomRepository.getById(battle.getAttacker());
            Player attackerPlayer = attacker.getPlayer();
            Player targetPlayer = target.getPlayer();
            int food = 0;
            int gold = 0;
            Resource kingdomFood = target.findFoodResource();
            Resource kingdomGold = target.findGoldResource();
            long capacity = army.size() * 100L;
            Random random = new Random();

            for (int i = 0; i < capacity; i++) {
                int randomNumber = random.nextInt(2);
                if (randomNumber == 0) {
                    if (kingdomGold.getAmount() > gold) {
                        gold++;
                    } else if (kingdomFood.getAmount() > food) {
                        food++;
                    }
                } else {
                    if (kingdomFood.getAmount() > food) {
                        food++;
                    } else if (kingdomGold.getAmount() > gold) {
                        gold++;
                    }
                }
            }

            String defenderText = "You have been attacked by " + attackerPlayer.getUserName() + ". " + food +
                    " food and " + gold + " gold have been lost.";
            String attackerText = "Your attack on " + target.getName() + "was successful. You are bringing home "
                    + food + " of food and " + gold + " of gold!";

            createMessage(attackerPlayer, targetPlayer, attackerText, defenderText);

            kingdomFood.setAmount(target.findFoodResource().getAmount() - food);
            kingdomGold.setAmount(target.findGoldResource().getAmount() - gold);
            this.kingdomRepository.save(target);

            String message = "" + attacker.getId() + "/" + food + "/" + gold + "/" + travelTime + "/" + battleId;
            if (!skip) {
                rabbitMQ(message);
            }
        }
    }

    private void rabbitMQ(String messageKey) throws IOException, TimeoutException {
        QueueSender battleSender = new QueueSender(messageKey);
        QueueReceiverForEndBattle queueReceiverForEndBattle = new QueueReceiverForEndBattle
                (messageKey, endBattleHandler);
        battleSender.sendMessage(messageKey);
        queueReceiverForEndBattle.receiveMessage();

    }

    private int countSenators(List<Troop> army) {
        int a = 0;
        for (Troop troop : army) {
            if (troop.getTroopType().equals(TroopType.SENATOR)) {
                a++;
            }
        }
        return a;
    }


    public boolean isAttackSuccessful(Long battleId, Long travelTime, boolean skip)
            throws IOException, TimeoutException {
        Battle battle = this.battleRepository.getById(battleId);
        Kingdom target = this.kingdomRepository.getById(battle.getTarget());
        Kingdom attacker = this.kingdomRepository.getById(battle.getAttacker());
        Player attackerPlayer = attacker.getPlayer();
        Player targetPlayer = target.getPlayer();
        List<Troop> attackersArmy = battle.getTroops();
        List<Troop> defendersArmy = target.getDefendersArmy();
        Building wall = target.findWall();
        long attackersAttack = countAttack(attackersArmy);
        long targetsAttack = countAttack(defendersArmy);
        long attackersDefend = countDefend(attackersArmy);
        long targetsDefend = countDefend(defendersArmy);
        long attack = attackersAttack - targetsDefend;
        long defend = targetsAttack - attackersDefend;
        if (wall != null) {
            defend += (long) wall.getLevel() * defendersArmy.size();
        }
        //In case of loose of attacker player
        if (defend > attack) {
            killAttackerArmy(attackersArmy, attacker, battle);
            String defenderText = "You have been attacked by " + attackerPlayer.getUserName() +
                    ", but you defend yourself!";
            String attackerText = "Your attack on " + target.getName() + " failed. Your army is destroyed.";
            if (attack > 0) {
                countLost(defendersArmy, target, attack, battle);
                defenderText = defenderText.concat(" You may have some losses!");
            } else {
                defenderText = defenderText.concat(" You have no losses!");
            }
            createMessage(attackerPlayer, targetPlayer, attackerText, defenderText);
            return false;
            // In case of even fight
        } else if (defend == attack) {
            String defenderText = "You have been attacked by " + attackerPlayer.getUserName()
                    + ", but you defend yourself! " + "You may have some losses!";
            String attackerText = "Your attack on " + target.getName() + " failed. But your army wasn't destroyed.";
            if (attack > 0) {
                countLost(defendersArmy, target, attack, battle);
            }
            if (defend > 0) {
                countLost(attackersArmy, attacker, defend, battle);
            }
            createMessage(attackerPlayer, targetPlayer, attackerText, defenderText);
            String message = "" + attacker.getId() + "/" + 0 + "/" + 0 + "/" + travelTime + "/" + battleId;
            if (!skip) {
                rabbitMQ(message);
            }
            return false;
            // In case of victory of attacker
        } else {
            killDefenderArmy(defendersArmy, target);
            if (defend > 0) {
                countLost(attackersArmy, attacker, defend, battle);
            }
            this.battleRepository.save(battle);
            return true;
        }

    }

    private void createMessage(Player attackerPlayer, Player targetPlayer, String attackerText, String defenderText) {
        Message attackerMessage = new Message(attackerPlayer, attackerText, "Attack Report", "System");
        Message defenderMessage = new Message(targetPlayer, defenderText, "Defend Report", "System");

        attackerPlayer.addMessage(attackerMessage);
        targetPlayer.addMessage(defenderMessage);

        this.playerRepository.save(attackerPlayer);
        this.playerRepository.save(targetPlayer);

        emailService.sendBattleReport(attackerPlayer, attackerMessage);
        emailService.sendBattleReport(targetPlayer, defenderMessage);

    }

    public void countLost(List<Troop> army, Kingdom kingdom, long damage, Battle battle) {
        Random random = new Random();
        for (int i = 0; i < damage; i++) {
            int randomNumber = random.nextInt(army.size());
            Troop actualTroop = army.get(randomNumber);
            actualTroop.setHp(actualTroop.getHp() - 1);
            if (actualTroop.getHp() == 0) {
                actualTroop.setKingdom(null);
                kingdom.getTroopList().remove(actualTroop);
                this.kingdomRepository.save(kingdom);
                actualTroop.setBattle(null);
                battle.getTroops().remove(actualTroop);
                this.battleRepository.save(battle);
                // Making fake Battle is the easy solution how to delete troops, because there was problems
                // caused by cascade.
                Battle fakeBattle = new Battle();
                fakeBattle.addTroopToBattle(actualTroop);
                actualTroop.setBattle(fakeBattle);
                this.battleRepository.save(fakeBattle);
                this.battleRepository.delete(fakeBattle);
            }
        }
        this.kingdomRepository.save(kingdom);
    }


    private void killDefenderArmy(List<Troop> army, Kingdom kingdom) {
        for (Troop troop : army) {
            kingdom.getTroopList().remove(troop);
            this.troopRepository.delete(troop);
        }
        this.kingdomRepository.save(kingdom);
    }


    private Long countDefend(List<Troop> army) {
        Long defend = 0L;
        for (Troop troop : army) {
            defend += troop.getDefense();
        }
        return defend;
    }

    private Long countAttack(List<Troop> army) {
        Long attack = 0L;
        for (Troop troop : army) {
            attack += troop.getAttack();
        }
        return attack;
    }

    public void spyAttack(Long battleId, Long travelTime, boolean skip) throws IOException, TimeoutException {
        Battle battle = this.battleRepository.getById(battleId);
        Kingdom target = this.kingdomRepository.getById(battle.getTarget());
        Kingdom attacker = this.kingdomRepository.getById(battle.getAttacker());
        Player targetPlayer = target.getPlayer();
        Player attackerPlayer = attacker.getPlayer();
        List<Troop> attackersArmy = battle.getTroops();
        List<Troop> defendersArmy = target.getSpiesWithoutBattle();
        // Math for finding the result
        int attackersRate = attackersArmy.size() * 10
                + troopLevelBonus(attackersArmy);
        int defendersRate = defendersArmy.size() * 5
                + troopLevelBonus(defendersArmy);
        int solution = attackersRate - defendersRate;
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        ///// Unsuccessful spy attack
        if (solution < randomNumber) {
            /// Deleting troops from the repository
            killAttackerArmy(attackersArmy, attacker, battle);
            // Sending results and alert messages
            String attackerText = "Your spies have been captured and eliminated in kingdom " + target.getName() + ".";
            String targetText = "" + attackerPlayer.getUserName() + " tried to spy on you. " +
                    "You have killed all his spies!";

            createMessage(attackerPlayer, targetPlayer, attackerText, targetText);

            /// Successful spy attack
        } else {
            List<Troop> knights = target.getKnights();
            List<Troop> swordsmen = target.getSwordsmen();
            List<Troop> archers = target.getArchers();
            List<Troop> spies = target.getSpies();
            List<Troop> senators = target.getSenators();
            int food = target.findFoodResource().getAmount();
            int gold = target.findGoldResource().getAmount();
            Building barracks = target.findBarracks();

            // Creating successful message
            int levelOfBarracks;
            if (barracks == null) {
                levelOfBarracks = 0;
            } else {
                levelOfBarracks = barracks.getLevel();
            }

            String report = "Knights: " + knights.size() + " Swordsmen: " + swordsmen.size()
                    + " Archers: " + archers.size() + " Spies: " + spies.size() + " Senators: " + senators.size()
                    + " Barracks level : " + levelOfBarracks + " Gold: " + gold + " Food: " + food;

            Message message = new Message(attackerPlayer, report, "Spy report", "System");
            attackerPlayer.addMessage(message);

            this.messageRepository.save(message);
            this.playerRepository.save(attackerPlayer);

            emailService.sendBattleReport(attackerPlayer, message);

            String messageKey = "" + attacker.getId() + "/" + 0 + "/" + 0 + "/" + travelTime + "/" + battleId;
            if (!skip) {
                rabbitMQ(messageKey);
            }
        }
    }

    private void killAttackerArmy(List<Troop> attackersArmy, Kingdom attacker, Battle battle) {
        for (Troop troop : attackersArmy) {
            troop.setKingdom(null);
            attacker.getTroopList().remove(troop);
        }
        this.battleRepository.delete(battle);
        this.kingdomRepository.save(attacker);
        this.troopRepository.deleteAll(attackersArmy);
    }

    private int troopLevelBonus(List<Troop> army) {
        int result = 0;
        for (Troop troop : army) {
            result += troop.getLevel();
        }
        return result;
    }
}
