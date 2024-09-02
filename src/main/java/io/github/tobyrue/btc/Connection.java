package io.github.tobyrue.btc;

import java.util.Optional;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum Connection implements StringIdentifiable
{
    NONE("none"),
    UP("up", Direction.UP),
    DOWN("down", Direction.DOWN),
    NORTH("north", Direction.NORTH),
    SOUTH("south", Direction.SOUTH),
    WEST("west", Direction.WEST),
    EAST("east", Direction.EAST);

    private final String name;
    private final Optional<Direction> direction;

    private Connection(String name)
    {
        this.name = name;
        this.direction = Optional.empty();
    }

    private Connection(String name, Direction direction)
    {
        this.name = name;
        this.direction = Optional.of(direction);
    }

    @Override
    public String asString()
    {
        return this.name;
    }

    public Direction asDirection()
    {
        assert(this.direction.isPresent());
        return this.direction.get();
    }

    public static Connection of(Direction direction)
    {
        for (Connection value: Connection.values())
        {
            if (value.direction.isPresent() && value.direction.get() == direction)
            {
                return value;
            }
        }

        return Connection.NONE;
    }
}