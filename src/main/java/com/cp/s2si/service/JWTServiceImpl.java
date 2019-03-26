package com.cp.s2si.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.s2si.config.S2SIProperties;
import com.cp.s2si.exception.JWTAudienceException;
import com.cp.s2si.exception.JWTBadEncryptionEception;
import com.cp.s2si.exception.JWTBadSignException;
import com.cp.s2si.exception.JWTEncryptionException;
import com.cp.s2si.exception.JWTExpiredException;
import com.cp.s2si.exception.JWTNoPayloadException;
import com.cp.s2si.exception.JWTNotATokenException;
import com.cp.s2si.exception.PRInvalidTokenException;
import com.cp.s2si.exception.PRTokenExpiredException;
import com.cp.s2si.exception.PRTokenIssueException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * 
 * @author panther
 *
 */
@Service
public class JWTServiceImpl implements JWTService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTServiceImpl.class);
	
	@Autowired
	private S2SIProperties s2SIProperties;
	
	@Autowired
	private ObjectMapper OM;
	
	/**
	 * JWT Request Sign Verifier
	 */
	private JWSVerifier REQ_VERIFIER;
	
	/**
	 * JWT MS Request Signer
	 */
	private JWSSigner MS_REQ_SIGNER;
	
	/**
	 * JWT MS Request Sign Verifier
	 */
	private JWSVerifier MS_REQ_VERIFIER;
	
	/**
	 * JWT All Encrypter
	 */
	private JWEEncrypter JWE_ENCRYPTER;
	
	/**
	 * JWT All Decrypter
	 */
	private JWEDecrypter JWE_DECRYPTER;
	
	/**
	 * Protected Resource Key
	 */
	private SecretKey PR_KEY;
	
	@PostConstruct
	private void artifacts() throws Exception {
		REQ_VERIFIER = new MACVerifier(s2SIProperties.getRSK().getBytes());
		JWE_ENCRYPTER = new DirectEncrypter(Base64.getDecoder().decode(s2SIProperties.getREK()));
		JWE_DECRYPTER = new DirectDecrypter(Base64.getDecoder().decode(s2SIProperties.getREK()));
		MS_REQ_SIGNER = new MACSigner(s2SIProperties.getMSSK().getBytes());
		MS_REQ_VERIFIER = new MACVerifier(s2SIProperties.getMSSK().getBytes());
		PR_KEY = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(s2SIProperties.getPRK().getBytes()));
	}

	@Override
	public String issueMicroServiceJWT(String subject) throws SignatureException, JWTEncryptionException {
		
		ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
		
		// Prepare JWT with claims set.
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject(subject)
				.issuer(s2SIProperties.getCORE_CANNONICAL_URL())
				.expirationTime(Date.from(zdt.plusMinutes(1).toInstant())) // 1 min expiration time
				.audience(Arrays.asList(
						s2SIProperties.getAUTH_CANNONICAL_URL(),
						s2SIProperties.getPROCESS_CANNONICAL_URL(),
						s2SIProperties.getS2SI_CANNONICAL_URL()
					))
				.issueTime(Date.from(zdt.toInstant()))
				.build();

		// JWT prepared and ready to be signed.
		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

		// Apply the HMAC protection. JWT Signing.
		try {
			signedJWT.sign(MS_REQ_SIGNER);
		} catch (JOSEException e) {
			LOGGER.error("JWT signing exception for subject {}", subject);
			throw new SignatureException(e);
		}
		
		// Wrapper JWT prepared for encryption. Payload is the signed JWT.
		JWEObject jweObject = new JWEObject(
			    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM)
			        .contentType("JWT") // required to signal nested JWT
			        .build(),
			    new Payload(signedJWT.serialize())); // Serialize to compact form, produces something like
		
		// Perform encryption of wrapper JWT.
		try {
			jweObject.encrypt(JWE_ENCRYPTER);
		} catch (JOSEException e) {
			LOGGER.error("JWT encryption exception for subject {}", subject);
			throw new JWTEncryptionException(e);
		}

		return jweObject.serialize();
	}

	@Override
	public String consumeRequestJWT(String jweObjectStr) throws JWTNotATokenException, JWTBadEncryptionEception, JWTNoPayloadException, JWTBadSignException, JWTExpiredException, JWTAudienceException {
		JWEObject jweObject;
		try {
			jweObject = JWEObject.parse(jweObjectStr);
		} catch (ParseException e) {
			throw new JWTNotATokenException(e);
		}
		try {
			jweObject.decrypt(JWE_DECRYPTER);
		} catch (JOSEException e) {
			throw new JWTBadEncryptionEception(e);
		}
		
		SignedJWT signedJWT;
		if(jweObject.getPayload() != null) {
			try {
				signedJWT = SignedJWT.parse(jweObject.getPayload().toString());
			} catch (ParseException e) {
				throw new JWTNotATokenException(e);
			}
		} else {
			LOGGER.error("Wrapper JWT doesnot contain payload of signed JWT");
			throw new JWTNoPayloadException("Missing signed JWT payload in encrypted wrapper JWT");
		}
		
		try {
			if(!signedJWT.verify(REQ_VERIFIER)) {
				LOGGER.warn("Signature verification failed {}", signedJWT);
				throw new JWTBadSignException("Signature verification failed");
			}
		} catch (JOSEException e) {
			throw new JWTBadSignException(e);
		}
		
		JWTClaimsSet readOnlyJWTClaimsSet;
		try {
			readOnlyJWTClaimsSet = signedJWT.getJWTClaimsSet();
		} catch (ParseException e) {
			LOGGER.error("SignedJWT payload parsing exception");
			throw new JWTNoPayloadException("Missing claimset payload in signedJWT");
		}
		//Expiration Time Check
		if (null == readOnlyJWTClaimsSet.getExpirationTime()) {
			LOGGER.error("No expiration time on SignedJWT claimset {}", readOnlyJWTClaimsSet);
			throw new JWTExpiredException("No expiration time on SignedJWT claimset");
		}
		ZonedDateTime jwtExpirationTime = ZonedDateTime.ofInstant(readOnlyJWTClaimsSet.getExpirationTime().toInstant(), ZoneOffset.UTC);
		ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneOffset.UTC);
		if(jwtExpirationTime.isBefore(currentTime.minusMinutes(2))) {
			LOGGER.error("JWT expired with claim set {}", readOnlyJWTClaimsSet);
			throw new JWTExpiredException("JWT time expired", readOnlyJWTClaimsSet.getSubject(), jwtExpirationTime.toLocalDateTime());
		}
		//Audience Check
		if (readOnlyJWTClaimsSet.getAudience() == null || !readOnlyJWTClaimsSet.getAudience().contains(s2SIProperties.getCORE_CANNONICAL_URL())) {
			LOGGER.error("JWT audience is not set for this micro service");
			throw new JWTAudienceException();
		}
		
		//All good return subject
		return readOnlyJWTClaimsSet.getSubject();
	}

	@Override
	public String consumeMicroServiceJWT(String jweObjectStr) throws JWTNotATokenException, JWTBadEncryptionEception, JWTNoPayloadException, JWTBadSignException, JWTExpiredException, JWTAudienceException {
		JWEObject jweObject;
		try {
			jweObject = JWEObject.parse(jweObjectStr);
		} catch (ParseException e) {
			throw new JWTNotATokenException(e);
		}
		try {
			jweObject.decrypt(JWE_DECRYPTER);
		} catch (JOSEException e) {
			throw new JWTBadEncryptionEception(e);
		}
		
		SignedJWT signedJWT;
		if(jweObject.getPayload() != null) {
			try {
				signedJWT = SignedJWT.parse(jweObject.getPayload().toString());
			} catch (ParseException e) {
				throw new JWTNotATokenException(e);
			}
		} else {
			LOGGER.error("Wrapper JWT doesnot contain payload of signed JWT");
			throw new JWTNoPayloadException("Missing signed JWT payload in encrypted wrapper JWT");
		}
		
		try {
			if(!signedJWT.verify(MS_REQ_VERIFIER)) {
				LOGGER.warn("Signature verification failed {}", signedJWT);
				throw new JWTBadSignException("Signature verification failed");
			}
		} catch (JOSEException e) {
			throw new JWTBadSignException(e);
		}
		
		JWTClaimsSet readOnlyJWTClaimsSet;
		try {
			readOnlyJWTClaimsSet = signedJWT.getJWTClaimsSet();
		} catch (ParseException e) {
			LOGGER.error("SignedJWT payload parsing exception");
			throw new JWTNoPayloadException("Missing claimset payload in signedJWT");
		}
		//Expiration Time Check
		if(null == readOnlyJWTClaimsSet.getExpirationTime()) {
			LOGGER.error("No expiration time on SignedJWT claimset {}", readOnlyJWTClaimsSet);
			throw new JWTExpiredException("No expiration time on SignedJWT claimset");
		}
		ZonedDateTime jwtExpirationTime = ZonedDateTime.ofInstant(readOnlyJWTClaimsSet.getExpirationTime().toInstant(), ZoneOffset.UTC);
		ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneOffset.UTC);
		if(jwtExpirationTime.isBefore(currentTime.minusMinutes(1))) {
			LOGGER.error("JWT expired with claim set {}", readOnlyJWTClaimsSet);
			throw new JWTExpiredException("JWT time expired");
		}
		//Audience Check
		if (readOnlyJWTClaimsSet.getAudience() == null || !readOnlyJWTClaimsSet.getAudience().contains(s2SIProperties.getCORE_CANNONICAL_URL())) {
			LOGGER.error("JWT audience is not set for this micro service");
			throw new JWTAudienceException();
		}
		
		//All good return subject
		return readOnlyJWTClaimsSet.getSubject();
	}

	@Override
	public String issueProtectedToken(String uid, Long ttl) throws PRTokenIssueException {
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("ttl", LocalDateTime.now().atZone(ZoneOffset.UTC).plusMinutes(ttl).toString());
		try {
			String data = OM.writeValueAsString(map);
			Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, PR_KEY);
            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
		} catch (JsonProcessingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			throw new PRTokenIssueException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String consumeProtectedToken(String token) throws PRInvalidTokenException, PRTokenExpiredException {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, PR_KEY);
			try {
				String data = new String(cipher.doFinal(Base64.getUrlDecoder().decode(token)));
				
				Map<String, String> map = OM.readValue(data, Map.class);
				String time = map.get("ttl");
				ZonedDateTime expiryTime = ZonedDateTime.parse(time);
				ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneOffset.UTC);
				if(expiryTime.isBefore(currentTime.minusMinutes(1))) {
					throw new PRTokenExpiredException();
				}
				return map.get("uid");
			} catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
				throw new PRInvalidTokenException(e);
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			LOGGER.error("Exception in cipher", e);
			throw new RuntimeException(e);
		}
	}

	
}
