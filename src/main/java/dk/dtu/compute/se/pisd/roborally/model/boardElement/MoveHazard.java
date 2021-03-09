package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * <p>Represents a {@link ActivationElement} that moves the player.
 * E.g. push panels and conveyor belts.</p>
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class MoveHazard extends ActivationElement {
    /**
     * <p>The direction this {@link MoveHazard} moves a {@link Player} in. May be null.</p>
     */
    public Heading direction;
    /**
     * <p>The number of clockwise turns. May be negative.</p>
     */
    public int rotation;
    /**
     * <p>The distance to move a {@link Player}.</p>
     */
    public int distance;

    protected MoveHazard(boolean passable, boolean isOpaque, Space space){
        super(passable, isOpaque, space);
    }

}