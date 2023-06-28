package cn.jiefly.config;

import cn.jiefly.service.HelloService;
import cn.jiefly.service.impl.HelloServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class DubboProvider {
    static {
        PropertyConfigurator.configure(DubboProvider.class.getClassLoader().getResource("log4j.properties"));
    }

    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("jiefly-dubbo");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://39.108.181.37:2181");
        ServiceConfig<HelloService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setInterface(HelloService.class);
        serviceConfig.setRef(new HelloServiceImpl());
        serviceConfig.export();
        System.out.println("Dubbo provider started.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
