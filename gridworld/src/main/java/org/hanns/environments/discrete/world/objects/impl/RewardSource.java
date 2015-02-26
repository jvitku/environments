package org.hanns.environments.discrete.world.objects.impl;

/**
 * Can be either reward or punishment source, based on the reward value that it produces.
 * 
 * The value of the reward should become a label too 
 * 
 * @author Jaroslav Vitku
 *
 */
public class RewardSource extends AbsTale{
	
	public RewardSource(int reward){
		this.reward = reward;
		this.label = String.valueOf(reward);
	}
}
