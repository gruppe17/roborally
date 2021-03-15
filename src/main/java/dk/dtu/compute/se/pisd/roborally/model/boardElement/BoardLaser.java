package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import dk.dtu.compute.se.pisd.roborally.model.*;

public class BoardLaser extends ActivationElement {


    protected BoardLaser(boolean passable, boolean isOpaque, Space space) {
        super(passable, isOpaque, space);
        
    }


    /**
     * <p>Represents the start and end points of the laser.</p>
     * <p>{start x, start y, end x, end y}</p>
     */
    int[] endPoints;
}
