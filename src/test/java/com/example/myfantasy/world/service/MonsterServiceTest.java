package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.Monster;
import com.example.myfantasy.world.repository.MonstersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonsterServiceTest {

    @InjectMocks
    private MonsterService monsterService;

    @Mock
    private MonstersRepository monstersRepository;

    @Test
    void saveMonster() {
        Monster monster = new Monster();
        monsterService.saveMonster(monster);

        verify(monstersRepository).save(Mockito.eq(monster));
        verifyNoMoreInteractions(monstersRepository);
    }

    @Test
    void getMonsterByLocation() {
        Location location = new Location();
        location.setX(0L);
        location.setY(0L);

        Monster expectedResult = new Monster();
        doReturn(Optional.of(expectedResult))
                .when(monstersRepository)
                .findByCurrentLocationXAndCurrentLocationY(0L, 0L);

        Optional<Monster> monsterByLocation = monsterService.getMonsterByLocation(location);

        assertThat(monsterByLocation)
                .isNotNull()
                .isNotEmpty()
                .get()
                .isEqualTo(expectedResult);

        verify(monstersRepository).findByCurrentLocationXAndCurrentLocationY(0L, 0L);
        verifyNoMoreInteractions(monstersRepository);
    }

    @Test
    void deleteMonsterById() {
        monsterService.deleteMonsterById(0L);

        verify(monstersRepository).deleteById(0L);
        verifyNoMoreInteractions(monstersRepository);
    }
}