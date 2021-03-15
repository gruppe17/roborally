package dk.dtu.compute.se.pisd.roborally.model.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement;

/**
 * Represents {@link BoardElement}s that can be activated in the activation phase.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class ActivationElement extends BoardElement implements IActivateable {
    /**
     * <p>The space this {@link ActivationElement} is occupying</p>
     */
    protected Space space;
    /**
     * <p>The activation priority of the element.</p>
     */
    protected int priority;

    protected ActivationElement(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority){
        super(impassableFrom, opaqueFrom, direction);
        this.space = space;
        this.priority = priority;
    }

    public Space getSpace() {
        return space;
    }

    public int getPriority() {return priority;}

    public abstract void activate();
}
