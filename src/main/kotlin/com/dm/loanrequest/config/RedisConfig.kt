package com.dm.loanrequest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.math.BigDecimal

@Configuration
open class RedisConfig {
    @Bean
    open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, BigDecimal> {
        val template = RedisTemplate<String, BigDecimal>()
        template.connectionFactory = redisConnectionFactory
        // Set the string serializer for the keys
        template.keySerializer = StringRedisSerializer()
        // Use a serializer that can handle BigDecimal for the values
        template.valueSerializer = GenericToStringSerializer(BigDecimal::class.java)
        return template
    }

}
