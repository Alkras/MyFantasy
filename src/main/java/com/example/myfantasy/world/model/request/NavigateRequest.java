package com.example.myfantasy.world.model.request;

import com.example.myfantasy.world.model.Direction;
import com.example.myfantasy.world.model.Location;
import lombok.*;
import org.hibernate.type.internal.ImmutableNamedBasicTypeImpl;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NavigateRequest {
    private long heroId;
    private Direction direction;
}
