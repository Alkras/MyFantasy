package com.example.myfantasy.gameflow.service;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.service.CharacterService;
import com.example.myfantasy.gameflow.exception.FightException;
import com.example.myfantasy.gameflow.model.FightInfo;
import com.example.myfantasy.gameflow.model.FightStatus;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.model.Monster;
import com.example.myfantasy.world.service.MonsterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FightServiceTest {

    @InjectMocks
    private FightService fightService;
    @Mock
    private CharacterService characterService;
    @Mock
    private MonsterService monsterService;

    @Test
    void fightShouldReturnCantFightHere() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.EMPTY)
                .build();
        long characterId = 1L;
        Character toBeReturned = new Character();
        toBeReturned.setId(characterId);
        toBeReturned.setCurrentLocation(location);

        doReturn(toBeReturned)
                .when(characterService).getCharacterById(characterId);

        assertThatThrownBy(() -> fightService.fight(characterId))
                .isInstanceOf(FightException.class)
                .hasMessage("You wont find anything to fight here");

        verify(characterService).getCharacterById(characterId);
        verifyNoMoreInteractions(characterService, monsterService);
    }

    @Test
    void fightShouldReturnNoMonster() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.MONSTER)
                .build();
        long characterId = 1L;
        Character toBeReturned = new Character();
        toBeReturned.setId(characterId);
        toBeReturned.setCurrentLocation(location);

        doReturn(toBeReturned)
                .when(characterService).getCharacterById(characterId);
        doReturn(Optional.empty())
                .when(monsterService)
                .getMonsterByLocation(location);

        assertThatThrownBy(() -> fightService.fight(characterId))
                .isInstanceOf(FightException.class)
                .hasMessage("There is no monster in this area");

        verify(characterService).getCharacterById(characterId);
        verify(monsterService).getMonsterByLocation(location);
        verifyNoMoreInteractions(characterService, monsterService);
    }

    @Test
    void fightShouldReturnMonsterDefeated() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.MONSTER)
                .build();
        long characterId = 1L;
        Character toBeReturned = new Character();
        toBeReturned.setId(characterId);
        toBeReturned.setCurrentLocation(location);

        doReturn(toBeReturned)
                .when(characterService).getCharacterById(characterId);
        Monster monster = getMonster(0L, 0, 0, 0, 0);
        doReturn(Optional.of(monster))
                .when(monsterService)
                .getMonsterByLocation(location);

        assertThatThrownBy(() -> fightService.fight(characterId))
                .isInstanceOf(FightException.class)
                .hasMessage("Monster is already defeated");

        verify(characterService).getCharacterById(characterId);
        verify(monsterService).getMonsterByLocation(location);
        verifyNoMoreInteractions(characterService, monsterService);
    }

    @Test
    void fightShouldBeWon() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.MONSTER)
                .build();
        long characterId = 1L;
        Character character = getCharacter(characterId, location, 10);
        Monster monster = getMonster(0L, 1, 0, 1, 1);

        doReturn(character)
                .when(characterService).getCharacterById(characterId);
        doReturn(Optional.of(monster))
                .when(monsterService)
                .getMonsterByLocation(location);
        doReturn(character)
                .when(characterService)
                .characterLevelUp(eq(character), eq(100));
        doReturn(character)
                .when(characterService)
                .saveHero(character);

        FightInfo fight = fightService.fight(characterId);
        assertThat(fight).isNotNull();
        assertThat(fight)
                .extracting(FightInfo::getFightStatus)
                .isEqualTo(FightStatus.WIN);

        assertThat(fight)
                .extracting(FightInfo::getMonster)
                .isNull();

        assertThat(fight)
                .extracting(FightInfo::getCharacter)
                .isNotNull()
                .extracting(Character::getId,
                        Character::getMoney,
                        x -> x.getCurrentLocation().getLocationType(),
                        x -> x.getCurrentLocation().getLocationThreatLevel())
                .containsExactly(1L, BigDecimal.valueOf(100), LocationType.EMPTY, 0);

        verify(characterService).getCharacterById(characterId);
        verify(monsterService).getMonsterByLocation(location);
        verify(monsterService).deleteMonsterById(0L);
        verify(characterService).characterLevelUp(eq(character), eq(100));
        verify(characterService).saveHero(character);
        verifyNoMoreInteractions(characterService, monsterService);
    }

    @Test
    void fightShouldBeLost() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.MONSTER)
                .build();
        long characterId = 1L;
        Character character = getCharacter(characterId, location, 0);
        Monster monster = getMonster(0L, 1000, 100000, 100000, 100000);

        doReturn(character)
                .when(characterService).getCharacterById(characterId);
        doReturn(Optional.of(monster))
                .when(monsterService)
                .getMonsterByLocation(location);
        doNothing()
                .when(monsterService)
                .saveMonster(monster);
        doNothing().when(characterService)
                .removeHeroById(characterId);

        FightInfo fight = fightService.fight(characterId);
        assertThat(fight).isNotNull();
        assertThat(fight)
                .extracting(FightInfo::getFightStatus)
                .isEqualTo(FightStatus.LOST);

        assertThat(fight)
                .extracting(FightInfo::getMonster)
                .isNotNull();

        assertThat(fight)
                .extracting(FightInfo::getCharacter)
                .isNull();


        verify(characterService).getCharacterById(characterId);
        verify(monsterService).getMonsterByLocation(location);
        verify(monsterService).saveMonster(monster);
        verify(characterService).removeHeroById(characterId);
        verifyNoMoreInteractions(characterService, monsterService);
    }


    @Test
    void fightShouldBeOngoing() {
        Location location = Location.builder()
                .x(1L)
                .y(1L)
                .locationType(LocationType.MONSTER)
                .build();
        long characterId = 1L;
        Character character = getCharacter(characterId, location, 0);
        Monster monster = getMonster(0L, 100, 0, 10, 10);

        doReturn(character)
                .when(characterService).getCharacterById(characterId);
        doReturn(Optional.of(monster))
                .when(monsterService)
                .getMonsterByLocation(location);
        doNothing()
                .when(monsterService)
                .saveMonster(monster);
        doReturn(character)
                .when(characterService)
                .saveHero(character);

        FightInfo fight = fightService.fight(characterId);
        assertThat(fight).isNotNull();
        assertThat(fight)
                .extracting(FightInfo::getFightStatus)
                .isEqualTo(FightStatus.ONGOING);

        assertThat(fight)
                .extracting(FightInfo::getMonster)
                .isNotNull()
                .extracting(Monster::getHitPoints)
                .isEqualTo(95);

        assertThat(fight)
                .extracting(FightInfo::getCharacter)
                .isNotNull()
                .extracting(Character::getHitPoints)
                .isEqualTo(95);

        verify(characterService).getCharacterById(characterId);
        verify(monsterService).getMonsterByLocation(location);
        verify(monsterService).saveMonster(monster);
        verify(characterService).saveHero(character);
        verifyNoMoreInteractions(characterService, monsterService);
    }

    private static Character getCharacter(long characterId, Location location, int agility) {
        Character character = new Character();
        character.setId(characterId);
        character.setCurrentLocation(location);
        character.setInventory(new ArrayList<>());
        character.setAttack(10);
        character.setArmor(10);
        character.setAgility(agility);
        character.setHitPoints(100);
        character.setMaxHitPoints(100);
        character.setMoney(BigDecimal.ZERO);
        character.setLevel(1);
        return character;
    }

    private static Monster getMonster(long id, int hitPoints, int agility, int armor, int attack) {
        Monster monster = new Monster();
        monster.setId(id);
        monster.setHitPoints(hitPoints);
        monster.setAgility(agility);
        monster.setArmor(armor);
        monster.setAttack(attack);
        return monster;
    }
}