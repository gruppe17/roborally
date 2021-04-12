package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends BoardElement{

    private int number = 1;

    public int getNumber() {
        return number;
    }

    public Checkpoint(@NotNull Heading[] position, @NotNull Heading[] impassableFrom, @NotNull Heading[] opaqueFrom, Heading direction) {
        super(position, impassableFrom, opaqueFrom, direction);

    }
}
