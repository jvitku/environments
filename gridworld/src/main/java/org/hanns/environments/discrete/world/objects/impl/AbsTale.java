package org.hanns.environments.discrete.world.objects.impl;

import org.hanns.environments.discrete.world.objects.Tale;

public abstract class AbsTale implements Tale{
	
	public static final String EMPTY = ".";
	public static final String OBSTACLE = "x";
	
	// default tale is an empty one
	protected boolean isObstacle = false;
	protected float reward = 0;
	protected String label = AbsTale.EMPTY;


	@Override
	public boolean isObstacle() { return this.isObstacle;	}

	@Override
	public String getLabel() { return this.label; }

	@Override
	public float getReward() { return this.reward; }

}
