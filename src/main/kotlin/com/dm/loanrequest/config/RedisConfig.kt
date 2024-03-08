package com.dm.loanrequest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer

@Configuration
open class RedisConfig {
    @Bean
    open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<*, *> {
        val template = RedisTemplate<Any, Any>()
        template.connectionFactory = redisConnectionFactory
        template.valueSerializer = GenericToStringSerializer(Any::class.java)
        return template
    }
}
