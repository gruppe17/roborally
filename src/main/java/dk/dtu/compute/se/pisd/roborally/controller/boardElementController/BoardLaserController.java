package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardLaser;
import dk.dtu.compute.se.pisd.roborally.model.boardElement.MoveHazard;

public class BoardLaserController implements IBoardElementController {
    private BoardLaser model;
    public BoardLaserController(BoardLaser model){
        this.model = model;
    }

    @Override
    public void activate() {
        Player player = model.getSpace().getPlayer();
        if (player == null) return;

    }
}
