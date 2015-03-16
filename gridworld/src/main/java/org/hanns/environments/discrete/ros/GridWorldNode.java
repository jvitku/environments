package org.hanns.environments.discrete.ros;

import java.util.Random;

import org.hanns.environments.discrete.world.actions.Action;
import org.hanns.environments.discrete.world.actions.impl.FourWayMovement;
import org.hanns.environments.discrete.world.impl.GridWorld;
import org.hanns.environments.discrete.world.objects.impl.Obstacle;
import org.hanns.environments.discrete.world.objects.impl.RewardSource;
import org.hanns.rl.common.exceptions.DecoderException;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractHannsNode;
import ctu.nengoros.util.SL;

/**
 * Parsing the parameters is handled in the parent. Here, rather the direct communication
 * with the GridWorld simulator is handled. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class GridWorldNode extends AbstractGridWorldNode{

	protected boolean dataExchanged = false;
	protected volatile boolean simPaused = false;
	protected int step = 0;
	
	protected BasicVariableEncoder posXEncoder;	// different resolutions in different dimensions
	protected BasicVariableEncoder posYEncoder;
	
	protected OneOfNEncoder actionEncoder;
	protected ActionSetInt actionSet; 

	private GridWorld world;
	
	@Override
	public void onStart(ConnectedNode connectedNode) {
		super.onStart(connectedNode);
		
		this.initData();
		
		this.buildDataIO(connectedNode);
		System.out.println(me+"Node configured and ready now!");
		this.waitForConnections(connectedNode);
	}

	protected void initData(){
		// need to encode x values in one float {0,1}
		posXEncoder = new BasicVariableEncoder(0,1, this.worldSize[0]);
		posYEncoder = new BasicVariableEncoder(0,1, this.worldSize[1]);
		
		actionSet = new BasicFinalActionSet(new String[]{"<",">","^","v"});
		actionEncoder = new OneOfNEncoder(actionSet);
		
		// initialize the world and place there all the stuff 
		world = new GridWorld(this.worldSize[0], this.worldSize[1]);
		
		for(int i=0; i<super.obstaclePositions[0].length; i++){
			world.placeObject(new Obstacle(),
					super.obstaclePositions[0][i], super.obstaclePositions[1][i]);
		}
		for(int i=0; i<super.rc.values.length; i++){
			world.placeObject(new RewardSource(rc.values[i], String.valueOf(rc.types[i])),
					super.rc.positions[0][i], super.rc.positions[1][i]);
		}
		this.hardReset(false);
	}
	
	@Override
	public void hardReset(boolean randomize) {

		this.step =0;
		this.dataExchanged = false;
		this.simPaused = false;
		
		if(randomize && super.randomizeAllowed){
			int maxAttempts = 10000;	// if the map is full of obstacles, the program could hang here
			int attempt = 0;
			int[] pos = new int[]{0,0};
			Random r = new Random();
			while(attempt++ < maxAttempts){
				pos[0] = r.nextInt(worldSize[0]);
				pos[1] = r.nextInt(worldSize[1]);
				if(world.teleportAgentTo(pos, false)){
					break;
				}
			}
		}else{
			world.teleportAgentTo(agentInitPosition, true);
		}
	}
	
	@Override
	protected void buildDataIO(ConnectedNode connectedNode){

		/**
		 * State publisher - connect to the input-data topic of (.e.g.) QLambda
		 * 
		 * Note that environments publish to topic named topicDataIn -> normal nodes receive these messages
		 */
		statePublisher = connectedNode.newPublisher(AbstractHannsNode.topicDataIn, std_msgs.Float32MultiArray._TYPE);

		/**
		 * Action subscriber = subscribe to agents actions, process agents requests for simulating one step
		 * 
		 * Note that environments subscribe to topic named topicDataOut -> receive topicDataOut from normal nodes
		 */
		Subscriber<std_msgs.Float32MultiArray> actionSub = 
				connectedNode.newSubscriber(AbstractHannsNode.topicDataOut, std_msgs.Float32MultiArray._TYPE);

		actionSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != noActions){
					log.error(me+"Received action description has" +
							"unexpected length of"+data.length+"! Expected number "
							+ "of actions (coding 1ofN) is "+noActions);
				}else{
					// try to decode action, if success, make simulation step and response with new data
					try {
						dataExchanged = true;
						int action = actionEncoder.decode(data);
						Action a = new FourWayMovement(action);

						if((step)%logPeriod == 0)
							log.info(me+"STEP: "+step+" Received agent's action, this one: "+SL.toStr(data)
									+" the action no "+action);

						// make step
						world.makeStep(a);
						
						// read and send the data
						std_msgs.Float32MultiArray fl = statePublisher.newMessage();
						fl.setData(encodeStateRewardMessage(world.getRewards(),world.getPosition()));
						statePublisher.publish(fl);	// send a response with reinforcement and new state
						
						//state = newState.clone(); // TODO why this?

						// log if needed
						if((step++) % logPeriod==0){
							System.out.println(me+"Responding with this message [rewards,position] "+
									SL.toStr(encodeStateRewardMessage(world.getRewards(),world.getPosition())));
							System.out.println(world.vis());
						}
					} catch (DecoderException e) {
						log.error(me+"Unable to decode agent's action, ignoring this message!");
						e.printStackTrace(); 
					}
				}
			}
		});
	}
	
	/**
	 * Get description of the environment state (agents position) and the list of rewards.
	 * Encode this it into an array of raw float values.
	 * The first N values correspond to supported types of reinforcements, the rest values
	 * are encoded integer values of environment variables (XY positions now).
	 * @param rewards list of reward values (for each type one value) 
	 * @param coords list of state variables (XY coordinates now)
	 * @return vector of float values from the range between 0 and 1
	 */
	protected float[] encodeStateRewardMessage(float[] rewards, int[] coords){
		float[] f = new float[rewards.length + coords.length];
		int pos = 0;
		for(int i=0; i<rewards.length; i++){
			f[pos] = rewards[pos++]; 
		}
		f[pos++] = posXEncoder.encode(coords[0]);
		f[pos] = posYEncoder.encode(coords[1]);
		return f;
	}
	
	/**
	 * This method is used for waiting for receiving communication.
	 * The node publishes current state of the environment, if in the 
	 * last second no message with action received. Newly connected agents 
	 * will respond with their action to this message.  
	 * 
	 * Note: it runs during entire simulation, so the waiting is renewed.
	 * 
	 * @param connectedNode
	 */
	protected void waitForConnections(ConnectedNode connectedNode){
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {}
			@Override
			protected void loop() throws InterruptedException {
				float[] published;
				Thread.sleep(1000);
				if(!dataExchanged){
					published = encodeStateRewardMessage(world.getRewards(),world.getPosition());
					log.info(me+"No agent detected, publishing the current [rewards+state] "+
							SL.toStr(published));
					System.out.println(world.vis());
					std_msgs.Float32MultiArray fl = statePublisher.newMessage();
					fl.setData(published);  
					statePublisher.publish(fl);
				}
				dataExchanged = false;
			}
		});
	}
}
