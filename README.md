Multi-hop Train Journey Booking
===============================

Individual Programming Project for Mobile and Ubiquitous Computing

See `Report.pdf` for more information.

Under MIT License, see `LICENSE` for more information.

Repository public since Thursday, 19th of December 2013 (_13 days past demonstration and submission date of 6 December 2013_).

## Compile data

```java -jar NetworkCompiler.jar stations.json routes.json compiled.json```

## How to run with supplied data

Run the following commands in order...

#### Dynamic Data Cluster - Data Store

Each dynamic data cluster requires __1__ data store

```java -jar DynamicDataCluster.jar "data-store" <filename> theCompany```

#### Dynamic Data Cluster - Master

Each dynamic data cluster requires __1__ master node

```java -jar DynamicDataCluster.jar "master" <data store location> theCompany```

#### Dynamic Data Cluster - Slave

Each dynamic data cluster can run with any number of slave nodes (at least __1__ required)

```java -jar DynamicDataCluster.jar <master location>```

#### Static Data Cluster - Master

The system requires __1__ static data cluster. The cluster itself requires __1__ master node

```java -jar StaticDataCluster.jar master compiled.json companies.json```

#### Static Data Cluster - Slave
The static data cluster can run with _any_ number of slaves, at least __1__ required

```java -jar StaticDataCluster.jar <master location>```

#### Client
The system may run with _any_ number of clients

```java -jar Client.jar <static master location>```

