package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;


import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines how a board element controller, such as a {@link MoveHazardController}, should look.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public interface IBoardElementController extends IActivateable {
	//todo: Maybe this should not be a interface but an abstract class?
	// The only thing that changes among the board element controllers
	// is the constructor and the activate methods

	/**
	 * <p>Returns the {@link ActivationElement} which
	 * this is a controller for.</p>
	 * @return the ActivationElement which this is controller for
	 */
	public @NotNull ActivationElement getBoardElement();
}
