package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;

public class BoardLaser implements ILaser {

    @Override
    public void fire() {

    }

    /**
     * <p>Represents the start and end points of the laser.</p>
     * <p>{start x, start y, end x, end y}</p>
     */
    int[] endPoints;
}
