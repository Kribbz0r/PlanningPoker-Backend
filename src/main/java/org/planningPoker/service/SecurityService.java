package org.planningPoker.service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.planningPoker.config.AppConfig;
import org.planningPoker.model.LoginDto;
import org.planningPoker.model.User;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.SignatureException;
import io.quarkus.runtime.configuration.ProfileManager;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class SecurityService {

    @Inject
    UserService userService;

    @Inject
    AppConfig appConfig;

    private final MongoClient mongoClient;

    @Inject
    public SecurityService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response login(@Valid LoginDto loginDto) throws Exception {
        System.out.println("service: " + loginDto.getEmail());
        Response userResponse = userService.findUser(loginDto.getEmail());
        Document userDocument = (Document) userResponse.getEntity();
        User user;

        if (userDocument != null && userDocument.get("authorized").equals(1)) {
            user = new User(userDocument.get("_id").toString(), userDocument.get("email").toString(),
                    userDocument.get("role").toString(), Integer.parseInt(userDocument.get("authorized").toString()),
                    userDocument.get("password").toString(), userDocument.get("name").toString());
        } else if (userDocument.get("authorized").equals(0)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("You do not have permission to use the planner.").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect email or password").build();
        }

        if (user != null && checkCredentials(user.getPassword(), loginDto.getPassword())) {
            System.out.println("creds: ");
            String newToken = generateJwtToken(user);

            return Response.ok(newToken).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect email or password").build();

    }

    private String generateJwtToken(User user) throws Exception {
        Set<String> userPermissions = getUserPermissions(user);
        PrivateKey privateKey = loadPrivateKey();
        System.out.println(appConfig.jwtIssuer());

        String issuer = appConfig.jwtIssuer() != null ? appConfig.jwtIssuer() : System.getenv("JWT_ISSUER");

        if (privateKey == null) {
            return "im a token";
        } else {
            return Jwt.issuer(issuer)
                    .upn(user.getEmail())
                    .groups(userPermissions)
                    .expiresIn(86400)
                    .claim(Claims.email_verified.name(), user.getEmail())
                    .sign(privateKey);
        }

    }

    private PrivateKey loadPrivateKey() throws Exception {
        System.out.println("You are here!!");

        try {
            String privateKeyString;
            if (ProfileManager.getLaunchMode().isDevOrTest()) {
                privateKeyString = appConfig.privateKey();
            } else {
                privateKeyString = System.getenv("PRIVATE_KEY");
            }

            System.out.println("private key!!!!: " + privateKeyString);
            privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "")
                    // .replace('"', "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            System.out.println("updated: " + privateKeyString);
            byte[] privateKeyByte = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getUserPermissions(final User user) {

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");

        }
        MongoCollection<Document> collection = database.getCollection("Roles");

        ObjectId convertedId = new ObjectId(user.getRole());
        Document query = new Document("_id", convertedId);

        Document roleDocument = collection.find(query).first();
        Set<String> permissions;

        if (roleDocument != null) {
            String permissionsString = roleDocument.getString("permissions");
            permissions = Set.of(permissionsString.split(","));
        } else {
            return Collections.emptySet();
        }

        return permissions;
    }

    private boolean checkCredentials(String encryptedPassword, String rawPassword) {
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }

    public Jws<io.jsonwebtoken.Claims> verifyJwt(String jwtToken) throws Exception {

        PublicKey publicKey = loadPublicKey();
        System.out.println("pub key: " + publicKey);
        String issuer = appConfig.jwtIssuer();
        System.out.println("issuer: " + issuer);

        try {
            return Jwts.parser().requireIssuer(issuer).verifyWith(publicKey).build().parseSignedClaims(jwtToken);
        } catch (SignatureException e) {
            Exception exception = new Exception("JWT Signature not valid");
            exception.initCause(e);
            throw exception;
        } catch (ExpiredJwtException e) {
            Exception exception = new Exception("JWT has expired");
            exception.initCause(e);
            throw exception;
        } catch (UnsupportedJwtException e) {
            Exception exception = new Exception("JWT not supported");
            exception.initCause(e);
            throw exception;
        } catch (MalformedJwtException e) {
            Exception exception = new Exception("Invalid JWT format");
            exception.initCause(e);
            throw exception;
        } catch (IllegalArgumentException e) {
            Exception exception = new Exception("Invalid JWT");
            exception.initCause(e);
            throw exception;
        }
    }

    private PublicKey loadPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {

        String publicKey;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            publicKey = appConfig.publicKey();
        } else {
            publicKey = System.getenv("PUBLIC_KEY");

        }

        System.out.println("key:" + publicKey);
        publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedPublicKey = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

}
