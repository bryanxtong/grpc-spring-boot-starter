package org.lognet.springboot.grpc.autoconfigure.eureka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

@ConfigurationProperties(prefix = "grpc.eureka")
public class GrpcEurekaProperties {

    @NestedConfigurationProperty
    private EurekaInstanceConfigBean discovery;

    public EurekaInstanceConfigBean getDiscovery() {
        return discovery;
    }

    public void setDiscovery(final EurekaInstanceConfigBean discovery) {
        this.discovery = discovery;
    }
}
