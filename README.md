Multi-hop Train Journey Booking
===============================

Individual Programming Project for Mobile and Ubiquitous Computing

# Compile data

```java -jar NetworkCompiler.jar stations.json routes.json compiled.json```

# How to run with supplied data

Run the following commands, in order

## Dynamic Data Cluster - Data Store

Each dynamic data cluster required __1__ data store
```java -jar DynamicDataCluster.jar "data-store" <filename> theCompany```

## Dynamic Data Cluster - Master

Each dynamic data cluster required __1__ master node
```java -jar DynamicDataCluster.jar "master" <data store location> theCompany```

## Dynamic Data Cluster - Slave

Each dynamic data cluster can run with any number of slave nodes (at least 1 required)
```java -jar DynamicDataCluster.jar <master location>```

## Static Data Cluster - Master

The system requires __1__ static data cluster. The cluster itself requires __1__ master node
```java -jar StaticDataCluster.jar master compiled.json companies.json```

## Static Data Cluster - Slave
The statuc data cluster can run with any number of slaves, at least 1 required
```java -jar StaticDataCluster.jar <master location>```

## Client
The system may run with any number of clients
```java -jar Client.jar <static master location>```

