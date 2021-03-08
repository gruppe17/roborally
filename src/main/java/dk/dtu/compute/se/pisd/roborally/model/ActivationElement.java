package dk.dtu.compute.se.pisd.roborally.model;

/**
 * Represents {@link BoardElement}s that can be activated in the activation phase.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class ActivationElement extends BoardElement{
    protected Space space;

    protected ActivationElement(boolean passable, boolean isOpaque, Space space){
        super(passable, isOpaque);
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }
}
