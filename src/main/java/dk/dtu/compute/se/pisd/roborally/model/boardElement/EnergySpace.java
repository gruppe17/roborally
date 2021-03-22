package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class EnergySpace extends ActivationElement {
    private boolean hasEnergyCube = true;

    protected EnergySpace(boolean passable, boolean isOpaque, Space space) {
        super(passable, isOpaque, space);
    }

    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    public void setHasEnergyCube(boolean b) {
        hasEnergyCube = b;
    }
}
