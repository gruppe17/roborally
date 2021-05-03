package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElement;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.CheckpointController;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;


/**
 * ...
 *
 * @author Tobias Maneschijn, s205422@student.dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Checkpoint extends ActivationElement {


	/**
	 * <p>The number of this checkpoint. Reaching
	 * the checkpoint only counts if a checkpoint
	 * of the number before has been visited.</p>
	 */
	private final int number;

	public int getNumber() {
		return number;
	}

	public Checkpoint(Heading[] position, Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, int priority, int checkpointNumber) {
		super(position, impassableFrom, opaqueFrom, direction, priority);
		this.number = checkpointNumber;
	}

	@Override
	public CheckpointController getController() {
		return (CheckpointController) super.getController();
	}

	@Override
	protected CheckpointController createContoller() {
		return new CheckpointController(this);
	}


}
