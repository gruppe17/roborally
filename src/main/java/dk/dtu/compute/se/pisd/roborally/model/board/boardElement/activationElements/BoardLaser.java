package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.Laser;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.BoardLaserController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.MoveHazardController;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.DamageType;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;

/**
 * ...
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardLaser extends ActivationElement {
    /**
     * <p>The laser of this board laser</p>
     */
    private Laser laser;

    public BoardLaser(Heading direction, Space space, int priority){
        this(new Heading[0], new Heading[0], direction, space, priority);

    }

    protected BoardLaser(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority){
        super(new Heading[0], impassableFrom, opaqueFrom, direction, space, priority);
        laser = new Laser(priority, true, DamageType.LASER, this);
    }

    @Override
    public BoardLaserController getController() {
        return (BoardLaserController) super.getController();
    }

    @Override
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
