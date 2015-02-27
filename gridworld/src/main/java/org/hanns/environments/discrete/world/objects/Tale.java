package org.hanns.environments.discrete.world.objects;

/**
 * One tale in the gridworld, can be an:
 * -obstacle
 * -reward
 * -punishment
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Tale{
	
	public boolean isObstacle();
	
	public String getLabel();
	
	public float getReward();

}
