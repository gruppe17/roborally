package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a wall {@link BoardElement}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Wall extends BoardElement {
    //todo: should this really be a class still? It is very small

    /**
     * <p>Creates a new wall with a given position.</p>
     *
     * @param position the position of the wall on the space
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public Wall(Heading position) {
        super(new Heading[] {position} , getBlockingArray(position), getBlockingArray(position), positionToDirection(position));
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


}
