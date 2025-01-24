package com.example.myfantasy.gameflow.controller;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.gameflow.model.FightInfo;
import com.example.myfantasy.gameflow.model.FightStatus;
import com.example.myfantasy.gameflow.service.FightService;
import com.example.myfantasy.gameflow.service.NavigationService;
import com.example.myfantasy.world.model.Direction;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.Monster;
import com.example.myfantasy.world.model.request.NavigateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActionController.class)
class ActionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private NavigationService navigationService;
    @MockitoBean
    private FightService fightService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void navigate() throws Exception {
        Location location = new Location();
        location.setX(1L);
        location.setY(2L);
        NavigateRequest navigateRequest = new NavigateRequest(1L, Direction.NORTH);
        doReturn(location)
                .when(navigationService)
                .move(navigateRequest);

        mockMvc.perform(post("/actions/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(navigateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(1))
                .andExpect(jsonPath("$.y").value(2));
    }

    @Test
    void navigateShouldReturnError() throws Exception {
        NavigateRequest navigateRequest = new NavigateRequest(0L, Direction.NORTH);

        mockMvc.perform(post("/actions/navigate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(navigateRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("You cannot play as shopkeeper :)"));
    }

    @Test
    void fight() throws Exception {
        Character character = new Character();
        character.setId(2L);
        FightInfo fightInfo = FightInfo.builder()
                .character(character)
                .monster(Monster.builder().id(1L).build())
                .fightStatus(FightStatus.ONGOING)
                .build();
        doReturn(fightInfo)
                .when(fightService)
                .fight(1L);

        mockMvc.perform(post("/actions/fight?characterId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.character.id").value(2))
                .andExpect(jsonPath("$.monster.id").value(1))
                .andExpect(jsonPath("$.fightStatus").value("ONGOING"));
    }

    @Test
    void fightShouldReturnError() throws Exception {
        mockMvc.perform(post("/actions/fight?characterId=0"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("You cannot play as shopkeeper :)"));
    }
}