package com.fc.sns.configuration;

import com.fc.sns.model.UserDto;
import io.lettuce.core.RedisURI;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * 캐싱 고려사항
 * 1. 데이터의 변경이 너무많이 일어나는 데이터는 캐싱의 의미가 없다. 데이터 변경이 일어나면 캐싱된 데이터도 eviction(퇴거, 축출) 시키고 새로이 변경된 데이터로 캐싱해 주어야 한다.
 * 2. 접근이 많은 데이터를 캐싱할수록 DB 부하가 적어지기 때문에 접근이 자주되는 데이터를 캐싱해두면 캐싱의 이점을 더 크게 볼수 있다.
 * 3. 여기서 가장 접근이 많은 데이터는 User 이다. (매 API 마다 User 테이블의 user 정보에 접근하고 있음)
 * 3-1. 또한 User 데이터의 경우 현재 스펙상 수정이 불가능.
 * 3-2. 수정이 이루어지는 스팩이 추가가 되더라도 post 처럼 빈번하게 수정이 이루어지는 데이터의 성질이 아니므로 User 를 캐싱한다.
 * <p>
 * 캐싱할 포맷은 Entity 보다는 Dto 가 낫다.
 */
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = getRedisURI();// connectionFactory(configruation(url))
        RedisConfiguration redisConfiguration = LettuceConnectionFactory.createRedisConfiguration(redisURI);// redis 와 application 을 연결시켜주는 ConnectionFactory 라이브러리; jedis(옛버전), lettuce(최신에 나온 라이브러리; 주로사용)
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration);
        factory.afterPropertiesSet();// initialize = true
        return factory;
    }

    private RedisURI getRedisURI() {
        if (StringUtils.hasText(redisProperties.getUrl())) {
            return RedisURI.create(redisProperties.getUrl());
        }
        return RedisURI.create(redisProperties.getHost(), redisProperties.getPort());
    }


    @Bean
    RedisTemplate<String, UserDto> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);//레디스 서버 커넥션 설정 정보를 세팅하는 메서드
        redisTemplate.setKeySerializer(new StringRedisSerializer());//key 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserDto>(UserDto.class));//value 직렬화
        //redisTemplate.opsForValue().set();//기본 get set 할 수 있도록 도와주는 메서드
        return redisTemplate;
    }
}
