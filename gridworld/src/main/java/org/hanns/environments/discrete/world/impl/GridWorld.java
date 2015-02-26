package org.hanns.environments.discrete.world.impl;

import org.hanns.environments.discrete.world.objects.Tale;

public class GridWorld extends AbsGridWorld{

	public GridWorld(int sx, int sy) {
		super(sx, sy);
		// TODO Auto-generated constructor stub
	}

	int []current;
	
	/**
	 * TODO make this work
	 */
	@Override
	public void makeStep(int action) {
		int[] next = this.current.clone();
		
		if(action==0){ 					// left
			if(current[0] > 0){
				next[0] = current[0]-1;
			}
		}else if(action==1){ 			// right
			if(current[0] < sx-1){
				next[0] = current[0]+1;
			}
		}else if(action==2){ 			// up 
			if(current[1] < sy-1){
				next[1] = current[1]+1;
			}
		}else if(action==3){ 			// down
			if(current[1] > 0){
				next[1] = current[1]-1;
			}
		}else if(action==-1){			// NOOP
			//return next; // TODO
		}else{
			System.err.println("unrecognized action! Action:"+action);
		}
	}

	@Override
	public int[] getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void placeObject(Tale object, int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
}
