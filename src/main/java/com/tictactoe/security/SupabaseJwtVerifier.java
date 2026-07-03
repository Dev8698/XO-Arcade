package com.tictactoe.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Component
public class SupabaseJwtVerifier {

    private final JWTVerifier hsVerifier;
    private JWTVerifier esVerifier;

    public SupabaseJwtVerifier(
            @Value("${supabase.jwt.secret}") String jwtSecret,
            @Value("${supabase.jwt.jwk.x:}") String jwkX,
            @Value("${supabase.jwt.jwk.y:}") String jwkY) {
        
        // 1. Initialize HS256 Verifier
        byte[] secretBytes;
        try {
            secretBytes = Base64.getDecoder().decode(jwtSecret.trim());
        } catch (IllegalArgumentException e) {
            secretBytes = jwtSecret.getBytes();
        }
        Algorithm hsAlgorithm = Algorithm.HMAC256(secretBytes);
        this.hsVerifier = JWT.require(hsAlgorithm).build();

        // 2. Initialize ES256 Verifier if public key coordinates are provided
        if (jwkX != null && !jwkX.trim().isEmpty() && jwkY != null && !jwkY.trim().isEmpty()) {
            try {
                ECPublicKey publicKey = getPublicKey(jwkX.trim(), jwkY.trim());
                Algorithm esAlgorithm = Algorithm.ECDSA256(publicKey, null);
                this.esVerifier = JWT.require(esAlgorithm).build();
            } catch (Exception e) {
                System.err.println("Failed to initialize ES256 public key verifier: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public DecodedJWT verify(String token) {
        // Decode the token first to inspect the header algorithm
        DecodedJWT decoded = JWT.decode(token);
        String alg = decoded.getAlgorithm();

        if ("ES256".equalsIgnoreCase(alg) && esVerifier != null) {
            return esVerifier.verify(token);
        }
        
        // Default fallback to HS256 verifier
        return hsVerifier.verify(token);
    }

    private ECPublicKey getPublicKey(String xBase64Url, String yBase64Url) throws Exception {
        byte[] xBytes = Base64.getUrlDecoder().decode(xBase64Url);
        byte[] yBytes = Base64.getUrlDecoder().decode(yBase64Url);
        BigInteger x = new BigInteger(1, xBytes);
        BigInteger y = new BigInteger(1, yBytes);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec ecSpec = ((ECPublicKey) kpg.generateKeyPair().getPublic()).getParams();

        ECPoint ecPoint = new ECPoint(x, y);
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(ecPoint, ecSpec);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPublicKey) kf.generatePublic(pubKeySpec);
    }
}
