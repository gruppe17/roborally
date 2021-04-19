package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.EnergySpaceController;
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class EnergySpace extends ActivationElement {
    private boolean hasEnergyCube = true;

    public EnergySpace() {
        super(new Heading[0], new Heading[0], new Heading[0], Heading.SOUTH,  7);
    }

    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    public void setHasEnergyCube(boolean b) {
        hasEnergyCube = b;
    }

    @Override
    @NotNull
    public EnergySpaceController getController() {
        return (EnergySpaceController) super.getController();
    }

    @Override
    @NotNull
    protected EnergySpaceController createContoller() {
        return new EnergySpaceController(this);
    }
}
