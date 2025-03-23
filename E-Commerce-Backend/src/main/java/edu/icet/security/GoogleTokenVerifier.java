package edu.icet.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edu.icet.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    @Value("${google.client-id}")
    private String clientId;

    public GoogleIdToken.Payload verify(String credential) throws GeneralSecurityException, IOException {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken != null) {
                return idToken.getPayload();
            }
            throw new InvalidCredentialsException("Invalid Google token: token verification failed");
        } catch (Exception e) {
            throw new InvalidCredentialsException("Google token verification error: " + e.getMessage());
        }
    }
}