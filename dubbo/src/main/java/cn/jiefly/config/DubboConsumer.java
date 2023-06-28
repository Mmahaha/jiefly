package cn.jiefly.config;

import cn.jiefly.service.HelloService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

public class DubboConsumer {
    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("jiefly-dubbo");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://39.108.181.37:2181");

        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setInterface(HelloService.class);
        HelloService helloService = referenceConfig.get();
        System.out.println(helloService.sayHello("abc"));
    }
}
