package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElement;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.interfaces.Spacebound;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import org.jetbrains.annotations.NotNull;

/**
 * Represents {@link BoardElement}s that can be activated in the activation phase.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class ActivationElement extends BoardElement implements Spacebound {
	/**
	 * <p>The space this {@link ActivationElement} is occupying</p>
	 */
	protected transient Space space;
	/**
	 * <p>The activation priority of the element.</p>
	 */
	protected int priority;

	/**
	 * <p>The controller of the {@link ActivationElement}.</p>
	 * <p>This is intentionally {@code private}.</p>
	 */
	private transient IBoardElementController controller;

	protected ActivationElement(Heading[] position, Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, int priority){
		super(position, impassableFrom, opaqueFrom, direction);
		this.priority = priority;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public int getPriority() {return priority;}

	/**
	 * <p>Returns the controller for this {@link ActivationElement}.
	 * If it is null, then it is initialised.</p>
	 * @return a non-null {@link IBoardElementController}
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@NotNull
	public IBoardElementController getController() {
		if (controller == null) controller = createContoller();
		return controller;
	}

	/**
	 * <p>Creates a new {@link IBoardElementController}
	 * for this {@link ActivationElement}.</p>
	 */
	@NotNull
	abstract protected IBoardElementController createContoller();

	public void setControllerNull(){
		this.controller = null;
	}

}
