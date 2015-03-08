package org.hanns.environments.discrete.world.impl;

import java.util.HashMap;

import org.hanns.environments.discrete.world.GridWorldInt;
import org.hanns.environments.discrete.world.actions.Action;
import org.hanns.environments.discrete.world.actions.impl.FourWayMovement;
import org.hanns.environments.discrete.world.actions.impl.MovementAction;
import org.hanns.environments.discrete.world.objects.Tale;
import org.hanns.environments.discrete.world.objects.impl.Empty;
import org.hanns.environments.discrete.world.objects.impl.RewardSource;

public class GridWorld implements GridWorldInt{

	public static String AGENT_LABEL = "A";
	
	protected final int sx, sy;
	protected final int[] current = {0,0};	// current position (before step)
	protected final int[] previous = {0,0};	// previous position
	
	protected final Tale[][] map;
	
	protected HashMap<String, Integer> rewardTypes;
	private int numRewardTypes = 0;	// used for indexing an array returned by the getRewards() 
	
	protected float[] rewards;	// one source produces one reward (currently), so encoding 0/1ofN
	
	public GridWorld(int sx, int sy) {
		this.sx = sx;
		this.sy = sy;
		
		rewardTypes = new HashMap<String, Integer>();	
		rewards = new float[0];	// changed while the new reward type is added
		
		map = new Tale[sx][sy];
		for(int i=0; i<sy; i++){
			for(int j=0; j<sx; j++){
				map[j][i] = (Tale) new Empty();	// fill with empty tales at first
			}
		}
	}
	
	@Override
	public boolean teleportAgentTo(int[] position, boolean warn){
		if(position[0]<0 || position[1]<0 || position[0]>=sx || position[1]>=sy){
			if(warn)
				System.err.println("ERROR: cannot teleport the agent outside the map! Ignoring..");
			return false;
		}
		if(map[position[0]][position[1]].isObstacle()){
			if(warn)
				System.err.println("ERROR: cannot teleport the agent outside the map! Ignoring..");
			return false;
		}
		current[0] = position[0];
		current[1] = position[1];
		return true;
	}
	
	@Override
	public void placeObject(Tale object, int x, int y) {
		if(x<0 || x>=sx || y<0 || y>=sy){
			System.err.println("Error placing object, position out of map!");
			return;
		}
		map[x][y] = object;	// place into the map
		
		if(object instanceof RewardSource){
			
			if(!this.rewardTypes.containsKey(((RewardSource)object).getRewardType())){
				rewardTypes.put(((RewardSource)object).getRewardType(), numRewardTypes++);
				rewards = new float[this.getNumRewardTypes()];
			}
		}
	}
	
	@Override
	public int getSX() { return sx; }

	@Override
	public int getSY() { return sy; }
	
	@Override
	public void makeStep(Action a) {
		
		this.updatePrevPos();			// store the original position
		
		this.updateCustomActions(a);
		this.updateMovementActions(a);
		
		this.updateRewards();			// update data about current rewards produced
		return;
	}
	

	@Override
	public void updateCustomActions(Action a) {
		if(a instanceof MovementAction)
			return;
		// TODO potentially add some custom actions (pickup, putdown..)
	}

	@Override
	public void updateMovementActions(Action a) {
		if(a instanceof MovementAction){
			if(a instanceof FourWayMovement){
				
				int action = ((FourWayMovement)a).getNumber();

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
			}else{
				// TODO potentially support e.g. 8-way movements if needed
				System.err.println("ERROR: only FourWayMovement actions are supported for now!");
			}
		}
	}

	@Override
	public int[] getPosition() { return current.clone(); }

	@Override
	public float[] getRewards() { return this.rewards.clone(); }
	
	// this should be constant during the simulation
	@Override
	public int getNumRewardTypes(){ return this.numRewardTypes; }

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

				if(this.isAgentHere(i, j)){
					line = line + "\t "+AGENT_LABEL;
				}else{
					line = line + "\t "+map[j][i].getLabel();
				}
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
	}
	
	private boolean isAgentHere(int x, int y){ return (x==current[0] && y==current[1]); }

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

	private Tale getCurrentTale(){ return map[current[0]][current[1]]; }
	
	private void updatePrevPos(){
		previous[0] = current[0];
		previous[1] = current[1];
	}

	private void cleanRewards(){
		this.rewards = new float[this.getNumRewardTypes()];
		for(int i=0; i<this.getNumRewardTypes(); i++){
			this.rewards[i] = 0;
		}
	}
	
	/**
	 * Read current reward(s) and store them in one vector of reward types
	 */
	private void updateRewards(){
		// agent either stays (no reward) or moves (only one reward on new coords.) 
		this.cleanRewards();
		if(!this.hasMoved()){
			return;
		}
		if(this.getCurrentTale() instanceof RewardSource){
			RewardSource rs = (RewardSource)this.getCurrentTale();
			this.rewards[this.rewardTypes.get(rs.getRewardType())] = rs.getRewardVal();
		}
	}
	
	@Override
	public boolean hasMoved() {
		return (current[0]!=previous[0])|| (current[1]!=previous[1]);
	}

}
