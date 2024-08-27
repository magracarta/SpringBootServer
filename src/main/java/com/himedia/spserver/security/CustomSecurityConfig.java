package com.himedia.spserver.security;

import com.himedia.spserver.security.filter.JWTCheckFilter;
import com.himedia.spserver.security.handler.APILgoinSuccessHandler;
import com.himedia.spserver.security.handler.APILoginFailHandler;
import com.himedia.spserver.security.handler.CustomAccessDeninedHadler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration //이클래스를 스프링 컨테이너로 사용하겠습니다.
@RequiredArgsConstructor //@Autowired보다 더 강력한 자동 주입 어노테이션
@Log4j2 //security에서 제공해주는 log 출력기능을 사용하겠습니다.
public class CustomSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //security 시스템이 방돌 후 가장 먼저 찾아 샐행하는 메서드(Bean)
        log.info("---------------------security config start-------------------------------------");
        //security Config 를 전체적으로 설정합니다.
        //CORS(Cross Origin Resource Sharing)
        //서버가 다른 곳들끼리 통신을 하고 있는 가운데 그들간의 통신에 제약을 두는 설정
        http.cors(
                httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
                }
        );
        //CSRF : 리퀘스트 위조방지 설정
        //CSRF의 취약점은 공격자가 "사용자가 의도하지 않는 요청"을 수행하게 하는 취약점입니다.
        //토큰 사용으로 어느정도 안전한 환경을 구성할 수 있으므로 disabled()
        http.csrf(config -> config.disable());

        //세션에 상태 저장을 하지 않을 환경 설정
        http.sessionManagement(
                sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        //로그인 처리 설정
        http.formLogin(config->{
           config.loginPage("/member/loginlocal"); //loadUserByUername 자동호출
           config.successHandler(new APILgoinSuccessHandler()); //로그인 성공시 실행할 코드를 갖은 클래스
           config.failureHandler(new APILoginFailHandler()); //로그인 실패시 실행할 코드를 갖은 클래스
        });

        //JWT 엑세스 토큰 체크
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        //접근시 발생한 예외처리 (옉세스 토큰 오류, 로그인 오류 등등
        http.exceptionHandling(config->{
           config.accessDeniedHandler(new CustomAccessDeninedHadler());
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //: cross-origin HTTP 요청들을 제한합니다.
    //그래서 corss-origin 요청을 하려면 서버의 동의가 필요합니다.
    //만약 서버가 동의한다면 브라우저에서는 요청을 허락하고, 동의하지 않는다면 브라우저에서 거절합니다.
    //이러한 허락을 구하고 거절하는 메커니즘을 HTTP-header를 이요해서 가능한데,
    //이를 CORS(Cross-Origin Resource Sharing)라고 부릅니다.
    //그래서 브라우저에서 cross-origin 요청을 안전하게 할 수 있도록 하는 메커니즘입니다.

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD" , "GET", "POST", "PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Cache-Control","Content-Type"));

        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return  source;
    }

}
