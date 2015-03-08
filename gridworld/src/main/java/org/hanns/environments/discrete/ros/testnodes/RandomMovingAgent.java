package org.hanns.environments.discrete.ros.testnodes;

import java.util.LinkedList;
import java.util.Random;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

public class RandomMovingAgent extends AbstractConfigurableHannsNode{

	public static final String name = "RandomAgent";

	private Random rand = new Random();
	
	public static int DEF_DATALEN = 3;	// one reward + X,Y
	public static int DEF_NOACTIONS = 4;


	// no of allowed actions (1ofN code)
	public static final String delayConf = "delay";
	public static final int DEF_DELAY = 10; // 10ms wait after new message 
	protected int delay;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started");
		this.registerParameters();
		paramList.printParams();
		super.fullName = super.getFullName(connectedNode);

		this.parseParameters(connectedNode);
		this.registerSimulatorCommunication(connectedNode);
		this.buildDataIO(connectedNode);

		
		System.out.println(me+"Node configured and ready now!");
	}
	
	/**
	 * Register the {@link #actionPublisher} for publishing actions 
	 * and the state subscriber for receiving state+reward data.
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>(){
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message){
				float[] data = message.getData();
				
				if(data.length != DEF_DATALEN)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+ DEF_DATALEN);
				else{
					float[] actions = new float[DEF_NOACTIONS];
					for(int i=0; i<actions.length; i++){
						actions[i]=0;
					}
					try {
						Thread.sleep(delay);
						actions[rand.nextInt(actions.length)] = 1;
						std_msgs.Float32MultiArray fl = dataPublisher.newMessage();
						fl.setData(actions);
						dataPublisher.publish(fl);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(prospPublisher==null)
			return false;
		if(dataPublisher==null)
			return false;
		return true;
	}
	
	@Override
	public String getFullName() { return this.fullName; }
	
	@Override
	public String listParams() { return null; }

	@Override
	public void hardReset(boolean arg0) {}

	@Override
	public void softReset(boolean arg0) {}

	@Override
	public LinkedList<Observer> getObservers() { return null; }

	@Override
	public float getProsperity() { return 0; }

	@Override
	public void publishProsperity() {}


	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {}


	@Override
	public ProsperityObserver getProsperityObserver() { return null; }


	@Override
	protected void parseParameters(ConnectedNode connectedNode) {
		r = new PrivateRosparam(connectedNode);
		System.out.println(me+"parsing parameters");
		delay = r.getMyInteger(delayConf, DEF_DELAY);
		if(delay<0){
			delay=0;
		}
	}

	@Override
	protected void registerParameters() {
		paramList = new ParamList();

		paramList.addParam(delayConf, ""+DEF_DELAY,
				"How many ms to wait before responding with new action");
		
	}


}
