package spring.group.spring.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import spring.group.spring.models.Role;
import spring.group.spring.services.MyUserDetailsService;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtProvider {
    private final JwtKeyProvider keyProvider;
    private final MyUserDetailsService myUserDetailsService;

    public String createToken(String username, List<Role> roles) throws JwtException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);
        Key privateKey = keyProvider.getPrivateKey();
        return Jwts.builder()
                .subject(username)
                .claim("auth", roles.stream().map(Role::name).toList())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Claims claims =
                    Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            String username = claims.getSubject();
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "",
                    userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            //don't trust the JWT!
            throw new JwtException("Bearer token not valid");
        }
    }

    public String getUsernameFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Claims claims =
                    Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }

}