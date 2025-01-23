package com.example.myfantasy.gameflow.model;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.world.model.Monster;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FightInfo {
    private Character character;
    private Monster monster;
    private FightStatus fightStatus;
}
