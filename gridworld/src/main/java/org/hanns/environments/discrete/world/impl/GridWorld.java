package org.hanns.environments.discrete.world.impl;

import org.hanns.environments.discrete.world.GridWorldInt;
import org.hanns.environments.discrete.world.objects.Tale;

public class GridWorld implements GridWorldInt{

	protected final int sx, sy;
	protected final int[] current = {0,0};
	
	protected final Tale[][] map;
	
	public GridWorld(int sx, int sy) {
		this.sx = sx;
		this.sy = sy;
		
		map = new Tale[sx][sy];
	}

	
	@Override
	public int getSX() { return sx; }

	@Override
	public int getSY() { return sy; }
	
	/**
	 * TODO test this
	 * TODO update rewards here
	 */
	@Override
	public void makeStep(int action) {
		int[] coords = current.clone();
		
		if(action==0){ 					// left
			if(current[0] > 0){
				if(!map[current[0]-1][current[1]].isObstacle()){ // if obstacle, position remains the same
					coords[0] = current[0]-1;
				}
			}
		}else if(action==1){ 			// right
			if(current[0] < sx-1){
				if(!map[current[0]+1][current[1]].isObstacle())
					coords[0] = current[0]+1;
			}
		}else if(action==2){ 			// up 
			if(current[1] < sy-1){
				if(!map[current[0]][current[1]+1].isObstacle())
					coords[1] = current[1]+1;
			}
		}else if(action==3){ 			// down
			if(current[1] > 0){
				if(!map[current[0]][current[1]-1].isObstacle())
					coords[1] = current[1]-1;
			}
		}else if(action==-1){ 			// NOOP
			return;
		}else{
			System.err.println("unrecognized action! "+action);
		}
		return;
	}

	@Override
	public int[] getPosition() { return current.clone(); }

	@Override
	public int[] getRewards() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Visualize the map, axes are as usual, x (first index) is horizontal increasing to the right,
	 * y is vertical increasing towards up. 
	 * @return String representing the map
	 */
	@Override
	public String vis(){
		String line = "------------------------------------\n";
		for(int i=sx-1; i>=0; i--){
			for(int j=0; j<sy; j++){

				line = line + "\t "+map[j][i].getLabel();
				
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
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
