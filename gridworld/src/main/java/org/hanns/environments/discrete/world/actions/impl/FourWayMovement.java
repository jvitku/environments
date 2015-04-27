package org.hanns.environments.discrete.world.actions.impl;

/**
 * Defines action.
 * 
 * TODO should be unified with statesactions project more.
 * 
 * @author Jaroslav Vitku
 *
 */
public class FourWayMovement extends MovementAction{

	public FourWayMovement(int direction){

		if(direction == -1){
			this.label = "-";
			this.number = -1;
			return;
		}

		if(direction<0 || direction>3){
			System.err.println("Only moving in 4 directions {0,1,2,3} is allowed here!");
		}
		this.label = AbstractAction.labels[direction];
		this.number = direction;
	}

}
