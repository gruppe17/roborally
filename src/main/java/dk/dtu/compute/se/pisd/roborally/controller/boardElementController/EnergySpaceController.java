package dk.dtu.compute.se.pisd.roborally.controller.boardElementController;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.EnergySpace;
import org.jetbrains.annotations.NotNull;

public class EnergySpaceController implements IBoardElementController {

    private final EnergySpace model;

    public EnergySpaceController(@NotNull EnergySpace model) {
        this.model = model;
    }


    @Override
    public void activate() {
        Player player = model.getSpace().getPlayer();
        if (player == null) return;

        if (model.getHasEnergyCube() || player.game.getStep() == 5) {
            player.addEnergyCubes(1);
            model.setHasEnergyCube(false);
        }
    }

    @Override
    public int getPriority() {
        return model.getPriority();
    }

    @Override
    public @NotNull EnergySpace getBoardElement() {
        return model;
    }
}
