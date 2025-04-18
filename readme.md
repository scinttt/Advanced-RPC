# Basic Outline

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

![image.png](attachment:d83c8c06-dbb2-43b3-a3e2-15ae191aa2fc:image.png)

Service Cache Update - Listening Machenism

![image.png](attachment:9a89fc9b-07f7-4a16-b7fd-0330dc674e0a:image.png)

## RPC-CORE

Define Config, Constant, Exception, Fault, Load balancer, model, protocol, proxy, Local Registry, Serializer, Server, SPI, Utils

![image.png](attachment:5bde8dc9-e4b1-4550-a864-f831547127ea:image.png)

# Advanced features:

## Global Configuration

1. Define a default config entity

    ```java
    @Data
    public class RpcConfig {
    
        /**
         * 名称
         */
        private String name = "yu-rpc";
    
        /**
         * 版本号
         */
        private String version = "1.0";
    
        /**
         * 服务器主机名
         */
        private String serverHost = "localhost";
    
        /**
         * 服务器端口号
         */
        private Integer serverPort = 8080;
    
        /**
         * 序列化器
         */
        private String serializer = SerializerKeys.JDK;
    
        /**
         * 负载均衡器
         */
        private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    
        /**
         * 重试策略
         */
        private String retryStrategy = RetryStrategyKeys.NO;
    
        /**
         * 容错策略
         */
        private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
    
        /**
         * 模拟调用
         */
        private boolean mock = false;
    
        /**
         * 注册中心配置
         */
        private RegistryConfig registryConfig = new RegistryConfig();
    }
    
    ```

2. Define how to load config

    ```java
    public class ConfigUtils {
    
        /**
         * 加载配置对象
         *
         * @param tClass
         * @param prefix
         * @param <T>
         * @return
         */
        public static <T> T loadConfig(Class<T> tClass, String prefix) {
            return loadConfig(tClass, prefix, "");
        }
    
        /**
         * 加载配置对象，支持区分环境
         *
         * @param tClass
         * @param prefix
         * @param environment
         * @param <T>
         * @return
         */
        public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
            StringBuilder configFileBuilder = new StringBuilder("application");
            if (StrUtil.isNotBlank(environment)) {
                configFileBuilder.append("-").append(environment);
            }
            configFileBuilder.append(".properties");
            Props props = new Props(configFileBuilder.toString());
            return props.toBean(tClass, prefix);
        }
    }
    ```

3. Maintain the global configuration object

```java
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
        // 创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}

```

## Mock Interface

1. Add field “mock” in the RPC Config

    ```java
    @Data
    public class RpcConfig {
        ...
        
        /**
         * 模拟调用
         */
        private boolean mock = false;
    }
    
    ```

2. Add MockServiceProxy

    ```java
    @Slf4j
    public class MockServiceProxy implements InvocationHandler {
    
        /**
         * 调用代理
         *
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 根据方法的返回值类型，生成特定的默认值对象
            Class<?> methodReturnType = method.getReturnType();
            log.info("mock invoke {}", method.getName());
            return getDefaultObject(methodReturnType);
        }
    
        /**
         * 生成指定类型的默认值对象（可自行完善默认值逻辑）
         *
         * @param type
         * @return
         */
        private Object getDefaultObject(Class<?> type) {
            // 基本类型
            if (type.isPrimitive()) {
                if (type == boolean.class) {
                    return false;
                } else if (type == short.class) {
                    return (short) 0;
                } else if (type == int.class) {
                    return 0;
                } else if (type == long.class) {
                    return 0L;
                }
            }
            // 对象类型
            return null;
        }
    }
    
    ```

3. Add getMockProxy method in proxyFactory

    ```java
    
    public class ServiceProxyFactory {
    
        /**
         * 根据服务类获取代理对象
         *
         * @param serviceClass
         * @param <T>
         * @return
         */
        public static <T> T getProxy(Class<T> serviceClass) {
            if (RpcApplication.getRpcConfig().isMock()) {
                return getMockProxy(serviceClass);
            }
    
            return (T) Proxy.newProxyInstance(
                    serviceClass.getClassLoader(),
                    new Class[]{serviceClass},
                    new ServiceProxy());
        }
    
        /**
         * 根据服务类获取 Mock 代理对象
         *
         * @param serviceClass
         * @param <T>
         * @return
         */
        public static <T> T getMockProxy(Class<T> serviceClass) {
            return (T) Proxy.newProxyInstance(
                    serviceClass.getClassLoader(),
                    new Class[]{serviceClass},
                    new MockServiceProxy());
        }
    }
    ```

4. Determine getServiceProxy or getMockProxy based on configuration file

    ```java
    RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
    
    UserService userService = null;
    
    if(rpc.isMock()){
        userService = ServiceProxyFactory.getMockProxy(UserService.class);
    }else{
        userService = ServiceProxyFactory.getProxy(UserService.class);
    }
    ```


## SPI & Serializer

Load Different Serializer through configuration file through SPI Loader

1. **Implement different Serializer**
    1. Jdk
    2. Json
    3. Hessian
    4. Kyro
2. **Register SPI**
    1. SPI Loader will automatically load: resources/META-INF/services

        ```yaml
        jdk=com.creaturelove.serializer.JdkSerializer
        ```

    2. META-INF/rpc/custom/com.creaturelove.serializer.Serializer
        1. User can add customized implementation class
    3. META-INF/rpc/system/com.creaturelove.serializer.Serializer

        ```yaml
        jdk=com.creaturelove.serializer.JdkSerializer
        hessian=com.creaturelove.serializer.HessianSerializer
        json=com.creaturelove.serializer.JsonSerializer
        kryo=com.creaturelove.serializer.KryoSerializer
        ```

3. **Update Serializer Factory, create Serializer through SPILoader**

    ```java
    public class SerializerFactory {
    
        static{
            SpiLoader.load(Serializer.class);
        }
    
        // old serializer map without SPI Loader
    //    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
    //        put(SerializerKeys.JDK, new JdkSerializer());
    //        put(SerializerKeys.JSON, new JsonSerializer());
    //        put(SerializerKeys.KRYO, new KryoSerializer());
    //        put(SerializerKeys.HESSIAN, new HessianSerializer());
    //    }};
    
        public static Serializer getInstance(String key){
            return SpiLoader.getInstance(Serializer.class, key);
        }
    }
    ```

4. **Customize serializer in the [application.properties](http://application.properties) in both consumer & producer**

    ```yaml
    rpc.serializer=hessian
    ```


## Service Register(Etcd)

1. Install server

    ```yaml
    docker run -d \
      --name etcd \
      --hostname etcd \
      -p 2379:2379 -p 2380:2380 \
      quay.io/coreos/etcd:v3.5.13 \
      /usr/local/bin/etcd \
      --name my-etcd \
      --data-dir /etcd-data \
      --listen-client-urls http://0.0.0.0:2379 \
      --advertise-client-urls http://etcd:2379 \
      --listen-peer-urls http://0.0.0.0:2380 \
      --initial-advertise-peer-urls http://etcd:2380 \
      --initial-cluster my-etcd=http://etcd:2380 \
      --initial-cluster-state new \
      --initial-cluster-token etcd-cluster-1
    ```


port 2379: provide HTTP API service, interact with etcdctl

port 2380: communication within the cluster

1. Jetcd is the java client of etcd
2. Demo

    ```java
    public class EtcdRegistry {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            // create client using endpoints
            Client client = Client.builder().endpoints("http://localhost:2379")
                    .build();
    
            KV kvClient = client.getKVClient();
            ByteSequence key = ByteSequence.from("test_key".getBytes());
            ByteSequence value = ByteSequence.from("test_value".getBytes());
    
            // put the key-value
            kvClient.put(key, value).get();
    
            // get the CompletableFuture
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);
    
            // get the value from CompletableFuture
            GetResponse response = getFuture.get();
    
            // delete the key
            kvClient.delete(key).get();
        }
    }
    
    ```

3. Set ServiceMetaData Model

    ```java
    @Data
    public class ServiceMetaInfo {
        private String serviceName;
    
        private String serviceVersion = "1.0";
    
        private String serviceHost;
    
        private Integer servicePort;
    
        private String serviceGroup = "default";
    
        public String getServiceKey(){
            return String.format("%s:%s", serviceName, serviceVersion);
        }
    
        public String getServiceNodeKey(){
            return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
        }
    
        public String getServiceAddress(){
            if(!StrUtil.contains(serviceHost, "http")){
                return String.format("http://%s:%s", serviceHost, servicePort);
            }
            return String.format("%s:%s", serviceHost, servicePort);
        }
    }
    ```

4. Setup RegistryConfig in the config package

    ```java
    @Data
    public class RegistryConfig {
        private String registry = "etcd";
    
        private String address = "http://localhost:2380";
    
        private String username;
    
        private String password;
    
        private Long timeout = 10000L;
    }
    ```

5. Registry Interface & Registry Implementation

    ```java
    public interface Registry {
    
        // Initialization
        void init(RegistryConfig registryConfig);
    
        // Register Service
        void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    
        // Unregister Service
        void unRegister(ServiceMetaInfo serviceMetaInfo);
    
        // ServiceDiscovery
        List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
    
        // Service Destroy
        void destroy();
    }
    
    ```

    ```java
    public class EtcdRegistry implements Registry {
        private Client client;
    
        private KV kvClient;
    
        private static final String ETCD_ROOT_PATH = "/rpc/";
    
        @Override
        public void init(RegistryConfig registryConfig){
            client = Client.builder()
                    .endpoints(registryConfig.getAddress())
                    .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                    .build();
            kvClient = client.getKVClient();
        }
    
        @Override
        public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{
            // create a lease client
            Lease leaseClient = client.getLeaseClient();
    
            // create a 30s lease
            long leaseId = leaseClient.grant(30).get().getID();
    
            // store key-value pairs
            String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
            ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
            ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
    
            // connect the key-value pair with the lease, and set TTL
            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
            kvClient.put(key, value, putOption).get();
        }
    
        public void unRegister(ServiceMetaInfo serviceMetaInfo){
            kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        }
    
        public List<ServiceMetaInfo> serviceDiscovery(String serviceKey){
            String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
    
            try{
                //prefix search
                GetOption getOption = GetOption.builder().isPrefix(true).build();
                List<KeyValue> keyValues = kvClient.get(
                        ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                        getOption)
                        .get()
                        .getKvs();
                // parse service info
                return keyValues.stream()
                        .map(keyValue -> {
                            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                            return JSONUtil.toBean(value, ServiceMetaInfo.class);
                        })
                        .collect(Collectors.toList());
            }catch(Exception e){
                throw new RuntimeException("Failed to retrieve the service list", e);
            }
        }
    
        public void destroy(){
            System.out.println("Current Node Destroy");
    
            // Release resources
            if(kvClient != null){
                kvClient.close();
            }
            if(client != null){
                client.close();
            }
        }
    }
    
    ```

6. Registry Factory

    ```java
    public class RegistryFactory {
        static{
            SpiLoader.load(Registry.class);
        }
    
        private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();
    
        public static Registry getInstance(String key){
            return SpiLoader.getInstance(Registry.class, key);
        }
    }
    ```

7. ServiceProxy call provider through Registry

    ```java
     // get provider address from registry center
      RpcConfig rpcConfig = RpcApplication.getRpcConfig();
      Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
      serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
    
      List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
    
      if(CollUtil.isEmpty(serviceMetaInfoList)){
          throw new RuntimeException("service address temporarily doesn't exist");
      }
    
      // get first one
      ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
      
      // Provider Address
      String providerAddress = selectedServiceMetaInfo.getServiceAddress();
    ```

8. Provider register the service to the Registry

    ```java
    // register the service to Local Registry
    String serviceName = UserService.class.getName();
    LocalRegistry.register(serviceName, UserServiceImpl.class);
    
    // register the service to Registry Center
    RpcConfig rpcConfig = RpcApplication.getRpcConfig();
    RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
    Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName(serviceName);
    serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
    serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
    
    try{
        registry.register(serviceMetaInfo);
    }catch(Exception e){
        throw new RuntimeException(e);
    }
    
    ```


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
3. Implement RpcProviderBootstrap
    1. Retrieve all the class annotated with @RpcService
        1. make the Bootstrap class implements the BeanPostProcessor interface and override postProcessAfterInitialization method
    2. Retrieve the information of those classes through reflection, then finish the service registration
4. Implement RpcConsumerBootstrap

   Retrieve all the attributes of the bean after bean initialization through Reflection just like provider. If the field contains @RpcReference, generate the proxy object and assign the value for that field.

5. Import the bootstrap class into @EnableRpc

    ```java
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
    public @interface EnableRpc {
    
        // need to start server
        boolean needServer() default true;
    }
    ```


# Future Expansion

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
    1. Implement cache with Caffeine