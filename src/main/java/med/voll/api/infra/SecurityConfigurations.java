package med.voll.api.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * Configuração para ser statelles
 * @author vini_
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
	
	@Autowired
	private SecurityFilter securityFilter;
	
	@Bean //exportar uma class para o Spring, fazendo com que ele consiga carrega-la e realize a sua injeção de dependencia em outras classes 
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception  {
		
		return http.csrf( csrf -> csrf.disable())
                .sessionManagement( sm -> sm.sessionCreationPolicy( SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST,"/login").permitAll()
                .requestMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()
                .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();//desabilita contra ataques croos-site request forgery
		
		
//		return http.csrf(csrf -> csrf.disable())
//        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        .authorizeHttpRequests(req -> {
//            req.requestMatchers(HttpMethod.POST, "/login").permitAll();
//            req.anyRequest().authenticated();
//        })
//        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
//        .build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder psswordEncoder() { //class que representar o algoritmo de hash de senha
		return new BCryptPasswordEncoder(); //class do proprio spring e usa esse algoritmo de hash de senha
	}
}
