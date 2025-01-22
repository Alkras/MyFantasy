package com.example.myfantasy.world.model.request;

import com.example.myfantasy.world.model.Direction;
import com.example.myfantasy.world.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.internal.ImmutableNamedBasicTypeImpl;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NavigateRequest {
    private long heroId;
    private Direction direction;
}
