package org.hanns.environments.discrete.world.objects.impl;


public class Obstacle extends AbsTale{
	
	public Obstacle(){
		System.out.println("---------- obstacle registered here!!");
		this.isObstacle = true;
		this.label = AbsTale.OBSTACLE;
	}
}
