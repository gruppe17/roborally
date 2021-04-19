package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.CheckpointController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.MoveHazardController;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends ActivationElement {


	private int number = 1;

	public int getNumber() {
		return number;
	}

	public Checkpoint(Heading[] position, Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, int priority) {
		super(position, impassableFrom, opaqueFrom, direction, priority);
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
