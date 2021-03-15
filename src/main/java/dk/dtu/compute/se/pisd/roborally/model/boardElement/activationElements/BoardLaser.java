package dk.dtu.compute.se.pisd.roborally.model.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.model.*;

public class BoardLaser extends ActivationElement {


    protected BoardLaser(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority){
        super(impassableFrom, opaqueFrom, direction, space, priority);

    }

    /**
     * <p>Represents the start and end points of the laser.</p>
     * <p>{start x, start y, end x, end y}</p>
     */
    int[] endPoints;

    @Override
    public void activate() {

    }
}
