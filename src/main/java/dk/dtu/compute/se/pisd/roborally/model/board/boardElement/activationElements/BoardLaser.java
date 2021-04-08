package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.BoardLaserController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.MoveHazardController;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;

public class BoardLaser extends ActivationElement {

    public BoardLaser(Heading direction, Space space, int priority){
        this(new Heading[0], new Heading[0], direction, space, priority);

    }

    protected BoardLaser(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority){
        super(new Heading[0], impassableFrom, opaqueFrom, direction, space, priority);

    }

    @Override
    public BoardLaserController getController() {
        return (BoardLaserController) super.getController();
    }

    @Override
    protected BoardLaserController createContoller() {
        return new BoardLaserController(this);
    }
}
