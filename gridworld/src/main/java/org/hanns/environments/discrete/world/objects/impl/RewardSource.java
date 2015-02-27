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
	
	public static final String DEF_TYPE = "0";
	
	// different sources of reward do not need to produce different type of reward!
	private final String type;
	
	public RewardSource(float reward){
		this.type = DEF_TYPE;
		this.reward = reward;
		this.label = String.valueOf(reward);
	}
	
	public RewardSource(float reward, String type){
		this.type = type;
		this.reward = reward;
		this.label = String.valueOf(reward);
	}
	
	public String getRewardType(){ return this.type; }
}
