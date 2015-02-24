Implementation of the SARSA Algorithm - ROS Node
====================================================

Author Jaroslav Vitku [jarda.vitku@gmail.com]

About
------

Project which defines sets of actions and states for discrete/continuous (TODO) environments, that can be encoded/decoded by the ROS nodes.

This ROS node will be used mainly in Hybrid Artificial Neural Network Systems, used in the ROS network or the [Nengoros simulator](http://nengoros.wordpress.com). 


Requirements
------------------

Rosjava [core](https://github.com/jvitku/nengoros).

Installation
------------------

Installation can be obtained by running the following command

	./gradlew installApp

In case of any problems, the best way how to use these nodes is by means of the NengoRos project (see: [https://github.com/jvitku/nengoros](https://github.com/jvitku/nengoros) )

Usage Information 
----------------

The project is supposed to be used as library 
Gridworld node has two types of outputs and one type of input:

* Reinforcement: an output whose value determines amount of current reinforcement (punishment TODO)
* Data: this usually multidimensional output which determines the state of the environment (2D currently)
* Input: this (potentially multidimensional) input determines an action selected by the agent


Before usage, the number of state variables, number of values per variable and number of actions has to be specified.

The simulator should be configured with given obstacles and sources of rewards/punishments (TODO).

Running the Demo
---------------------

The algorithm can be ran either as a NeuralModule in the Nengoros simulator, or as a standalone ROS node. 

## Standalone ROS Node

this is everything TODO: 

The start scripts are prepared in the `rl/sarsa` folder which simplify launching the nodes. There are three following nodes:

* **QLambda node** (`org.hanns.rl.discrete.ros.sarsa.QLambda`) which implements the Q(lambda) discrete RL algorithm with eligibility traces
* **GridWorldNode** (`org.hanns.rl.discrete.ros.testnodes.GridWorldNode`) which implements simple simulator with a grid world and a source of reinforcement
* **QLambdaConfigurator** (`org.hanns.rl.discrete.ros.testnodes.QLambdaConfigurator`) which serves as an example of online configuration of ROS RL node

The following should be started from the `rl/sarsa` folder for successful running the demo:

	jroscore
	./conf 		# RL algorithm configuration
	./map		# simple grid world simulator
	./qlambda	# Q(lambda) discrete RL algorithm

## Nengoros Integration

## Discrete SARSA
* **Number of (domain independent) actions** is received as the final configuration (in the configuration) received as ROS node parameter during the start


* **Number of world states** is another question, this could be done in one of the following manners:

	* States are defined by array of positive integers on the interval `<0;x>`
 	* ROS communication by means of String messages (of known set) defining possible states (not very universal)
 	* Automatic identification of states (e.g. integers on the interval `<?,?>`)

	Or several possibilities from above which cane be turned on/off by the static configuration.

### Configuration

#### Inputs Outputs

* Actions:
	
	* The algorithm select 1ofN actions, so **single integer defining** how many actions to make. For compatibility with the Nengo, these will be represented exactly as 1ofN. Data is vector of N numbers, where only one value is set to 1. 
	
	* The configuration is **final** for now
	* Therefore this configuration **determines number of outputs** (Nengoros)

* States

	* State can be potentially multidimensional, while supposing that the received values are on **range of <0,1>**, the sampling can be defined by two parameters as:
	
		* Sampling resolution (how many samples between <0,1>)
		* Size of input data (length of received vector of values on the interval <0,1>)
		
	* The configuration is **final** for now
	* This configuration (also) **determines number of inputs** (Nengoros)
	
	The sampling resolution can be set the same for all inputs or for each one separately (different size of Q-learning matrix)

## Changelog


## TODO

* move all discrete actions and states from RL/SARSA here