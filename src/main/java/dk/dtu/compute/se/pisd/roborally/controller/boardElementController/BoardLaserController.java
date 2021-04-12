package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.BoardLaser;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardLaserController implements IBoardElementController {
    private BoardLaser model;
    public BoardLaserController(BoardLaser model){
        this.model = model;
    }

    /**
     * <p>Fire in the direction the owner
     * robot is facing. Their range has no
     * limit. Any robot in the line of sight is
     * shot. Robot lasers cannot fire through
     * walls or shoot more than one robot.</p>
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    @Override
    public void activate() {
        getBoardElement().getLaser().activate();
    }

    @Override
    public int getPriority() {
        return model.getPriority();
    }

    @Override
    public @NotNull BoardLaser getBoardElement() {
        return model;
    }

    /**
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public void readyToSave(){
        model.setSpace(null);
        model.setControllerNull();
    }
}
