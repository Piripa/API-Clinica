package med.voll.api.infra;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.usuario.UsuarioRepository;
//utilizado para que o Spring carregue uma class/componente genérico


@Component 
public class SecurityFilter extends OncePerRequestFilter { //chamado sempre que tiver uma requisição na api
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository repository;
	
	
	@Override //FilterChain representa a cadeia de filtros,podemos ter 0 ou vários filtros de autenticacao
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//recuperar token : enviado por um cabeçalho do protocolo HTTP (Cabeçalho Authorizaton)
		//É importante determinar a ordem dos filtros aplicados, para serem chamados
		
		var tokenJWT = recuperarToken(request);
		
		if(tokenJWT!= null) {
			var subject = tokenService.getSubject(tokenJWT);
			var usuario = repository.findByLogin(subject);
			var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()); //estilo dto que precisa ser chamado
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		
		
		filterChain.doFilter(request, response); //necessario para chamar os próximos filtros na aplicação
	}

private String recuperarToken(HttpServletRequest request) {
	var authorizationHeader = request.getHeader("Authorization");
	if(authorizationHeader != null) {
		return authorizationHeader.replace("Bearer ","");
	}
	return null;
	
}
	
}
