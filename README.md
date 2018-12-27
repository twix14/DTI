# Coordination Service using BFT-SMaRt
This group project was built in a masters subject, Detection and Intrusion Tolerance, in the Faculty of Sciences of the University of Lisbon.
It consists of a coordination service similar to [ZooKeeper](https://github.com/apache/zookeeper) using
the [BFT-SMaRt replication library](http://bft-smart.github.io/library/)

## Features
* **Hierarchical namespace**: create "folders" and keys inside "folders". Example, you can create a node named A
and then associate a value with A/B, being B the key inside A.

* **Sequential nodes**: create nodes using sequential identifiers. Example, if you create node B and C inside folder A
the name of node B will be B_1 and node C will be C_2

* **Watchers**: a client of this service can register to receive notifications when a node is modified
(deleted or its value changed). If the watcher is assigned to a folder, the creation of removal
of nodes inside of the folder will cause a notification. Once a notification is issued, the watcher
is removed.

* **Ephemeral nodes**: ephemeral nodes are automatically deleted when the client that created them disconnects from 
the system. This feature can be used to detect failures.

## Getting Started
To use this project you just simply clone this repository using git and make sure you have a working version of 
Java Runtime Environment (This project was built using **JavaSE-1.8**).

```
git clone https://github.com/twix14/DTI.git
```

## Deployment
Since this project uses the BFT-SMaRt library you need to set `3f + 1` replicas in order to tolerate f faults in any replica, this is because this library follows the Byzantine Fault Tolerance model in order to tolerate arbitrary faults.

In order to test this project it's advised for you to run 4 local servers (**Currently it only works locally**) and as many clients as you like. **All these commands must be made inside the library-1.1-beta folder in order to work**

For this project to work in a distributed environment you must change the config/hosts.config file with the necessary IPs.

You can start a server like this (the *id* field must be unique):

```
java -cp bin/:lib/* bftmap.BFTMapServer id
```

You can start a client like this (the *id* field must be unique):

```
java -cp bin/:lib/* bftmap.ZKInteractiveClient id
```

Inside the client you'll have several options listed:
* **CREATE** 
It will ask you for:
  * The path in which to place a new node (use the forward slash). Nodes must be created as individuals, e.g first A and then A/B, and not just A/B
  * The data to place on the node (only String is currently supported since the arguments written in
the console, but internally, data is represented as bytes)
  * If you want the node/folder to be ephemeral and/or sequential

* **GET**
  * Retrieves information about that node (children, data, timestamp of the last hearbeat received in case of being ephemeral)

* **REMOVE**
  * Deletes the node

* **PRINT**
  * Prints the hierarchical namespace

* **WATCHER**
  * Place watcher on node that notifies the client that placed watcher on him (one watcher at a time) about data modification or removal of node

* **UPDATE**
  * Update node data
