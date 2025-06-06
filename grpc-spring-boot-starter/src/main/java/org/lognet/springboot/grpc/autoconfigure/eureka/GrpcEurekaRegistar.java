package org.lognet.springboot.grpc.autoconfigure.eureka;

import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.*;

public class GrpcEurekaRegistar implements SmartLifecycle {
    public static final String GPPC_PORT = "grpc.port";
    private List<EurekaRegistration> registrations;

    @EventListener
    public void onGrpcServerStarted(GRpcServerInitializedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        EurekaInstanceConfigBean eurekaInstanceConfigBean = applicationContext.getBean(GrpcEurekaProperties.class).getDiscovery();
        GRpcServerProperties gRpcServerProperties = applicationContext.getBean(GRpcServerProperties.class);
        EurekaRegistration registration = applicationContext.getBean(EurekaRegistration.class);
        //metadata from eureka.instance and gprc.eureka.discovery will override for as they share the same instance.
        Map<String, String> metadataMap = registration.getInstanceConfig().getMetadataMap();
        metadataMap.putAll(eurekaInstanceConfigBean.getMetadataMap());
        Integer port = gRpcServerProperties.getPort();
        if (port == null || port == 0) {
            metadataMap.put(GPPC_PORT, String.valueOf(gRpcServerProperties.getRunningPort()));
        } else {
            metadataMap.put(GPPC_PORT, String.valueOf(gRpcServerProperties.getPort()));
        }
        System.out.println(registration.getMetadata());
        this.registrations = new ArrayList<>();
        this.registrations.add(registration);
        List<EurekaRegistration> var10000 = this.registrations;
        var10000.forEach(reg -> reg.getEurekaClient().getApplicationInfoManager().registerAppMetadata(metadataMap));
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        this.registrations = null;
    }

    @Override
    public boolean isRunning() {
        return null != this.registrations;
    }
}
