package org.lognet.springboot.grpc.autoconfigure.eureka;

import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.lognet.springboot.grpc.GRpcServerRunner;
import org.lognet.springboot.grpc.autoconfigure.GRpcAutoConfiguration;
import org.lognet.springboot.grpc.autoconfigure.OnGrpcServerEnabled;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindHandlerAdvisor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
@Configuration
@ConditionalOnClass({EurekaServiceRegistry.class})
@AutoConfigureAfter({EurekaClientAutoConfiguration.class, GRpcAutoConfiguration.class})
@ConditionalOnProperty(value = {"spring.cloud.service-registry.auto-registration.enabled"}, matchIfMissing = true)
@ConditionalOnBean({EurekaServiceRegistry.class, GRpcServerRunner.class})
@OnGrpcServerEnabled
@EnableConfigurationProperties({GrpcEurekaProperties.class})
public class EurekaGrpcAutoConfiguration {

    @Bean
    public ConfigurationPropertiesBindHandlerAdvisor configurationPropertiesBindHandlerAdvisor() {
        return bindHandler -> new AbstractBindHandler(bindHandler) {
            private final ConfigurationPropertyName gprcConfigName = ConfigurationPropertyName.of("grpc.eureka");
            @Override
            public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
                if (gprcConfigName.equals(name)) {
                    EurekaInstanceConfigBean result = context.getBinder().bindOrCreate("eureka.instance", EurekaInstanceConfigBean.class);
                    GrpcEurekaProperties p = (GrpcEurekaProperties) target.getValue().get();
                    p.setDiscovery(result);
                }
                return super.onStart(name, target, context);
            }
        };
    }

    @Bean
    public GrpcEurekaRegistar grpcEurekaRegistar(/*EurekaServiceRegistry eurekaServiceRegistry*/) {
        return new GrpcEurekaRegistar();
    }
}
