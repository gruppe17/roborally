package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElement.MoveHazard;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a controller for a {@link MoveHazard}.</p>
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class MoveHazardController implements IBoardElementController {
	private MoveHazard model;
	public MoveHazardController(MoveHazard model){
		this.model = model;
	}

	@Override
	public void activate() {
		Player player = model.getSpace().getPlayer();
		if (player == null) return;
		player.playerController.move(model.getDirection(), model.getDistance());
		player.playerController.turn(model.getRotation());
	}

	@Override
	public int getPriority() {
		return model.getPriority();
	}

	@Override
	public @NotNull MoveHazard getBoardElement() {
		return model;
	}

}
