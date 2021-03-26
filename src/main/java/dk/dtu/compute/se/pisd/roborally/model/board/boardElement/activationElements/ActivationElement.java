package dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;

/**
 * Represents {@link BoardElement}s that can be activated in the activation phase.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class ActivationElement extends BoardElement {
    /**
     * <p>The space this {@link ActivationElement} is occupying</p>
     */
    protected Space space;
    /**
     * <p>The activation priority of the element.</p>
     */
    protected int priority;

    protected ActivationElement(Heading[] position, Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction, Space space, int priority){
        super(position, impassableFrom, opaqueFrom, direction);
        this.space = space;
        this.priority = priority;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public int getPriority() {return priority;}
}
