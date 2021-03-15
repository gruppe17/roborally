package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * Represents the element on the board, e.g. walls, push panels, the priority antenna, etc.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class BoardElement extends Subject {
    /**
     * <p>Whether this element is passable by robots.</p>
     */
    protected boolean passable;

    /**
     * <p>Whether the element is opaque to standard lasers.</p>
     */
    protected boolean isOpaque;

    /**
     * <p>Which way the element is pointing. May be null if the element has no direction.</p>
     */
    protected Heading direction;

    protected Space space;

    /*Getters and setters*/
    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public boolean isPassable() {
        return passable;
    }

    public boolean isOpaque() {
        return isOpaque;
    }

    public void setOpaque(boolean opaque) {
        isOpaque = opaque;
    }

    public Heading getDirection() {
        return direction;
    }

    public void setDirection(Heading direction) {
        this.direction = direction;
    }



    /*Constructors*/
    protected BoardElement(boolean passable, boolean isOpaque) {
        this(passable, isOpaque, null);
    }

    protected BoardElement(boolean passable, boolean isOpaque, Heading direction) {
        this.passable = passable;
        this.isOpaque = isOpaque;
        this.direction = direction;
    }
}
