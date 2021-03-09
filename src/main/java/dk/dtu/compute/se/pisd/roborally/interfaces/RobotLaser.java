package dk.dtu.compute.se.pisd.roborally.interfaces;

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
        if(lastSpace == null)
            return;
        Player playerAtSpace = lastSpace.getPlayer();
        while(true){
            lastSpace = owner.board.getNeighbour(lastSpace, owner.getHeading());

            /* remember to add the right elements here */
           /* if(lastSpace.element == WallBoardElement || lastSpace.element == PriorityAntennaBoardElement){
                break;
            }*/

            /* exit if nothing is hit */
            if(lastSpace == null){
                break;
            }

            if(playerAtSpace != null && playerAtSpace != owner){
               //Damage the player.
                break;
            }
        }
    }
}
