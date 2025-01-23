package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.Monster;
import com.example.myfantasy.world.repository.MonstersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MonsterService {

    private final MonstersRepository monstersRepository;

    public void saveMonster(Monster monster) {
        monstersRepository.save(monster);
    }

    public Optional<Monster> getMonsterByLocation(Location location) {
        return monstersRepository.findByCurrentLocationXAndCurrentLocationY(location.getX(), location.getY());
    }

    public void deleteMonsterById(Long id) {
        monstersRepository.deleteById(id);
    }
}
