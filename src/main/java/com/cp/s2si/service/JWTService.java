package com.cp.s2si.service;

import java.security.SignatureException;

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

/**
 * 
 * @author panther
 *
 */
public interface JWTService {
	
	/**
	 * Generate a JWT and issue it. Expires in 15 mins.
	 * 
	 * @param subject The subject on the token, Can be userId or any JSON string. Preferably not too long.
	 * @return JWT Signed and Encrypted token in string 
	 * @throws SignatureException
	 * @throws JWTEncryptionException
	 */
	String issueMicroServiceJWT(String subject) throws SignatureException, JWTEncryptionException;
	
	String consumeRequestJWT(String jweObjectStr) throws JWTNotATokenException, JWTBadEncryptionEception, JWTNoPayloadException, JWTBadSignException, JWTExpiredException, JWTAudienceException;
	
	String consumeMicroServiceJWT(String jweObject) throws JWTNotATokenException, JWTBadEncryptionEception, JWTNoPayloadException, JWTBadSignException, JWTExpiredException, JWTAudienceException;

	String issueProtectedToken(String uid, Long ttl) throws PRTokenIssueException;

	String consumeProtectedToken(String token) throws PRInvalidTokenException, PRTokenExpiredException;

}