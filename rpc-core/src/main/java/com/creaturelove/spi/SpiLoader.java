package com.creaturelove.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.creaturelove.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    // Store Loaded Class
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    // object instance cache
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    // System SPI Catalog
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    // User Customized SPI Catalog
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    // Scan Route
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    // Dynamic Loaded Class list
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    // Load all the types
    public static void loadAll(){
        log.info("Load all SPI");
        for(Class<?> aClass: LOAD_CLASS_LIST){
            load(aClass);
        }
    }

    public static <T> T getInstance(Class<?> tClass, String key){
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if(keyClassMap == null){
            throw new RuntimeException(String.format("SpiLoader unloaded %s type", tClassName));
        }
        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader's %s doesn't have key=%s type", tClassName, key));
        }

        // Retrieve the implementation type
        Class<?> implClass = keyClassMap.get(key);

        // load specific object from instance cache
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try{
                instanceCache.put(implClassName, implClass.newInstance());
            }catch (InstantiationException | IllegalAccessException e){
                String errorMsg = String.format("Instantiation failed for %s class", implClassName);
            }
        }

        return (T) instanceCache.get(implClassName);
    }

    public static Map<String, Class<?>> load(Class<?> loadClass){
        log.info("Load SPI for type {}", loadClass.getName());

        // Scan path, User custom is prioritized to system SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();

        for(String scanDir : SCAN_DIRS){
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());

            //Read each resource file
            for(URL resource : resources){
                try{
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        String[] strArray = line.split("=");
                        if(strArray.length > 1){
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                }catch(Exception e){
                    log.error("spi resource load error", e);
                }
            }
        }

        loaderMap.put(loadClass.getName(), keyClassMap);

        return keyClassMap;
    }
}
