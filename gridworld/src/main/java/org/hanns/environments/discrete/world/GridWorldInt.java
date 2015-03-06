package org.hanns.environments.discrete.world;

import org.hanns.environments.discrete.world.actions.Action;
import org.hanns.environments.discrete.world.objects.Tale;

public interface GridWorldInt{
	
	public int getSX();
	public int getSY();
	
	/**
	 * Receive one action, apply it, infer world constraints and make sim. step 
	 * @param a an action selected by the agent
	 */
	public void makeStep(Action a);
	
	/**
	 * @return current XY position in the map
	 */
	public int[] getPosition();
	
	/**
	 * @return list of reward values of length corresponding
	 * to the number of reward sources
	 */
	public float[] getRewards();
	
	/**
	 * @return multiline String representing command-line 
	 * visualization of the map 
	 */
	public String vis();
	
	/**
	 * @param actions list of action utilities
	 * @return an index of action with max the value 
	 */
	public int getMaxActionInd(Double[] actions);
	
	/**
	 * Place given tale into the map.
	 * The map is initialized with empty tales, adding new
	 * tale rewrites the previous one.
	 * 
	 * @param object an object that implements Tale interface
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void placeObject(Tale object, int x, int y);
	
	/**
	 * @return true if the agent has moved in the previous makeStep() call
	 */
	public boolean hasMoved();
	
	/**
	 * @return number of different reward types
	 */
	public int getNumRewardTypes();
	
	
	public void updateCustomActions(Action a);
	
	public void updateMovementActions(Action a);
	
}
