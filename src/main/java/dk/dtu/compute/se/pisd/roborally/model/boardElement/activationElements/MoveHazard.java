package dk.dtu.compute.se.pisd.roborally.model.boardElement.activationElements;

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
     * <p>The number of clockwise turns. May be negative.</p>
     */
    public int rotation;
    /**
     * <p>The distance to move a {@link Player}.</p>
     */
    public int distance;


    protected MoveHazard(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority, int rotation, int distance){
        super(impassableFrom, opaqueFrom, direction, space, priority);
        this.rotation = rotation;
        this.distance = distance;
    }

}
