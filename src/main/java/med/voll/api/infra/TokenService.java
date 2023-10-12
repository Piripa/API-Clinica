package med.voll.api.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import med.voll.api.usuario.Usuario;

@Service

public class TokenService {
	
	@Value("${api.security.token.secret}") //par ler do aplication.properties
	private String secret;
	
	//É recomendado configurar uma data de expiração do token
	public String gerarToken(Usuario usuario) {
		try {
		    var algoritmo = Algorithm.HMAC256(secret);
		    return  JWT.create().withIssuer("API Voll.med").withSubject(usuario.getLogin()).withExpiresAt(dataExpiracao()).sign(algoritmo);
		} catch (JWTCreationException exception){
			throw new RuntimeException("ERRO AO GERAR TOKE JWT", exception);
		}
	}
	
	public String getSubject(String tokenJWT) {
        try {
                var algoritmo = Algorithm.HMAC256(secret);
                return JWT.require(algoritmo)
                                .withIssuer("API Voll.med")
                                .build()
                                .verify(tokenJWT)
                                .getSubject();
        } catch (JWTVerificationException exception) {
                throw new RuntimeException("Token JWT inválido ou expirado!");
        }
}

	private Instant dataExpiracao() {
		// TODO Auto-generated method stub
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
}
