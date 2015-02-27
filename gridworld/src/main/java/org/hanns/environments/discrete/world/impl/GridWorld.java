package org.hanns.environments.discrete.world.impl;

import java.util.ArrayList;

import org.hanns.environments.discrete.world.GridWorldInt;
import org.hanns.environments.discrete.world.objects.Tale;
import org.hanns.environments.discrete.world.objects.impl.Empty;
import org.hanns.environments.discrete.world.objects.impl.RewardSource;

public class GridWorld implements GridWorldInt{

	protected final int sx, sy;
	protected final int[] current = {0,0};	// current position (before step)
	protected final int[] previous = {0,0};	// previous position
	
	protected final Tale[][] map;
	
	protected ArrayList<String> rewardTypes;
	protected int[] rewards;	// one source produces one reward (currently), so encoding 0/1ofN
	
	public GridWorld(int sx, int sy) {
		this.sx = sx;
		this.sy = sy;
		
		rewardTypes = new ArrayList<String>();	// list of unique reward names
		
		map = new Tale[sx][sy];
		for(int i=0; i<sy; i++){
			for(int j=0; j<sx; j++){
				map[j][i] = (Tale) new Empty();	// fill with empty tales at first
			}
		}
	}
	
	@Override
	public void placeObject(Tale object, int x, int y) {
		if(x<0 || x>=sx || y<0 || y>=sy){
			System.err.println("Error placing object, position out of map!");
			return;
		}
		map[x][y] = object;	// place into the map
		
		if(object instanceof RewardSource){
			// new type? add it
			if(!this.rewardTypeRegistered(((RewardSource)object).getRewardType())){
				rewardTypes.add(((RewardSource)object).getRewardType());
			}
		}
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

		this.updatePrevPos();			// store the original position
		
		if(action==0){ 					// left
			if(current[0] > 0){
				if(!map[current[0]-1][current[1]].isObstacle()){ // if obstacle, position remains the same
					current[0]--;
					//coords[0] = current[0]-1;
				}
			}
		}else if(action==1){ 			// right
			if(current[0] < sx-1){
				if(!map[current[0]+1][current[1]].isObstacle())
					current[0]++;
					//coords[0] = current[0]+1;
			}
		}else if(action==2){ 			// up 
			if(current[1] < sy-1){
				if(!map[current[0]][current[1]+1].isObstacle())
					//coords[1] = current[1]+1;
					current[1]++;
			}
		}else if(action==3){ 			// down
			if(current[1] > 0){
				if(!map[current[0]][current[1]-1].isObstacle())
					//coords[1] = current[1]-1;
					current[1]--;
			}
		}else if(action == -1){			// NOOP
			assert true;
		}else{
			System.err.println("unrecognized action! "+action);
		}
		this.updateRewards();			// update data about current rewards produced
		return;
	}

	@Override
	public int[] getPosition() { return current.clone(); }

	@Override
	public int[] getRewards() {	return this.rewards.clone(); }

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

	private void updatePrevPos(){
		previous[0] = current[0];
		previous[1] = current[1];
	}
	
	/**
	 * 
	 */
	private void updateRewards(){
		// agent either stays (no reward) or moves (only one reward on new coords.) 
		this.rewards = new int[this.rewardTypes.size()];
		if(!this.hasMoved()){
			return;
		}
		if(map[current[0]][current[1]] instanceof RewardSource){
			// TODO arrayList or Map here?
		}
			
	}

	/**
	 * @param type what to search for
	 * @return true if the reward type is already registered
	 */
	private boolean rewardTypeRegistered(String type){
		if(rewardTypes.isEmpty())
			return false;
		
		for(int i=0; i<rewardTypes.size(); i++){
			if(rewardTypes.get(i).equalsIgnoreCase(type))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasMoved() {
		return (current[0]!=previous[0])|| (current[1]!=previous[1]);
	}

}
