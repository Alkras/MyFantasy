package com.example.myfantasy.gameflow.service;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.service.CharacterService;
import com.example.myfantasy.world.model.Direction;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationBiome;
import com.example.myfantasy.world.model.LocationKey;
import com.example.myfantasy.world.model.request.NavigateRequest;
import com.example.myfantasy.world.service.LocationService;
import com.example.myfantasy.world.service.WorldGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NavigationServiceTest {
    @InjectMocks
    private NavigationService navigationService;
    @Mock
    private WorldGenerationService worldGenerationService;
    @Mock
    private CharacterService characterService;
    @Mock
    private LocationService locationService;
    @Captor
    private ArgumentCaptor<Character> characterArgumentCaptor;

    @Test
    void moveShouldGetNewLocation() {
        long characterId = 1L;
        NavigateRequest navigateRequest = new NavigateRequest(characterId, Direction.NORTH);
        Location startingLocation = Location.builder().x(0L).y(0L).build();
        LocationKey newLocationKey = new LocationKey(0L, 1L);
        Location newLocation = Location.builder().x(0L).y(1L).build();
        Character character = new Character();
        character.setCurrentLocation(startingLocation);
        doReturn(character)
                .when(characterService)
                .getCharacterById(characterId);
        doReturn(Optional.of(newLocation))
                .when(locationService)
                .getLocationById(newLocationKey);
        doReturn(character)
                .when(characterService)
                .saveHero(character);


        Location result = navigationService.move(navigateRequest);
        assertThat(result)
                .isNotNull()
                .isEqualTo(newLocation);

        verify(characterService).getCharacterById(characterId);
        verify(locationService).getLocationById(newLocationKey);
        verify(characterService).saveHero(character);
        verifyNoMoreInteractions(characterService, locationService, worldGenerationService);
    }

    @Test
    void moveShouldCreateNewLocation() {
        long characterId = 1L;
        NavigateRequest navigateRequest = new NavigateRequest(characterId, Direction.NORTH);
        Location startingLocation = Location.builder().x(0L).y(0L).locationBiome(LocationBiome.VILLAGE).build();
        LocationKey newLocationKey = new LocationKey(0L, 1L);
        Location newLocation = Location.builder().x(0L).y(1L).build();
        Character character = new Character();
        character.setCurrentLocation(startingLocation);
        doReturn(character)
                .when(characterService)
                .getCharacterById(characterId);
        doReturn(Optional.empty())
                .when(locationService)
                .getLocationById(newLocationKey);
        doReturn(newLocation)
                .when(worldGenerationService)
                .generateLocation(LocationBiome.VILLAGE, newLocationKey);
        doReturn(character)
                .when(characterService)
                .saveHero(character);

        Location result = navigationService.move(navigateRequest);
        assertThat(result)
                .isNotNull()
                .isEqualTo(newLocation);

        verify(characterService).getCharacterById(characterId);
        verify(locationService).getLocationById(newLocationKey);
        verify(worldGenerationService).generateLocation(LocationBiome.VILLAGE, newLocationKey);
        verify(characterService).saveHero(character);
        verifyNoMoreInteractions(characterService, locationService, worldGenerationService);
    }
}