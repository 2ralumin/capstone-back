package capstone.be.global.jwt;
import capstone.be.global.config.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


//  권한이나 인증이 필요한 특정 주소 요청했을 때 BasicAuthenticationFilter를 거친다. JWT 검증

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    // request 로 들어오는 Jwt 의 유효성을 검증 - JwtProvider.validationToken() 을 필터로서 FilterChain 에 추가
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        // request 에서 token 을 취한다.
        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        // 검증
        log.info("[Verifying token]");
        log.info(((HttpServletRequest) request).getRequestURL().toString());

        //set Authentication
        if (token != null && jwtProvider.validationToken(token)) {
            if (!((HttpServletRequest) request).getRequestURI().equals("/v1/user/reissue")){
                // Redis에 해당 accessToken logout 여부를 확인
                String isLogout = redisTemplate.opsForValue().get(token);

                // 로그아웃이 없는(되어 있지 않은) 경우 해당 토큰은 정상적으로 작동하기
                if (ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}