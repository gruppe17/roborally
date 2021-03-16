package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;


public class Wall extends BoardElement{

    Heading position;

    public Wall(Heading position){
        super(northEastSouthWestArray(), northEastSouthWestArray(), positionToDirection(position));
        this.position = position;
    }

    private static Heading positionToDirection(@NotNull Heading position){
        if (position.ordinal() % 2 == 0) return Heading.NORTH;
        return Heading.EAST;
    }

    public Heading getPosition(){
        return position;
    }

    /**
     * <p>Returns a new {@link Heading} array containing
     * {{@link Heading#NORTH}, {@link Heading#EAST},{@link Heading#SOUTH},
     * {@link Heading#WEST}}. This is done, because there seems to be a
     * problem with importing, resulting in ridiculously long names.</p>
     * @return A new Heading array containing the four cardinal directions
     */
    private static Heading[] northEastSouthWestArray(){
        return new Heading[] {
            Heading.NORTH, Heading.EAST,
                    Heading.SOUTH, Heading.WEST
        };
    }
}
