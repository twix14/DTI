# DTI
---------- INICIAR OS SERVIDORES DO BFT-SMaRt-----------
4 terminais/tabs cada um com uma instância servidor do bft, com ids diferentes
java -cp bin/:lib/* bftmap.BFTMapServer *id*


---------- INICIAR O CLIENTE ZooKeeper ----------------
Iniciar o cliente num terminal/tab com um id difente dos servidores
1º java -cp bin/:lib/* bftmap.ZKInteractiveClient *id* 

2º Dentro do programa pode utilizar comandos create para criar um nó (usar esta / slash, forward slash!!)*, update para mudar os dados de um nó, watcher para colocar um watcher nesse nó que avisa se houve modificações de dados ou se ele foi removido,
print para imprimir a totalidade do namespace, get para ir buscar a informação relativa a um nó, e remove para remover um nó

*O primeiro nó tem de ser criado sem barra visto que não construímos todos os nós no caminho (ex. A/B/C só funciona se A/B já existir e A/B só funciona se A já existir)