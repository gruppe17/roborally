package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElement;

import dk.dtu.compute.se.pisd.roborally.controller.Laser;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.BoardLaserController;
import dk.dtu.compute.se.pisd.roborally.model.enums.DamageType;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a board laser.</p>
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardLaser extends ActivationElement {
	/**
	 * <p>The laser of this board laser.</p>
	 */
	private Laser laser;

	public BoardLaser(Heading direction){
		this(direction, 6);
	}

	public BoardLaser(Heading direction, int priority){
		this(new Heading[0], new Heading[0], direction, priority);
	}

	protected BoardLaser(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, int priority){
		super(new Heading[0], impassableFrom, opaqueFrom, direction, priority);
		laser = new Laser(priority, true, DamageType.LASER, this);
	}

	@Override
	@NotNull
	public BoardLaserController getController() {
		return (BoardLaserController) super.getController();
	}

	@Override
	@NotNull
	protected BoardLaserController createContoller() {
		return new BoardLaserController(this);
	}

	public Laser getLaser() {
		return laser;
	}

	public void setLaser(Laser laser) {
		this.laser = laser;
	}
}
