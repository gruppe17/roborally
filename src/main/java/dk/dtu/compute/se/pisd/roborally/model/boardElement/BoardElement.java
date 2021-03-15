package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

/**
 * Represents the element on the board, e.g. walls, push panels, the priority antenna, etc.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class BoardElement extends Subject {
    /**
     * <p>Whether this element is passable by robots
     * coming from a certain direction. If a direction is
     * not contained herein it is considered to be
     * passable from that direction.</p>
     */
    protected Heading[] impassableFrom;

    /**
     * <p>Whether the element is opaque to standard lasers
     * coming from a certain direction. If a direction is
     * not contained herein it is considered to not be
     * opaque from that direction.</p>
     */
    protected Heading[] opaqueFrom;

    /**
     * <p>Which way the element is pointing. May be null if the element has no direction.</p>
     */
    protected Heading direction;


    /*Getters and setters*/
    public Heading[] getImpassableFrom(){ return impassableFrom;}

    public Heading[] getOpaqueFrom() {return opaqueFrom;}

    public Heading getDirection() {
        return direction;
    }

    public void setDirection(Heading direction) {
        this.direction = direction;
    }


    protected BoardElement() {
        this(null, null);
    }

    /*Constructors*/
    protected BoardElement(Heading[] impassableFrom, Heading[] opaqueFrom) {
        this(impassableFrom, opaqueFrom, null);
    }

    protected BoardElement(Heading[] impassableFrom, Heading[] opaqueFrom, Heading direction) {
        this.impassableFrom = impassableFrom;
        this.opaqueFrom = opaqueFrom;
        this.direction = direction;
    }
}
