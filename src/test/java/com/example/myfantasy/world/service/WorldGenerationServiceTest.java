package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorldGenerationServiceTest {

    @InjectMocks
    private WorldGenerationService worldGenerationService;
    @Mock
    private MonsterGenerationService monsterGenerationService;
    @Mock
    private LocationService locationService;
    @Mock
    private MonsterService monsterService;

    @Captor
    private ArgumentCaptor<Location> locationArgumentCaptor;

    @Test
    void generateLocationWithoutMonster() {
        Location expectedLocation = new Location();
        doReturn(expectedLocation)
                .when(locationService)
                .saveLocation(locationArgumentCaptor.capture());

        doReturn(Optional.empty())
                .when(monsterGenerationService)
                .generateMonster(any(), any(), eq(1));

        Location returnedLocation = worldGenerationService.generateLocation(LocationBiome.VILLAGE, new LocationKey(1L, 1L));

        assertThat(returnedLocation)
                .isNotNull()
                .isEqualTo(expectedLocation);

        assertThat(locationArgumentCaptor.getValue())
                .isNotNull()
                .satisfies(
                        x -> assertThat(x.getLocationThreatLevel()).isEqualTo(0),
                        x -> assertThat(x.getX()).isEqualTo(1L),
                        x -> assertThat(x.getY()).isEqualTo(1L),
                        x -> assertThat(x.getLocationBiome()).isNotNull(),
                        x -> assertThat(x.getLocationType()).isNotNull()
                );
    }

    @Test
    void generateLocationWithMonster() {
        Monster monster = new Monster();
        Location expectedLocation = new Location();
        doReturn(expectedLocation)
                .when(locationService)
                .saveLocation(locationArgumentCaptor.capture());

        doReturn(Optional.of(monster))
                .when(monsterGenerationService)
                .generateMonster(any(), any(), eq(1));

        doNothing()
                .when(monsterService)
                .saveMonster(monster);

        Location returnedLocation = worldGenerationService.generateLocation(LocationBiome.VILLAGE, new LocationKey(1L, 1L));

        assertThat(returnedLocation)
                .isNotNull()
                .isEqualTo(expectedLocation)
                .extracting(Location::getLocationThreatLevel)
                .isEqualTo(1);

        Location capturedLocation = locationArgumentCaptor.getValue();
        assertThat(capturedLocation)
                .isNotNull()
                .satisfies(
                        x -> assertThat(x.getLocationThreatLevel()).isEqualTo(0),
                        x -> assertThat(x.getX()).isEqualTo(1L),
                        x -> assertThat(x.getY()).isEqualTo(1L),
                        x -> assertThat(x.getLocationBiome()).isNotNull(),
                        x -> assertThat(x.getLocationType()).isNotNull()
                );

        verify(locationService).saveLocation(capturedLocation);
        verify(monsterGenerationService).generateMonster(any(), any(), eq(1));
        verify(monsterService).saveMonster(monster);
        verifyNoMoreInteractions(locationService, monsterGenerationService, monsterService);
    }

    @Test
    void verifyRandomnessInGeneration() {
        //POSSIBLE FLAKY? generation number during asserts have their values adjusted by +-100 to make it probably not flaky:)
        Location expectedLocation = new Location();
        doReturn(expectedLocation)
                .when(locationService)
                .saveLocation(locationArgumentCaptor.capture());

        doReturn(Optional.empty())
                .when(monsterGenerationService)
                .generateMonster(any(), any(), eq(1));


        IntStream.range(0, 1000).forEach(_ -> worldGenerationService.generateLocation(LocationBiome.VILLAGE, new LocationKey(1L, 1L)));


        List<Location> locations = locationArgumentCaptor.getAllValues();
        assertThat(locations).isNotNull().hasSize(1000);

        Map<LocationType, Long> groupedByLocationType = locations.stream().map(Location::getLocationType).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<LocationBiome, Long> groupedByLocationBiome = locations.stream().map(Location::getLocationBiome).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        assertThat(groupedByLocationType).isNotNull().hasSize(3);
        assertThat(groupedByLocationBiome).isNotNull().hasSize(5);

        //Current generation in ideal conditions should have 800 MONSTER, 100 SHOP and EMPTY each
        assertThat(groupedByLocationType.get(LocationType.MONSTER)).isGreaterThan(700);
        assertThat(groupedByLocationType.get(LocationType.SHOP)).isLessThan(200);
        assertThat(groupedByLocationType.get(LocationType.EMPTY)).isLessThan(200);

        //Current generation in ideal conditions should have 600 VILLAGE(Village was passed as argument to generation), 100 each of rest
        assertThat(groupedByLocationBiome.get(LocationBiome.VILLAGE)).isGreaterThan(500);
        assertThat(groupedByLocationBiome.get(LocationBiome.CASTLE)).isLessThan(200);
        assertThat(groupedByLocationBiome.get(LocationBiome.CAVE)).isLessThan(200);
        assertThat(groupedByLocationBiome.get(LocationBiome.DESERT)).isLessThan(200);
        assertThat(groupedByLocationBiome.get(LocationBiome.FOREST)).isLessThan(200);
    }
}