package com.creaturelove;

import com.creaturelove.registry.LocalRegistry;
import com.creaturelove.server.HttpServer;
import com.creaturelove.server.VertxHttpServer;
import com.creaturelove.service.UserService;
import com.creaturelove.serviceImpl.UserServiceImpl;

/**
 * Hello world!
 *
 */
public class ProviderServer
{
    public static void main( String[] args )
    {
        //RPC Initialization
        RpcApplication.init();

        // register the service
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // start http server at provider side
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
