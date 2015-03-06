package org.hanns.environments.discrete.ros;

import java.util.LinkedList;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

public abstract class AbstractGridWorldNode extends AbstractConfigurableHannsNode{

	public static final String name = "GridWorld";

	/**
	 * World parameters
	 */
	// world dimensions
	public static final String sizeConf = "size";
	//public static final String topicSize= conf+sizeConf;
	private static final int DXS=10, DYS=10;
	public static final int[] DEF_SIZE = new int[]{DXS,DYS};
	protected int[] worldSize;

	public static final String posConf = "agentPos";
	public static final int[] DEF_AGENTPOS = new int[]{0,0};
	protected int[] agentInitPosition;
	
	// obstacles
	public static final String obstaclesConf = "obstacles";
	private static final int DXO=2, DYO=5;	// place one obst.
	public static final int[] DEF_OBSTACLE = new int[]{DXO,DYO};
	protected int[][] obstaclePositions;

	// rewards
	public static final String rewardConf = "rewards";
	public static final String topicSize= conf+rewardConf;
	private static final int DXR=7, DYR=5;
	public static final int[] DEF_REWARD = new int[]{DXR,DYR, 193, 1};//type=193,r=1
	protected rewardConfig rc = new rewardConfig();

	//enable randomization from the Nengoros simulator? (override the hardreset(true) to false?)
	public static final String randomizeConf = "randomize";
	public static final boolean DEF_RANDOMIZE = false;
	protected boolean randomizeAllowed;
	
	class rewardConfig{
		int[][] positions; 	// list of N X,Y coordinates
		int[] values;		// e.g. reward/punishment source? (N values)
		int[] types;		// rewards can have different types (food/water..) 
	};
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public String listParams() { return this.paramList.listParams(); }

	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(r == null)
			return false;
		// TODO register all things that need to be started
		return true;
	}

	@Override
	public void softReset(boolean randomize){}

	// no configuration needs to be changed during the simulation here
	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {}

	@Override
	protected void buildDataIO(ConnectedNode arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public LinkedList<Observer> getObservers() { return null; }

	@Override
	public ProsperityObserver getProsperityObserver() { return null; }
	
	@Override
	public void publishProsperity() {}
	
	@Override
	public float getProsperity() { return 0; }

	@Override
	public void onStart(ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters");

		this.registerParameters();
		paramList.printParams();
		this.parseParameters(connectedNode);
		//this.registerObservers();	// probably will be unused (using logs to console)
		System.out.println(me+"initializing ROS Node IO");

		super.fullName = super.getFullName(connectedNode);
		System.out.println(me+"Node configured and ready now!");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode) {
		r = new PrivateRosparam(connectedNode);
		System.out.println(me+"parsing parameters");
		logToFile = r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);
		randomizeAllowed = r.getMyBoolean(randomizeConf, DEF_RANDOMIZE);

		worldSize =this.checkSizes(r.getMyIntegerList(sizeConf, DEF_SIZE));
		System.out.println("OK, world parameters parsed as: "+SL.toStr(worldSize));

		agentInitPosition = this.checkAgentPos(r.getMyIntegerList(posConf, DEF_AGENTPOS));
		
		obstaclePositions = this.checkObstacles(r.getMyIntegerList(obstaclesConf, DEF_OBSTACLE));
		System.out.println("OK, parsed list of obstacle coords is for X: "
				+SL.toStr(obstaclePositions[0])+" and for Y: "+SL.toStr(obstaclePositions[1]));

		this.checkRewards(r.getMyIntegerList(rewardConf,DEF_REWARD));

		System.out.println("OK, parsed list of rewards as follows. X: "
				+SL.toStr(rc.positions[0])+" and for Y: "+SL.toStr(rc.positions[1])+
				" types: "+SL.toStr(rc.types)+" rewards: "+SL.toStr(rc.values));
	}

	private void checkRewards(int[] list){
		if(list.length<4 || list.length%4!=0){
			System.err.println("ERROR: List of reward sources has not correct length: modulo 4!!" +
					" Expected format of rewards is: (X1,Y1,R1Type,R1Val,X2,Y2,..) " +
					" Will use the default reward");
			rc.positions = new int[2][1];
			rc.positions[0][0] = DEF_REWARD[0];
			rc.positions[1][0] = DEF_REWARD[1];
			rc.types = new int[]{DEF_REWARD[2]};
			rc.values = new int[]{DEF_REWARD[3]};
			return;
		}
		int expectedNoRewards = list.length/4;
		int pos=0;
		int noRewards = 0;

		// count rewards that really are in the map
		for(int i=0; i<expectedNoRewards; i++){
			if(!this.checkCoords(list[pos], list[pos+1], true)){// out of the map?
				pos+=4;
				continue;
			}
			noRewards++;
		}
		
		rc.positions = new int[2][];
		rc.positions[0] = new int[noRewards];
		rc.positions[1] = new int[noRewards];
		rc.values = new int[noRewards];
		rc.types = new int[noRewards];

		for(int i=0; i<noRewards; i++){
			if(!this.checkCoords(list[pos], list[pos+1], false)){// out of the map?
				pos+=4;
				continue;
			}
			rc.positions[0][i] = list[pos];
			rc.positions[1][i] = list[pos+1];
			rc.types[i] =list[pos+2]; 
			rc.values[i] = list[pos+3];
			pos+=4;
		}
	}
	
	private int[] checkAgentPos(int[] pos){
		if(pos.length!=2 ||
				pos[0] < 0 || pos[1] <0 ||
				pos[0] >= this.worldSize[0] || pos[1] >= this.worldSize[1]){
			System.out.println("WARNING: incorrect agent position: "+SL.toStr(pos)+" " +
					", will use the default one: "+SL.toStr(DEF_AGENTPOS));
			return DEF_AGENTPOS;
		}
		return pos;
	}
	
	private boolean checkCoords(int x, int y, boolean warn){
		if(x<0 || y<0 || x>=worldSize[0] || y>=worldSize[1]){
			if(warn)
				System.out.println("WARNING: given position out of the map, ignoring this object! ["+x+","+y+"]" +
						" (world size: "+SL.toStr(worldSize)+")");
			return false;
		}
		if(x==this.agentInitPosition[0] && y==this.agentInitPosition[1]){
			if(warn)
				System.out.println("WARNING: given position is on the agent, ignoring this object! ["+x+","+y+"]");
			return false;
		}
		return true;
	}

	private int[][] checkObstacles(int[] list){
		int[][] out = new int[2][];
		if(list.length<2){
			System.err.println("ERROR: insufficient length of list, will use default obstacle\n\n");
			out[0] = new int[]{DXO};
			out[1] = new int[]{DYO};
			return out;
		}
		int expectedNoObstacles = list.length/2;
		int noObstacles = 0;
		int pos = 0;
		
		// count obstacles that really are in the map
		for(int i=0; i<expectedNoObstacles; i++){
			if(!this.checkCoords(list[pos], list[pos+1], true)){// out of the map?
				pos+=2;
				continue;
			}
			noObstacles++;
		}
		
		if(list.length %2!=0){
			System.err.println("ERROR: List of obstacles has not even length!!" +
					" Expected format of obstacle coordinates is: (X1,Y1,X2,Y2..). " +
					" Will try to parse as much as possible..");
			noObstacles = (list.length-1)/2;
		}
		out[0] = new int[noObstacles];
		out[1] = new int[noObstacles];

		for(int i=0; i<noObstacles; i++){
			if(!this.checkCoords(list[pos], list[pos+1],false)){// out of the map?
				pos+=2;
				continue;
			}
			out[0][i] = list[pos];
			out[1][i] = list[pos+1];
			pos+=2;
		}
		return out;
	}

	private int[] checkSizes(int[] sizes){
		if(sizes.length!=2){
			System.err.println("ERROR: list of world sizes has to define X and Y, will use DEFAULT sizes!\n\n");
			return DEF_SIZE;
		}
		if(sizes[0]<=0 || sizes[1]<=0){
			System.err.println("ERROR: zero, or negative dimenison size specified, will use DEFAULT sizes!!\n\n");
			return DEF_SIZE;
		}
		return sizes;
	}


	/**
	 * NoInputs == noActions allowed
	 * NoOutputs == X&Y pos. + noRewardTypes registered 
	 */
	@Override
	protected void registerParameters() {
		paramList = new ParamList();
		// TODO IO
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of state variables");
		paramList.addParam(noOutputsConf, ""+DEF_NOOUTPUTS,"Number of actions available to the agent (1ofN coded)");
		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log?");
		paramList.addParam(randomizeConf, ""+DEF_RANDOMIZE, "Should allow RANDOMIZED reset from Nengo?");

		paramList.addParam(sizeConf, SL.toStr(DEF_SIZE), "List of two integers determining X, Y size of the map");
		paramList.addParam(posConf, SL.toStr(DEF_AGENTPOS), "Two integers determining X, Y starting position of the agent");
		paramList.addParam(obstaclesConf, SL.toStr(DEF_OBSTACLE), "List (even no.) of coordinates (X1,Y1,X2,Y2..) of obstacles");
		paramList.addParam(rewardConf, SL.toStr(DEF_REWARD), "List defining rewards as follows (X1,Y1,R1Type,R1Val,X2,Y2,..).");
	}



}
