package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

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
     * <p>Represents the position of the element.
     * I.e. an element with position {{@link Heading#NORTH},
     * {@link Heading#EAST}} could potentially block
     * light or movement from either the north or the
     * east or both.</p>
     */
    protected Heading[] position;

    /**
     * <p>Which way the element is pointing. May be null if the element has no direction.</p>
     */
    protected Heading direction;


    /*Getters and setters*/

    /**
     * <p>Whether the element is at the specified
     * position of the space.</p>
     *
     * @param position the on the space to check
     * @return true if the element is at the specified position
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public boolean isAtPosition(Heading position) {
        for (Heading pos : this.position) {
            if (pos == position) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Whether the element blocks movement
     * moving from the specified headings through
     * the element.</p>
     * @param heading the heading of the mover relative to the element
     * @return true if the element blocks movement from the specified heading
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public boolean isImpassableFrom(Heading heading){
        for (Heading direction: impassableFrom) {
            if (heading == direction) return true;
        }
        return false;
    }


    public Heading[] getImpassableFrom(){ return impassableFrom.clone();}

    public Heading[] getOpaqueFrom() {return opaqueFrom.clone();}

    public Heading getDirection() {
        return direction;
    }

    public void setDirection(Heading direction) {
        this.direction = direction;
        notifyChange();
    }

    public Heading[] getPosition() {
        return position;
    }

    /**
     * <p>Sets the position. If this is needed by others,
     * then the a BoardElement class should implement
     * their own public method. <b>Do not make this
     * method public!</b> some board elements, e.g.
     * {@link Wall}s can't just have their position
     * be set to anything.</p>
     */
    protected void setPosition(Heading[] position){
        this.position = position;
    }


    protected BoardElement() {
        this(null, null);
    }

    /*Constructors*/
    protected BoardElement(Heading[] impassableFrom, Heading[] opaqueFrom) {
        this(SWNEArray(),  impassableFrom, opaqueFrom, null);
    }

    protected BoardElement(@NotNull Heading[] position, @NotNull Heading[] impassableFrom, @NotNull Heading[] opaqueFrom, Heading direction) {
        //Should probably validate position contents
        this.position = position;
        this.impassableFrom = impassableFrom;
        this.opaqueFrom = opaqueFrom;
        this.direction = direction;
    }

    /**
     * <p>Returns a new {@link Heading} array containing
     * {{@link Heading#SOUTH}, {@link Heading#WEST},
     * {@link Heading#NORTH}, {@link Heading#EAST}.</p>
     *
     * @return A new Heading array containing the four cardinal directions
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected static Heading[] SWNEArray() {
        return new Heading[]{Heading.SOUTH, Heading.WEST,
                Heading.NORTH, Heading.EAST};
    }
}
