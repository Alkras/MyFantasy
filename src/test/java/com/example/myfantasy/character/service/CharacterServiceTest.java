package com.example.myfantasy.character.service;

import com.example.myfantasy.character.model.request.CreateCharacterRequest;
import com.example.myfantasy.character.exceptions.CharacterNotFoundException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Type;
import com.example.myfantasy.character.repository.CharactersRepository;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.service.LocationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @InjectMocks
    private CharacterService characterService;

    @Mock
    private CharactersRepository charactersRepository;
    @Mock
    private LocationService locationService;

    @Captor
    private ArgumentCaptor<Character> characterCaptor;

    @Test
    void createHeroWithDefaults() {
        CreateCharacterRequest createCharacterRequest = new CreateCharacterRequest("name", Type.WARRIOR);

        Character shouldReturnCharacter = new Character();

        Location location = new Location();
        doReturn(location)
                .when(locationService)
                .getStartingLocation();
        doReturn(shouldReturnCharacter)
                .when(charactersRepository)
                .save(characterCaptor.capture());

        Character returnedCharacter = characterService.createHeroWithDefaults(createCharacterRequest);

        assertThat(returnedCharacter).isEqualTo(shouldReturnCharacter);

        Character capturedCharacter = characterCaptor.getValue();
        assertThat(capturedCharacter)
                .isNotNull()
                .satisfies(
                        x -> assertThat(x.getCurrentLocation()).isEqualTo(location),
                        x -> assertThat(x.getId()).isNull(),
                        x -> assertThat(x.getAgility()).isEqualTo(10),
                        x -> assertThat(x.getArmor()).isEqualTo(10),
                        x -> assertThat(x.getAttack()).isEqualTo(10),
                        x -> assertThat(x.getHitPoints()).isEqualTo(300),
                        x -> assertThat(x.getMaxHitPoints()).isEqualTo(300),
                        x -> assertThat(x.getName()).isEqualTo("name"),
                        x -> assertThat(x.getType()).isEqualTo(Type.WARRIOR),
                        x -> assertThat(x.getExperience()).isEqualTo(0),
                        x -> assertThat(x.getExperienceForLevelUp()).isEqualTo(100),
                        x -> assertThat(x.getLevel()).isEqualTo(1),
                        x -> assertThat(x.getInventory()).isNull(),
                        x -> assertThat(x.getMoney()).isEqualTo(BigDecimal.ZERO)
                );

        verify(charactersRepository).save(Mockito.eq(capturedCharacter));
        verify(locationService).getStartingLocation();
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void saveHero() {
        Character character = new Character();
        Character shouldReturn = new Character();

        doReturn(shouldReturn)
                .when(charactersRepository)
                .save(character);

        Character returned = characterService.saveHero(character);

        Assertions.assertThat(returned)
                .isNotNull()
                .isEqualTo(shouldReturn);

        verify(charactersRepository).save(character);
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void getCharacterById() {
        long id = 1L;
        Character shouldReturn = new Character();

        doReturn(Optional.of(shouldReturn))
                .when(charactersRepository)
                .findById(1L);

        Character returned = characterService.getCharacterById(id);

        Assertions.assertThat(returned)
                .isNotNull()
                .isEqualTo(shouldReturn);

        verify(charactersRepository).findById(1L);
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void getCharacterByIdShouldThrowError() {
        long id = 1L;

        doReturn(Optional.empty())
                .when(charactersRepository)
                .findById(1L);

        Assertions.assertThatThrownBy(() -> characterService.getCharacterById(id))
                .isInstanceOf(CharacterNotFoundException.class)
                .hasMessage("Character with id:1 does not exist");

        verify(charactersRepository).findById(1L);
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void removeHeroById() {
        long id = 1L;
        doNothing().when(charactersRepository).deleteById(id);
        characterService.removeHeroById(id);
        verify(charactersRepository).deleteById(id);
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void getAllHeroes() {
        Character character = new Character();
        List<Character> shouldReturnList = List.of(character);
        doReturn(shouldReturnList)
                .when(charactersRepository)
                .findAll();

        List<Character> returned = characterService.getAllHeroes();

        Assertions.assertThat(returned)
                .isNotNull()
                .hasSize(1)
                .isEqualTo(shouldReturnList);

        verify(charactersRepository).findAll();
        verifyNoMoreInteractions(charactersRepository, locationService);
    }

    @Test
    void characterLevelUp() {
        Character character = createCharacter();
        Character leveledUpCharacter = characterService.characterLevelUp(character, 300);

        Assertions.assertThat(leveledUpCharacter)
                .isNotNull()
                .isSameAs(character)
                .satisfies(
                        x -> assertThat(x.getId()).isEqualTo(1L),
                        x -> assertThat(x.getAgility()).isEqualTo(12),
                        x -> assertThat(x.getArmor()).isEqualTo(12),
                        x -> assertThat(x.getAttack()).isEqualTo(12),
                        x -> assertThat(x.getHitPoints()).isEqualTo(340),
                        x -> assertThat(x.getMaxHitPoints()).isEqualTo(340),
                        x -> assertThat(x.getName()).isEqualTo("Warrior"),
                        x -> assertThat(x.getType()).isEqualTo(Type.WARRIOR),
                        x -> assertThat(x.getExperience()).isEqualTo(50),
                        x -> assertThat(x.getExperienceForLevelUp()).isEqualTo(200),
                        x -> assertThat(x.getLevel()).isEqualTo(3)
                );
    }

    private Character createCharacter() {
        Character character = new Character();
        character.setType(Type.WARRIOR);
        character.setName("Warrior");
        character.setId(1L);
        character.setLevel(1);
        character.setAgility(10);
        character.setArmor(10);
        character.setAttack(10);
        character.setHitPoints(300);
        character.setMaxHitPoints(300);
        character.setExperienceForLevelUp(100);
        return character;
    }
}