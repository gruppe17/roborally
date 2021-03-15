package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * Represents {@link BoardElement}s that can be activated in the activation phase.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class ActivationElement extends BoardElement{
    /**
     * <p>The space this {@link ActivationElement} is occupying</p>
     */
    protected Space space;
    /**
     * <p>The activation priority of the element.</p>
     */
    protected int priority;

    protected ActivationElement(boolean passable, boolean isOpaque, Space space, int priority){
        super(passable, isOpaque);
        this.space = space;
        this.priority = priority;
    }

    public Space getSpace() {
        return space;
    }

    public int getPriority() {return priority;}
}
