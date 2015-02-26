package org.hanns.environments.discrete.world.impl;

import org.hanns.environments.discrete.world.GridWorldInt;
import org.hanns.environments.discrete.world.objects.Tale;

public abstract class AbsGridWorld implements GridWorldInt{

	protected final int sx, sy;
	protected final int[] current = {0,0};
	
	public AbsGridWorld(int sx, int sy){
		this.sx = sx;
		this.sy = sy;
	}
	
	@Override
	public int getSX() { return sx; }

	@Override
	public int getSY() { return sy; }

	@Override
	public void makeStep(int action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getPosition() { return current.clone(); }

	@Override
	public int[] getRewards() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String vis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxActionInd(Double[] actions) {
		int ind = 0;
		boolean found = false;
		for(int i=0; i<actions.length; i++){
			if(actions[ind]>0)
				found = true;
			if(actions[ind]<actions[i]){
				ind = i;
			}
		}
		if(!found)
			return -1;
		return ind;
	}

	@Override
	public void placeObject(Tale object, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
