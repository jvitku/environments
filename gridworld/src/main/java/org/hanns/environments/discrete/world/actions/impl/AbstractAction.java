package org.hanns.environments.discrete.world.actions.impl;

import org.hanns.environments.discrete.world.actions.Action;

public abstract class AbstractAction implements Action{
	
	public static final String[] labels = {"^","v",">", "<"};

	protected String label;
	protected int number;
	
	@Override
	public String getLabel() { return this.label; }

	@Override
	public int getNumber() { return this.number; }
}
