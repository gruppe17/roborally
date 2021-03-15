package dk.dtu.compute.se.pisd.roborally.interfaces;

import dk.dtu.compute.se.pisd.roborally.model.boardElement.activationElements.ActivationElement;

/**
 * <p>Represents something that can be activated, e.g. an {@link ActivationElement} or a robot laser.</p>
 */
public interface IActivateable{

    public void activate();
}
