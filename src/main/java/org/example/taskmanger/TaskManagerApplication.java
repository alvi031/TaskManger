package org.example.taskmanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class TaskManagerApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(TaskManagerApplication.class, args);
        System.out.println("Count: " + run.getBeanDefinitionCount());
    }

}
