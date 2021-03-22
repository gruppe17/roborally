package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;

public class EnergySpace extends ActivationElement {
    private boolean hasEnergyCube = true;

    protected EnergySpace(Heading[] position, Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority) {
        super(position, impassableFrom, opaqueFrom, direction, space, priority);
    }


    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    public void setHasEnergyCube(boolean b) {
        hasEnergyCube = b;
    }
}
