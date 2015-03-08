Implementation of the SARSA Algorithm - ROS Node
====================================================

Author Jaroslav Vitku [jarda.vitku@gmail.com]

About
------

Project which implements simple discrete 2D gridworld simulator with obstacles and one or more reward/punishment sources.

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

Gridworld node has two types of outputs and one type of input:

* Reinforcement: an output whose value determines amount of current reinforcement (punishment TODO)
* Data: this usually multidimensional output which determines the state of the environment (2D currently)
* Input: this (potentially multidimensional) input determines an action selected by the agent


Before usage, the number of state variables, number of values per variable and number of actions has to be specified.

The simulator should be configured with given obstacles and sources of rewards/punishments (TODO).

Running the Demo
---------------------

The algorithm can be ran either as a NeuralModule in the Nengoros simulator, or as a standalone ROS node (TODO example script). 

## Standalone ROS Node

this is everything TODO: 

The start scripts are prepared in the `environments/gridworld` folder which simplify launching the nodes. There are three following nodes:

* **GridWorldNode** (`org.hanns.rl.discrete.ros.testnodes.GridWorldNode`) which implements simple simulator with a grid world and a source of reinforcement

The following should be started from the `environments/gridworld` folder for successful running the demo:

	jroscore
	./map		# simple grid world simulator

## Nengoros Integration

The standard jython scripts are included in the `environments/gridworld/python` folder, so [linking the data](http://nengoros.wordpress.com/tutorials/integrating-new-project-with-the-nengoros/) into the simulator and following one of tutorials [how to run ROS nodes](http://nengoros.wordpress.com/tutorials/demo-2-publisher-subscriber/) in the Nengoros is sufficient. To link data, run the following command from the folder `nengoros/demonodes`:

	./linkdata -cf ../environments/gridworld
	

TODO and Design Details
-----------------------

Node configuration is defined on two main levels:

* **Final configuration** is defined by means of ROS parameters received (ideally) during the ROS node startup. The configuration parameter may change during the simulation, but this will most likely result in the resetting the algorithm data.

* **Online Configuration** will be determined online by means of config inputs. These inputs are basically the same as data inputs and their values can be changed during the simulation.

## Discrete World

* **Number of (domain independent) actions** is received as the final configuration (in the configuration) received as ROS node parameter during the start

* **Number of world states** is another question, this could be done in one of the following manners:

	* States are defined by array of positive integers on the interval `<0;x>`
 	* ROS communication by means of String messages (of known set) defining possible states (not very universal)
 	* Automatic identification of states (e.g. integers on the interval `<?,?>`)

	Or several possibilities from above which cane be turned on/off by the static configuration.


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
	
	
### Running the Gridworld

After launching the node:

* the algorithm is setup (if no commandline configuration parameters found, the default ones are loaded) 
* then the node waits for received action
* if the action is received, the world rules are applied and the resulting state together with reinforcement is sent back (published)

## Example of Use

Run the GridWorldNode and the testnodes.RandomlyMovingAgent as follows:
	
	cd environments/gridworld
	./jroscore
	./world
	./randomAgent

or with some parameters:

	./world _logPeriod:=1 _obstacles:=0,2 _size:=2,10 _rewards:=1,5,1,1,0,6,2,-1
	./randomAgent _delay:=150
	
## Changelog

* agent's and environment's correct behavior tested

* added visualization of agent's position

* added randomAgent which tests the `GridWorldNode`

* the `ros.AbstractGridWorldNode` is fully configurable, contains the `GridWorld` simulator and waits for connections. TODO test it with some agent.

* support for lists of integers passed as commandline parameters added (define all the world properties from commandline)

* the GridWorld completely reworked, changes are:

	* tales made as classes (tale has name, kind..)
	* rewards can have arbitrary float value (punishments supported)
	* rewards support multiple reward types
	* world can have arbitrary no. of rewards and reward types
	* world is configurable on the startup

* added dependency on the `statesactions` project

* fixed bug in the synchronization in the simulator, sync problems should be gone

* all python scripts updated, including the GUI template
	
* sending the `NOOP` action to the `nengo` simulator caused that the node is not ready (now the node publishes prosperity also during the `NOOP`)

* new branch `feature-separate-asm`, now main of the code is copied into the `smdp` project, the old sources remain in the `mdp` project for backwards compatibility 

* added on Observer which logs qMatrix into file @see org.hanns.rl.discrete.observer.qMatrix.stats

* refactoring

	- restructuralized the package hierarchy (SARSA vs Q-Learning vs SARSA-Lambda vs Q-Lambda)
	- project SARSA renamed to MDP
	- all unit tests work


## TODO
		
* Define the Observer interface for the GridWorldNode, call observers.observe() each step..

* Unit tests: fail after predefined time? If one of tested nodes ends with an exception, the other waits indefinitely.


