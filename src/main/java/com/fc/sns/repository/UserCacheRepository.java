package com.fc.sns.repository;

import com.fc.sns.configuration.filter.JwtTokenFilter;
import com.fc.sns.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 1. key 를 어떻게 정의해야 할까?
 * User 를 캐싱하기로 했을 때, 가장 user 를 많이 사용하는 곳? {@link JwtTokenFilter} 에서 user 가 있는지 없는지 체크를 하기 때문에, 그부분을 캐싱으로 대체하려고 함.
 * 그 부분에 search key 는 username
 * <p>
 * 2. expired time
 * 유저가 캐싱한 후 앞으로 이 사이트를 이용하지 않게 되면,
 * expired time 을 세팅해주지 않았을 때,
 * 이용되지 않는 데이터가 캐시에 영구히 저장되어있게 되므로 캐시 저장소의 공간활용에 좋지 않다.(금방 가득찰 수 있음)
 * 3일을 예시로 본다면,
 * 3일이 지난 후 expire 된 로그인 유저 정보는,
 * 다시 로그인 할 유저가 접근할 때 db 의 정보를 select 하여 가져와서 캐싱하면 된다.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {
    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUserDto(UserDto userDto) {
        String key = getKey(userDto.getUsername());
        log.info("Set User to Redis {} , {}", key, userDto);
        userDtoRedisTemplate.opsForValue().set(key, userDto, USER_CACHE_TTL);
    }

    public Optional<UserDto> getUserDto(String userName) {
        String key = getKey(userName);
        UserDto userDto = userDtoRedisTemplate.opsForValue().get(key);
        log.info("Get User to Redis {} , {}", key, userDto);
        return Optional.ofNullable(userDto);
    }

    private String getKey(String username) {
        // prefix 붙이는 이유: 현재는 캐싱을 UserDto 대상으로만 하지만, 나중에 다른 자원이 캐싱대상이 되었을때는 key 로만 구분하면 중복될수 있다. prefix:key 형식을 채택한다.
        return new StringBuffer().append("USER:").append(username).toString();
    }


}
