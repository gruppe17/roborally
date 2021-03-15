package dk.dtu.compute.se.pisd.roborally.model.board.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;


public class wall extends BoardElement{


    public wall(Heading position){
        super(northEastSouthWestArray(), northEastSouthWestArray(), position);
    }

    public Heading getPosition(){
        return direction;
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
