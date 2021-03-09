package dk.dtu.compute.se.pisd.roborally.controller.player;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.layout.Priority;

public class RobotLaser implements ILaser {

    Player owner;

    public RobotLaser(Player owner){
        this.owner = owner;
    }

    /**
     * <p>Fire in the direction the owner
     * robot is facing. Their range has no
     * limit. Any robot in the line of sight is
     * shot. Robot lasers cannot fire through
     * walls or shoot more than one robot.</p>
     */
    @Override
    public void fire() {
        Space currentSpace = owner.getSpace();
        Space lastSpace = currentSpace;

        /* Abort if player is not in a space*/
        if(lastSpace == null)
            return;

        while(true){
            /* get next space */
            lastSpace = owner.board.getNeighbour(lastSpace, owner.getHeading());
            Player playerAtSpace = lastSpace.getPlayer();
            /* remember to add the right elements to prevent hitting walls and stuff here */
           /* if(lastSpace.element == WallBoardElement || lastSpace.element == PriorityAntennaBoardElement){
                break;
            }*/

            /* exit if nothing is hit */
            if(lastSpace == null){
                break;
            }

            /* We should add fx to the spaces that are hit*/

            /* If player is hit, then damage it and do stuff. */
            if(playerAtSpace != null && playerAtSpace != owner){
                break;
            }
        }

        /*Maybe do some cleanup of fx here?*/
    }
}
