# Coordination Service using BFT-SMaRt
This project was made in a masters subject (Detection and Intrusion Tolerance), in the Faculty of Sciences of the University of Lisbon.
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
Java Development Kit. If not download it [here](https://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html).

```git
  git clone https://github.com/twix14/DTI.git
```

## Deployment



---------- INICIAR OS SERVIDORES DO BFT-SMaRt-----------
4 terminais/tabs cada um com uma instância servidor do bft, com ids diferentes
java -cp bin/:lib/* bftmap.BFTMapServer *id*


---------- INICIAR O CLIENTE ZooKeeper ----------------
Iniciar o cliente num terminal/tab com um id difente dos servidores
1º java -cp bin/:lib/* bftmap.ZKInteractiveClient *id* 

2º Dentro do programa pode utilizar comandos create para criar um nó (usar esta / slash, forward slash!!)*, update para mudar os dados de um nó, watcher para colocar um watcher nesse nó que avisa se houve modificações de dados ou se ele foi removido,
print para imprimir a totalidade do namespace, get para ir buscar a informação relativa a um nó, e remove para remover um nó

*O primeiro nó tem de ser criado sem barra visto que não construímos todos os nós no caminho (ex. A/B/C só funciona se A/B já existir e A/B só funciona se A já existir)
