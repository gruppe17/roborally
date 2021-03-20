package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a wall {@link BoardElement}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Wall extends BoardElement {

    /**
     * <p>The position of the wall on the space.</p>
     */
    protected Heading position;

    /**
     * <p>Creates a new wall with a given position.</p>
     *
     * @param position the position of the wall on the space
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public Wall(Heading position) {
        super(getBlockingArray(position), getBlockingArray(position), positionToDirection(position));
        this.position = position;
    }

    /**
     * <p>Converts the position of the wall to a direction.
     * This is relevant for drawing the wall.</p>
     * @param position the position of the wall on the space
     *
     * @return a heading representing the direction of the wall
     */
    private static Heading positionToDirection(@NotNull Heading position) {
        if (position.ordinal() % 2 == 0) return Heading.NORTH;
        return Heading.EAST;
    }

    public Heading getPosition() {
        return position;
    }

    /**
     * <p>Returns an array of {@link Heading}s containing the
     * directions blocked by the wall.</p>
     *
     * @param position the position of the wall on the space
     * @return a new heading array containing the directions blocked by the wall
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private static Heading[] getBlockingArray(Heading position) {
        return new Heading[]{position, position.next().next()};
    }

    /**
     * <p>Returns a new {@link Heading} array containing
     * {{@link Heading#NORTH}, {@link Heading#EAST},{@link Heading#SOUTH},
     * {@link Heading#WEST}}. This is done, because there seems to be a
     * problem with importing, resulting in ridiculously long names.</p>
     * <p>The problem seems to have fixed itself. But the method will stay for now.</p>
     *
     * @return A new Heading array containing the four cardinal directions
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private static Heading[] northEastSouthWestArray() {
        return new Heading[]{
                Heading.NORTH, Heading.EAST,
                Heading.SOUTH, Heading.WEST
        };
    }
}
