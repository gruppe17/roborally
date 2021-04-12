package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.EnergySpace;
import org.jetbrains.annotations.NotNull;

public class CheckpointController implements IBoardElementController {

    private final Checkpoint model;

    public CheckpointController(@NotNull Checkpoint model) {
        this.model = model;
    }


    @Override
    public void activate() {
        Player player = model.getSpace().getPlayer();
        if (player == null) return;



    }

    @Override
    public int getPriority() {
        return model.getPriority();
    }

    @Override
    public @NotNull EnergySpace getBoardElement() {
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
