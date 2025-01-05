package library.library_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureException;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "secretkeyforlibrarybackendandforangularfrontend";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // Generate token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate token
    public static boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (SignatureException | ExpiredJwtException e) {
            return false;
        }
    }

    // Extract all claims from token
    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // Your public or secret key for signing/verifying the JWT
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Using the updated method for parsing signed claims
    private static Claims getAllClaimsFromTokenUsingParseSignedClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)  // Your public or secret key for verification
                .build()
                .parseSignedClaims(token)  // This is the updated method to parse signed claims
                .getPayload();
    }

    // Extract username
    public static String extractUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }


}
