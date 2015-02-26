package org.hanns.environments.discrete.world;

import org.hanns.environments.discrete.world.objects.Tale;

public interface GridWorldInt{
	
	public void makeStep(int action);
	
	/**
	 * @return current XY position in the map
	 */
	public int[] getPosition();
	
	/**
	 * @return list of reward values of length corresponding
	 * to the number of reward sources
	 */
	public int[] getRewards();
	
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
}
