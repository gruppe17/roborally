package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;

public class EnergySpace extends ActivationElement {
    private boolean hasEnergyCube = true;

    public EnergySpace( Space space) {
        super(new Heading[0], new Heading[0], new Heading[0], Heading.SOUTH, space, 7);
    }

    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    public void setHasEnergyCube(boolean b) {
        hasEnergyCube = b;
    }
}
