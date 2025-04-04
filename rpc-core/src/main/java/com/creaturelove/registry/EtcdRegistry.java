package com.creaturelove.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.creaturelove.config.RegistryConfig;
import com.creaturelove.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {
    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    @Override
    public void init(RegistryConfig registryConfig){
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();

        // Enable Health Check
        heartBeat();
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

        // Add node info to local cache
        localRegisterNodeKeySet.add(registerKey);
    }

    public void unRegister(ServiceMetaInfo serviceMetaInfo){
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));

        //Remove from local cache
        localRegisterNodeKeySet.remove(registerKey);
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

    @Override
    public void heartBeat(){
        // continue every 10s
        CronUtil.schedule("*/10 * * * * *", new Task(){
            @Override
            public void execute(){
                for(String key : localRegisterNodeKeySet){
                    try{
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // node is expired(need restart to reRegister)
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }
                        // node is not expired, reRegister
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    }catch(Exception e){
                        throw new RuntimeException(key + "failed to continue the lease", e);
                    }
                }
            }
        });

        // support second level cronjob
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        // create client using endpoints
//        Client client = Client.builder().endpoints("http://localhost:2379")
//                .build();
//
//        KV kvClient = client.getKVClient();
//        ByteSequence key = ByteSequence.from("test_key".getBytes());
//        ByteSequence value = ByteSequence.from("test_value".getBytes());
//
//        // put the key-value
//        kvClient.put(key, value).get();
//
//        // get the CompletableFuture
//        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
//
//        // get the value from CompletableFuture
//        GetResponse response = getFuture.get();
//
//        // delete the key
//        kvClient.delete(key).get();
//    }
}
