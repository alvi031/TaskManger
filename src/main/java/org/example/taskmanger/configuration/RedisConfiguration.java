package org.example.taskmanger.configuration;

import org.example.taskmanger.database.dto.TaskReadDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisConfiguration {

@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

    JsonMapper mapper = JsonMapper.builder().build();

    JavaType listType = mapper.getTypeFactory()
            .constructCollectionType(List.class, TaskReadDto.class);

    JacksonJsonRedisSerializer<List<TaskReadDto>> listSerializer =
            new JacksonJsonRedisSerializer<>(mapper, listType);

    JacksonJsonRedisSerializer<TaskReadDto> taskSerializer =
            new JacksonJsonRedisSerializer<>(mapper, TaskReadDto.class);

    RedisCacheConfiguration tasksListConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(listSerializer)
                    );

    RedisCacheConfiguration taskConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(taskSerializer)
                    );

    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
            .withInitialCacheConfigurations(
                    Map.of(
                            "tasksList", tasksListConfiguration,
                            "task", taskConfiguration
                    )
            )
            .build();
}
}