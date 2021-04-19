package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.EnergySpace;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
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

        // if player has reached all checkpoints, trigger win.

        if(player.getLastCheckpoint() == model.getNumber()-1){
            player.setLastCheckpoint(player.getLastCheckpoint() + 1);
            if(model.getSpace().board.getCheckpointAmount() == player.getLastCheckpoint()){
                player.game.setPhase(Phase.GAME_FINISHED);
            }
        }


    }

    @Override
    public int getPriority() {
        return model.getPriority();
    }

    @Override
    public @NotNull Checkpoint getBoardElement() {
        return model;
    }


}
