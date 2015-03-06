package org.hanns.environments.discrete.world.actions.impl;

public class FourWayMovement extends MovementAction{

	public FourWayMovement(int direction){
		if(direction<0 || direction>3){
			System.err.println("Only moving in 4 directions {0,1,2,3} is allowed here!");
		}
		this.label = AbstractAction.labels[direction];
		this.number = direction;
	}

}
