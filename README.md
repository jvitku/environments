HANNS project - Nodes for Simulated Environments
================================================


Author Jaroslav Vitku [jarda.vitku@gmail.com]


About
------

This is a part of Hybrid Artificial Neural Network Systems (HANNS) project (see [website](http://artificiallife.co.nf) ). 

Each node can be connected into a potentially heterogeneous network of nodes communicating via the ROS, potentially [Nengoros](http://nengoros.wordpress.com). 

This is ROS meta-package (currently without direct catkin support), a collection of ROS(java) nodes.

 
Purpose of this repository
-----------------------

This project holds environments and test cases for the HANNS. There should be:

* both discrete (e.g. `gridworld`) and continuous (`Vivae`) environments.
* corresponding discrete and continuous definitions of actions and states (`statesactions` project).


Technical notes
---------------


Installation
------------------

The best way how to install these nodes so far, is to use them as a part of the NengoRos project (see [github](https://github.com/jvitku/nengoros) )

TODO
----------

* move the Vivae simulator here

Changelog
------------------
* gridworld now depends on the statesactions project, eclipse works
* gridworld separated into statesactions and gridworld
* unwanted sources cleaned-up

