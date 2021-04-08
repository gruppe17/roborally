package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.EnergySpaceController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.MoveHazardController;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;

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

    public MoveHazard(Heading direction, Space space, int priority, int rotation, int distance){
        this(new Heading[0], new Heading[0], direction, space, priority, rotation, distance);
    }

    protected MoveHazard(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority, int rotation, int distance){
        super(new Heading[0], impassableFrom, opaqueFrom, direction, space, priority);
        this.rotation = rotation;
        this.distance = distance;
    }

    @Override
    public MoveHazardController getController() {
        return (MoveHazardController) super.getController();
    }

    @Override
    protected MoveHazardController createContoller() {
        return new MoveHazardController(this);
    }
}
