package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationBiome;
import com.example.myfantasy.world.model.LocationKey;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.repository.LocationsRepository;
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
class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;
    @Mock
    private LocationsRepository locationsRepository;
    @Captor
    private ArgumentCaptor<Location> locationArgumentCaptor;

    @Test
    void getStartingLocationGetLocationFromDB() {
        Location expectedResult = new Location();
        LocationKey locationKey = new LocationKey(0L, 0L);

        doReturn(Optional.of(expectedResult))
                .when(locationsRepository)
                .findById(locationKey);

        Location returnedResult = locationService.getStartingLocation();

        assertThat(returnedResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        verify(locationsRepository).findById(locationKey);
        verifyNoMoreInteractions(locationsRepository);
    }

    @Test
    void getStartingLocationFirstTimeGeneration() {

        doReturn(Optional.empty())
                .when(locationsRepository)
                .findById(any(LocationKey.class));

        Location expectedReturn = new Location();
        doReturn(expectedReturn)
                .when(locationsRepository)
                .save(locationArgumentCaptor.capture());

        Location returnedResult = locationService.getStartingLocation();

        assertThat(returnedResult)
                .isNotNull()
                .isEqualTo(expectedReturn);

        Location capturedLocation = locationArgumentCaptor.getValue();

        assertThat(capturedLocation)
                .isNotNull()
                .satisfies(
                        x -> assertThat(x.getX()).isEqualTo(0L),
                        x -> assertThat(x.getY()).isEqualTo(0L),
                        x -> assertThat(x.getLocationBiome()).isEqualTo(LocationBiome.VILLAGE),
                        x -> assertThat(x.getLocationType()).isEqualTo(LocationType.EMPTY),
                        x -> assertThat(x.getLocationThreatLevel()).isEqualTo(0)
                );

        verify(locationsRepository).findById(any(LocationKey.class));
        verify(locationsRepository).save(eq(capturedLocation));
        verifyNoMoreInteractions(locationsRepository);
    }

    @Test
    void getLocationById() {
        Location expectedResult = new Location();
        LocationKey locationKey = new LocationKey(0L, 0L);

        doReturn(Optional.of(expectedResult))
                .when(locationsRepository)
                .findById(locationKey);

        Optional<Location> returnedOptionalResult = locationService.getLocationById(locationKey);

        assertThat(returnedOptionalResult)
                .isNotNull()
                .get()
                .isEqualTo(expectedResult);

        verify(locationsRepository).findById(locationKey);
        verifyNoMoreInteractions(locationsRepository);


    }

    @Test
    void saveLocation() {
        Location location = new Location();
        Location expectedLocation = new Location();
        doReturn(expectedLocation)
                .when(locationsRepository)
                .save(location);

        Location returnedResult = locationService.saveLocation(location);

        assertThat(returnedResult)
                .isNotNull()
                .isEqualTo(expectedLocation);

        verify(locationsRepository).save(location);
        verifyNoMoreInteractions(locationsRepository);
    }
}