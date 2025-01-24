package com.example.myfantasy.world.model.request;

import com.example.myfantasy.world.model.Direction;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NavigateRequest {
    private long heroId;
    private Direction direction;
}
