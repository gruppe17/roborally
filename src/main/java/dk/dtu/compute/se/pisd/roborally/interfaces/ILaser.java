package dk.dtu.compute.se.pisd.roborally.interfaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;

public interface ILaser extends IActivateable{

    @Override
    default void activate(){
        fire();
    }

    /**
     * <p>Fires a laser according to it's implementation.</p>
     */
    void fire();
}
