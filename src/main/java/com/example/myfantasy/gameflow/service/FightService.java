package com.example.myfantasy.gameflow.service;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemType;
import com.example.myfantasy.character.service.CharacterService;
import com.example.myfantasy.gameflow.exception.FightException;
import com.example.myfantasy.gameflow.model.FightInfo;
import com.example.myfantasy.gameflow.model.FightStatus;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.model.Monster;
import com.example.myfantasy.world.service.MonsterService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class FightService {
    private final CharacterService characterService;
    private final MonsterService monsterService;

    @Transactional
    public FightInfo fight(Long characterId) {
        FightInfo.FightInfoBuilder fightInfoBuilder = FightInfo.builder();
        Character character = characterService.getCharacterById(characterId);
        Location currentLocation = character.getCurrentLocation();

        if (!LocationType.MONSTER.equals(currentLocation.getLocationType())) {
            throw new FightException("You wont find anything to fight here");
        }

        Monster monster = monsterService.getMonsterByLocation(currentLocation)
                .orElseThrow(() -> new FightException("There is no monster in this area"));

        if (monster.getHitPoints() <= 0) {
            throw new FightException("Monster is already defeated");
        }

        return performAttacks(characterId, character, monster, currentLocation, fightInfoBuilder);
    }

    private FightInfo performAttacks(Long characterId, Character character, Monster monster, Location currentLocation, FightInfo.FightInfoBuilder fightInfoBuilder) {
        int playerDamage = calculatePlayersDamage(character, monster);

        if ((monster.getHitPoints() - playerDamage) <= 0) {
            return calculateWinBenefitsAndReturnInfo(monster, character, currentLocation, fightInfoBuilder);
        } else {
            monster.setHitPoints(monster.getHitPoints() - playerDamage);
            monsterService.saveMonster(monster);
        }

        int monsterDamage = calculateMonstersDamage(monster, character);

        if (character.getHitPoints() - monsterDamage <= 0) {
            characterService.removeHeroById(characterId);
            return fightInfoBuilder.character(null).monster(monster).fightStatus(FightStatus.LOST).build();
        } else {
            character.setHitPoints(character.getHitPoints() - monsterDamage);
            characterService.saveHero(character);
        }

        return fightInfoBuilder.character(character).monster(monster).fightStatus(FightStatus.ONGOING).build();
    }

    private FightInfo calculateWinBenefitsAndReturnInfo(Monster monster, Character character, Location currentLocation, FightInfo.FightInfoBuilder fightInfoBuilder) {
        monsterService.deleteMonsterById(monster.getId());
        int bonus = calculateBonus(character, currentLocation);
        character.setMoney(character.getMoney().add(BigDecimal.valueOf(bonus)));
        currentLocation.setLocationThreatLevel(0);
        currentLocation.setLocationType(LocationType.EMPTY);
        Character leveledUp = characterService.saveHero(characterService.characterLevelUp(character, bonus));
        return fightInfoBuilder
                .character(leveledUp)
                .monster(null)
                .fightStatus(FightStatus.WIN).build();
    }

    private int calculateBonus(Character character, Location currentLocation) {
        return character.getLevel() <= currentLocation.getLocationThreatLevel() ?
                (currentLocation.getLocationThreatLevel() - character.getLevel() + 1) * 100 :
                (1 / (character.getLevel() - currentLocation.getLocationThreatLevel())) * 100;
    }

    private int calculatePlayersDamage(Character character, Monster monster) {
        if (isAttackEvaded(character.getAgility(), monster.getAgility())) {
            return 0;
        }
        return calculateDamageWithArmor(
                calculatePlayersAttack(character),
                monster.getArmor());
    }

    private int calculatePlayersAttack(Character character) {
        return character.getAttack() + getBestItemPower(character.getInventory(), ItemType.WEAPON);
    }

    private int calculateMonstersDamage(Monster monster, Character character) {
        if (isAttackEvaded(monster.getAgility(), character.getAgility())) {
            return 0;
        }
        return calculateDamageWithArmor(
                monster.getAttack(),
                calculatePlayersArmor(character));
    }

    private int calculatePlayersArmor(Character character) {
        return character.getArmor() + getBestItemPower(character.getInventory(), ItemType.ARMOR);
    }

    private static Integer getBestItemPower(List<Item> inventory, ItemType itemType) {
        return inventory.stream()
                .filter(item -> itemType.equals(item.getItemType())).max(Comparator.comparing(Item::getStrength))
                .map(Item::getStrength)
                .orElse(0);
    }


    private static int calculateDamageWithArmor(int attackerAttack, int defenderArmor) {
        return Math.max(Math.ceilDiv(attackerAttack, 2), attackerAttack - defenderArmor);
    }

    private boolean isAttackEvaded(int attackerAgility, int defenderAgility) {
        if (attackerAgility == 0 && defenderAgility == 0) {
            return false;
        }
        return new Random().nextInt(attackerAgility + defenderAgility) > attackerAgility;
    }

}
