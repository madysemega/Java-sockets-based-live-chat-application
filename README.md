# Java-sockets-based-live-chat-application
## Introduction
La surveillance informatique est un sujet d'actualité. Certains l’utilisent à des fins de 
monitorage pour optimiser le réseau d’une société et d’autres à des fins de cybersécurité. Plusieurs 
applications souhaitent intégrer à leur développement la protection des données des utilisateurs 
d'où l’importance de comprendre le fonctionnement des réseaux informatiques. Les services tels
que WhatsApp et Facebook sont souvent utilisés pour communiquer. Des données confidentielles 
y sont même parfois échangées. Cependant, plusieurs failles de sécurité de ces systèmes ont été 
exposées.
Le but de la présente étude est donc de se familiariser avec les concepts de sockets et de 
threads afin de concevoir une application de clavardage OpenSource, sécuritaire et qui protège 
votre vie privée. Cette application permettra d’établir une connexion de plusieurs clients de concert 
sur un même serveur qui pourront s’envoyer et recevoir des messages.
En équipe, nous avons comme mandat de s’occuper de la partie serveur et client. Les 
couches de sécurité. L'esthétique et l’interface utilisateur seront réalisées par d’autres équipes. 
Pour répondre à notre mandat, il nous est demandé de concevoir une interface console simple, tout 
en s’assurant de livrer un produit fonctionnel.
Lorsqu’un client crée un socket, il tente d’établir une connexion avec un serveur pouvant 
recevoir et gérer ce socket. Les informations du client seront donc transmises au serveur grâce à 
ce dernier. Le serveur doit pouvoir ouvrir une session unique pour chaque client tentant de se 
connecter au chat. Le client se connecte à ce socket à l’aide d’un socket de connexion ayant le 
même numéro de port que le port d’écoute du serveur. La session TCP est alors établie et identifiée 
par le couple de sockets serveur-client.
De plus, puisque le serveur doit ouvrir plusieurs connexions au chat simultanément, 
l’utilisation de threads est capitale. En effet, le serveur doit lancer un thread pour chaque connexion 
de client. Il doit servir d’intermédiaire de communication entre les différents clients connectés, 
c’est-à-dire être apte à redistribuer tous les messages reçus aux clients concernés. Ainsi, un autre 
thread doit aussi être lancé au niveau des clients pour s’assurer l’envoi et la réception des messages 
de la part du serveur en simultané

## Présentation
L’application est décomposée en deux parties. D’abord, la partie serveur qui se charge de 
recevoir, gérer et mettre fin aux connexions et interactions entre les clients. Ensuite, la partie client 
qui a pour but de se connecter pour envoyer des messages au serveur tout en ayant la possibilité 
d’en recevoir en retour pour les afficher sur un interface console.
D’une part, la partie serveur a plusieurs responsabilités, nous avons créé 4 classes afin de les 
gérer au mieux :

- Server: Cette classe récupère les informations sur l’adresse IP et le numéro de port sur 
lesquels sera exécuté le serveur. Ensuite un nouveau thread sera créé pour chaque client 
qui enverra un socket au serveur. La gestion de la communication entre le client et le 
serveur sera assurée par une autre classe.
- ServerThread: Cette classe contient le thread principal. Elle s’occupe de valider 
l’authentification du client et de lui afficher les anciens messages. Ensuite, elle exécute une 
boucle infinie (jusqu’à la déconnexion du client) qui s’occupe de lire les messages envoyés 
par le client associé au thread et les redistribués à l’ensemble des clients actifs dans le
format requis.
- ClientsInformationManager: Cette classe est utilisé par la classe précédente. Elle 
contient l’information des clients enregistrés. 
- MessagesManager: Cette classe est aussi utilisée par ServerThread. Elle contient les 
messages échangés par les clients. 
D’autre part, les responsabilités de la partie client étant moins nombreuses, nous les avons 
regroupées en une seule classe Client. Cette classe a pour but de connecter un client au serveur 
à l’aide d’un socket qu’elle aura préalablement créé. Afin de créer le socket, il faudra entrer une 
adresse IP et un numéro de port dont les validités seront vérifiées par des méthodes prévues à cet 
effet. Ensuite viendra la phase d’identification d’utilisateur dont les détails seront couverts par le 
serveur. Du côté client, cette identification consistera simplement à une suite d’envoi et de 
réception de messages avec le serveur. Une fois le client connecté au serveur, un nouveau thread 
est lancé afin d'assurer une réception continue des messages. Le thread principal quant à lui (main 
thread) enverra de manière continue les entrées de l’utilisateur au serveur. En combinant ces deux 
boucles infinies qui sont exécutées parallèlement, on parvient à simuler une impression de 
discussion en temps réel dans l’application.

## Difficultés rencontrées
La première difficulté rencontrée était de synchroniser le comportement du client et du 
serveur afin d’assurer l’envoi des messages d’un client à l’ensemble des clients connectés excepté 
le destinateur, tout en recevant les requêtes d’envois de nouveaux messages. Pour ce faire, nous 
avons fait appel à plus d’un thread et nous avons utilisé une liste de clients actifs, nous permettant
d’allouer à chacun un seul thread. 
Ensuite, nous avons rencontré une difficulté lors de la vérification des données du client. 
Nous n’arrivions pas à déterminer la classe à laquelle devait être assignée cette responsabilité. 
Nous avons fini par opter pour une vérification du côté serveur même si nous l’avions 
préalablement instaurée du côté client. En effet, le client ne peut pas s’occuper de sa propre 
vérification sur notre base de données. Nous avons donc créé des méthodes pour échanger de 
l'information avec le serveur qui est géré par la classe ServerThread. Les attributs 
outputFromServer et inputFromClient de cette classe nous permette de transférer de 
l’information entre les deux parties à l’aide de la méthode ```getInputStream()``` et 
```getOutputStream()``` qui sont acheminés par notre socket.

## Critiques et améliorations
Nous avons globalement apprécié faire ce travail pratique, puisque la mise en contexte est 
d’actualité et le travail en soi est une introduction intéressante au cours de réseaux informatiques. 
Nous avons eu à notre disponibilité de l’aide fournie par les chargés de cours, les notes de cours 
et plusieurs documents en ligne afin d’enrichir notre compréhension des sockets et des threads. La 
description du travail pratique est bien définie et il y a très peu d'ambiguïté au niveau de ce qui est 
attendu par l’étudiant. Un point à améliorer serait peut-être, si le temps le permet, d’ajouter et 
montrer la procédure permettant des échanges entre ordinateurs de différents réseaux. De plus, 
ajouter une interface n’aurait pas nécessiter beaucoup de travail supplémentaire, mais aurait pu 
être une option intéressante de visualisation du produit final. 
Conclusion
En conclusion, l’objectif de ce laboratoire était de concevoir une application de clavardage 
client-serveur, soit de permettre à plusieurs clients d’échanger des messages via un serveur. Ce 
laboratoire a été utile afin d’approfondir notre compréhension des sockets et des threads. Ce travail 
pratique nous a aussi permis de mieux comprendre comment deux machines peuvent échanger de 
l’information via un serveur. Nous avons appris à utiliser les threads pour permettre plusieurs 
échanges entre machine et tout cela en temps réel. Nous comprenons mieux maintenant comme 
nous pouvons utiliser un réseau pour permettre d’instaurer un système de clavardage
