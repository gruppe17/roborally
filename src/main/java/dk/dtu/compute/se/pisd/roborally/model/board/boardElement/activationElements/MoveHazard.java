package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.EnergySpaceController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.MoveHazardController;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.MoveHazardType;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a {@link ActivationElement} that moves the player.
 * E.g. push panels and conveyor belts.</p>
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class MoveHazard extends ActivationElement {
	/**
	 * <p>The number of clockwise turns. May be negative.</p>
	 */
	protected int rotation;
	/**
	 * <p>The distance to move a {@link Player}.</p>
	 */
	protected int distance;

	/**
	 * <p>The type of the move hazard. Used for displaying
	 * the element.</p>
	 */
	protected MoveHazardType moveHazardType;

	public MoveHazard(Heading direction, int priority, int rotation, int distance, MoveHazardType moveHazardType){
		this(new Heading[0], new Heading[0], direction, priority, rotation, distance, moveHazardType);
	}

	protected MoveHazard(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, int priority, int rotation, int distance, MoveHazardType moveHazardType){
		super(new Heading[0], impassableFrom, opaqueFrom, direction, priority);
		this.rotation = rotation;
		this.distance = distance;
		this.moveHazardType = moveHazardType;
	}

	public int getDistance() {
		return distance;
	}

	public int getRotation() {
		return rotation;
	}

	public MoveHazardType getMoveHazardType() {
		return moveHazardType;
	}

	@Override
	@NotNull
	public MoveHazardController getController() {
		return (MoveHazardController) super.getController();
	}

	@Override
	@NotNull
	protected MoveHazardController createContoller() {
		return new MoveHazardController(this);
	}
}
