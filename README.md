# Descritpion du projet
Dans le cadre du cours de développement d'application internet (API) suivi à la HEIG-VD d'Yverdon-les-bains, nous avons 
été amené au cours du projet-ci à développer une application client envoyant des emails forgés à un serveur mail mocké 
via le protocole SMTP. La contrainte majeure étant que la communication client-serveur est faite exclusivement via 
l'utilisation de sockets afin de mieux comprendre le protocole SMTP en particulier les différentes commandes principales 
permettant l'envoie de d'emails, ainsi que les différentes contraintes liées à l'encodage. Ce projet a également permis 
d'affirmer notre compréhension de la gestion de la lecture et écriture dans des fichiers externes.

# Qu'est-ce que MockMock ?
Puisque nous ne développons qu'un client. Nous avons besoin d'un serveur mail mocké sur lequel nous pouvons réaliser
notre expérience en tout impunité. Pour ce faire, nous utilisons un projet Github externe appelé 
[MockMock](https://github.com/tweakers/MockMock). Ce repo met à disosition un exécutable JAR lançant un serveur mail SMTP
en local. Ce dernier ouvrira donc un port accessible via telnet en localhost permettant de communiquer directement avec
le serveur SMTP en plus d'un autre port permettant l'ouverture d'une interface graphique web où seront visible l'ensemble
des mails reçu par le serveur.

# Installation

## Pré-requis
Avant de pouvoir commencer à créer votre propre campagne de mails forgés, vous aurez besoin des outils suivant:
- Docker Desktop
- Version de JDK 18 ou supérieure
- Maven 3.8.6

Nous vous laissons regarder sur l'internet afin d'installer correctement ces outils :)

## Installtion du serveur SMTP
L'installation de la partie serveur de ce projet se fera via Docker. Nous partons du principe que l'installation de ce logiciel est fonctionnelle.<br>
Docker est un logiciel de conteneurisation permettant d'empacté un logiciel ainsi que l'ensemble de ses dépendance dans
un contenant qui peut facilement s'exporter sur divers machine indépendamment de leur système d'exploitation. 

## Installation de EmailPranker
# Implémentation
