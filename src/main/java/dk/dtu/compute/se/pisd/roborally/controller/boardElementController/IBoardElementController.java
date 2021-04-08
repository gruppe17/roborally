package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;


import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines how a board element controller, such as a {@link MoveHazardController}, should look.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public interface IBoardElementController extends IActivateable {
	public @NotNull ActivationElement getBoardElement();
}
