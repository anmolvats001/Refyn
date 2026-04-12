package com.example.AuthService.Service;

import com.example.AuthService.Dto.KafkaDto;
import com.example.AuthService.Dto.RequestDto;
import com.example.AuthService.Dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    @Autowired
    Keycloak keycloak;
    @Autowired
    KafkaTemplate<String , Object>kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;
    public String createUser(RequestDto data) {
        try {
            RealmResource realm = keycloak.realm("refyn-realm");

            UserRepresentation user = new UserRepresentation();
            user.setUsername(data.getUsername());
            user.setEmail(data.getEmail());
            user.setFirstName(data.getFirstName());
            user.setLastName(data.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);

            Response response = realm.users().create(user);

            if (response.getStatus() == 409) {
                throw new RuntimeException("User already exists: " + data.getUsername());
            }

            if (response.getStatus() != 201) {
                throw new RuntimeException("User creation failed: " + response.getStatus());
            }

            // extract userId
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf('/') + 1);

            // set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(data.getPassword());
            credential.setTemporary(false);
            realm.users().get(userId).resetPassword(credential);

            // assign role
            RoleRepresentation role = realm.roles()
                    .get(data.getRole())
                    .toRepresentation();
            realm.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(role));

            // send to kafka
            String json = objectMapper.writeValueAsString(
                    KafkaDto.builder()
                            .userId(userId)
                            .username(data.getUsername())
                            .email(data.getEmail())
                            .company(data.getCompany())
                            .college(data.getCollege())
                            .firstName(data.getFirstName())
                            .lastName(data.getLastName())
                            .role(data.getRole())
                            .build()
            );

            System.out.println("Sending to Kafka: " + json);

            kafkaTemplate.send("user-topic", json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✓ Kafka send success");
                        } else {
                            System.out.println("✗ Kafka send failed: " + ex.getMessage());
                        }
                    });

            return userId;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("User creation failed: " + e.getMessage());
        }
    }
    public ResponseDto login(String username, String password) {
        try{
            System.out.println(username);
            System.out.println(password);
            Keycloak keycloakClient = KeycloakBuilder.builder()
                    .serverUrl("http://keycloak:8080") // change if Docker
                    .realm("refyn-realm")
                    .clientId("refyn-client")
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password)
                    .build();

            return ResponseDto.builder().token(keycloakClient.tokenManager().getAccessTokenString()).refreshToken(keycloakClient.tokenManager().grantToken().getRefreshToken()).message("login successfull").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User login failed: " + e.getMessage(), e);
        }

    }
    public void deleteUser(String userId) {
        keycloak.realm("refyn-realm").users().get(userId).remove();
    }

}
