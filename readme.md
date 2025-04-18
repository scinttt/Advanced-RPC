# Overview
This RPC Framework is based on Java + Etcd + Vert.x + Customized Protocol.  
This allows the developer to call the remote service using a local method through an annotation and a configuration file.  
Also support Serializer, Loadbalancer, Retry Strategy, and Fault Tolerant Strategy.

## Consumer

Call the service

## Provider

Provide Service Implementation

## Common

Provide Data Entities and Service Interfaces

## Registry Center

**Features:**

1. Distributed Data Center:

   Register information to data center for storage, retrieval and sharing

2. Service Registration
3. Service Discovery
4. Health Check
5. Services Removal

**Distributed Data Center:**

- Store and Retrieve data.
- Allow to listen or expire the data for removing dead node and updating node list, etc.
- High availability, high stability, high reliability, data consistency
- Tools:
    - ZooKeeper
    - Redis
    - Etcd

**Jtcd(Java client of etcd):**

1. **kvClient**：

   Manipulate the Key-Value pair in the etcd.

2. **leaseClient**：
    1. Manage the lease(TTL) for the Key-Value Pairs
    2. CRUD for the lease
3. **watchclient：**

   Listen the change of key in the etcd

4. **clusterClient：**
    1. Manipulate the node of the etcd cluster
    2. Election
    3. Healthcheck
5. **authClient**：
    1. Manage the Authentication and Authorization of the etcd.
    2. CRUD the user and role through authClient
6. **lockclient**：
    1. CRUD the lock through the lockClient.
    2. Used to implement distributed lock
7. **electionClient**：
    1. Used to implement distributed election
    2. Create, submit, monitor the electio

RegistryCenter Architecture:

![RegistryCenter.png](static%2Fpicture%2FRegistryCenter.png)
Service Cache Update - Listening Machenism

![ListeningMechanism.png](static%2Fpicture%2FListeningMechanism.png)
## RPC-CORE

Define Config, Constant, Exception, Fault, Load balancer, model, protocol, proxy, Local Registry, Serializer, Server, SPI, Utils

![RpcArchitecture.png](static%2Fpicture%2FRpcArchitecture.png)
# Advanced features:

## Global Configuration
Help our RPC framework easily get configuration from a global configuration object
Global Configuration Contains: 
- name
- version
- serverHost
- serverPort
- ...
   
## Mock Interface
When we can not access real remote service, mock service help us do the development, testing and debuging.

## SPI & Serializer

Load a Different Serializer through the configuration file through the SPI Loader
SPI Mechanism allow provider register its implementation to the system through specific configuration file and reflection, without the change of the original code, which improves the modularity and expansibility.

1. JDK Serializer
2. JSON Serializer
3. Hessian Serializer
4. Kryo Serializer
5. Protobuf Serializer


## Service Register(Etcd)
Features:
1. Distributed data storage
2. Service Registration
3. Service Discovery
4. Health Check
5. Service Destroy

Etcd:
1. Etcd is a distributed key-value pair database based on Go.
2. Mainly used for the service discovery, configuration management, and distributed lock.
3. Ensure data Consistency through Raft Alogrithm
   
## Registry Optimization

1. HeartBeat Check Mechanism
    1. Timed
    2. Internet Request
    3. Implement HeartBeat Check with Etcd
        1. Provider register its services to Registry Center and set TTL
        2. Etcd will maintain the service and delete it after TTL
        3. Provider regularly renew the registration TTL
2. Service Node Destroy
    1. Active Offline
       provider exit the service and delete it from regitry center
    2. Passive Offline
       when there is an exception and exit the service, we deelte the service through Etcd’s key expiration mechanism

3. Add consumer Service Cache
   Cache the service into consumer to improve the performance.
    1. Add local cache
    2. Use local cache
    3. Listen the change of Service Registry and update the cache

## Customized Protocol
1. Protocol Message Design
    1. Magic Number
        1. security verification (Like Https Certification)
    2. Version number
        1. Ensure the consistency of request and response
    3. Serializer
        1. Tell the consumer and the client how to parse the data (like Content-type of HTTP)
    4. Type
        1. Request or Response?
    5. Status
        1. Status result (like HTTP Status Code)
    6. body
2. Internet Transition
    1. Vertx Tcp Client and Vertx Tcp Server
3. Implement Encoder and Decoder with the Serializer
4. Implemenet TcpServerHandler
5. Semi-Packet & Sticky Packet Problems
    1. Semi-Packet: Received data is less than original data.
        1. Set the request body length in the request header. If the received data length doesn’t equals to request body length then throw Exception, hold it until next time.
    2. Sticky Packet: Received data is more than original data
        1. Set the request body length in the request header. Read excessive part until next time.
    3. Solution with Vert.x through RecordParser

## LoadBalancer

1. Multiple Load Balancer
    1. Round-Robin
    2. Random
    3. Consistent Hash
2. Apply with SpiLoader

## Retry Strategy

1. Multiple Retry Strategy with Guava-Retrying
    1. No-Retry
    2. Fixed-Interval-Retry
2. Encapsulate the request into a Callable interface as the retryer’s parameter

## Fault Tolerant Strategy

1. **Multiple Fault Tolerant Strategy**
    1. Fail-Over:

       When the request of a node fails, switch to a node and call again

    2. Fail-Back:

       When there is an error, find a way like downgrade/retry/call other service to recover it.

    3. Fail-Safe:

       When there is an error, ignore it. Like nothing happens.

    4. Fail-Fast:

       When there is an error, report immediately to outsider to handle it.

2. **Solution:**
    1. Retry
    2. Rate-limiting
    3. Fallback
    4. Circuit-Breaker
    5. Timed-out
3. **Retry + Fault Tolerance**
    1. When there is an error, we retry it to solve some temporary problem like Network Fluctuation or Server Temporarily Unavailable
    2. If it still fails after multiple retrying, other fault-tolerant strategy will be triggered like fail-back, circuit-breaker, rate-limiting to reduce the influence of the error.

## Boot Strap + Annotation

1. Encapsulate the initialization of the provider and consumer
2. Define the Annotation
    1. @EnableRpc
    2. @RpcService
    3. @RpcReference
# Upcomming Features:
1. Add RequestParameter List in RpcRequest
    1. Provider Parameter List
    2. Consumer Parameter List
        1. Provider can determine the next step according to the reuqest parameter list
        2. Like implement security verification based on the token in the parameter list
2. Develop Service Management Dashboard
    1. Like Nacos Registry Center Dashboard
3. Support multiple type configuration file as global configuration like yml/yaml
    1. SPI mechanism allow developer expand configuration parser
4. Interceptor
   Add execution before or after the service call
    1. Log verification
    2. Security verification
5. Customized Exception
   Make the error message clearer
    1. Customize RpcException according to ErrorCode
    2. ConsumerException
    3. RegistryCenterException
    4. ProviderException
6. Service support specific version number
7. Allow consumer choose service-level loadbalancer/retrystrategy/faultoleracestrategy
    1. Dynamic create serviceproxy basd on the consumer configuration
8. Service Group
9. Consumer set timeout
10. Handle Bean Injection Problem
    1. Local Registry need to put the instance of implementation class but not the class type
11. Stress Testing
12. Optimize the Cache Mechanism of Service Registry
    1. Distinguish the service key, use a map to maintain multiple service information
13. Add TTL for Service Registration, refresh the cache regularly
    1. Implement the cache with Caffeine
