package org.hanns.environments.discrete.ros;

import java.util.LinkedList;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

public class AbstractGridWorldNode extends AbstractConfigurableHannsNode{

	@Override
	public String listParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void hardReset(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void softReset(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LinkedList<Observer> getObservers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getProsperity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void publishProsperity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildDataIO(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProsperityObserver getProsperityObserver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void parseParameters(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void registerParameters() {
		// TODO Auto-generated method stub
		
	}

	
	
}
