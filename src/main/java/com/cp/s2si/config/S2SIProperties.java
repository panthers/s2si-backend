package com.cp.s2si.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @author panther
 *
 */
@Component
public class S2SIProperties {
	
	public static final String MODULE_NAME = "s2si";
	
	public static final UUID RANDOM_SERVER_UUID = UUID.randomUUID();
	
	/**
	 * Request Signing Key used to sign all JWT for client.
	 * All requests coming from client will be verified against this key.
	 */
	@Value("${chp.auth.keys.rsk}")
	private String RSK;
	
	public String getRSK() {
		return RSK;
	}

	/**
	 * Micro Service Signing Key used to verify JWT requests coming from Micro Services (core, wf, int etc.)
	 */
	@Value("${chp.auth.keys.mssk}")
	private String MSSK;
	
	public String getMSSK() {
		return MSSK;
	}
	
	/**
	 * Request Encryption Key used to encrypt and decrypt all JWT.
	 */
	@Value("${chp.auth.keys.rek}")
	private String REK;
	
	public String getREK() {
		return REK;
	}
	
	/**
	 * Protected Resource Encryption Key used to encrypt and decrypt username and ttl.
	 */
	@Value("${chp.auth.keys.prk}")
	private String PRK;
	
	public String getPRK() {
		return PRK;
	}
	
	/**
	 * Canonical URL for the auth module. This will be the URL by which auth module can be accessed in all scenarios.
	 * Internal network, External network, Clustered, Non Clustered, proxied etc. 
	 */
	@Value("${chp.auth.canonical.url}")
	private String AUTH_CANNONICAL_URL;

	public String getAUTH_CANNONICAL_URL() {
		return AUTH_CANNONICAL_URL;
	}
	
	/**
	 * Canonical URL for the core module. This will be the URL by which core module can be accessed in all scenarios.
	 * Internal network, External network, Clustered, Non Clustered, proxied etc. 
	 */
	@Value("${chp.core.canonical.url}")
	private String CORE_CANNONICAL_URL;
	
	public String getCORE_CANNONICAL_URL() {
		return CORE_CANNONICAL_URL;
	}
	
	/**
	 * Canonical URL for the process module. This will be the URL by which process module can be accessed in all scenarios.
	 * Internal network, External network, Clustered, Non Clustered, proxied etc. 
	 */
	@Value("${chp.process.canonical.url}")
	private String PROCESS_CANNONICAL_URL;
	
	public String getPROCESS_CANNONICAL_URL() {
		return PROCESS_CANNONICAL_URL;
	}

	/**
	 * Canonical URL for the s2si module. This will be the URL by which s2si module can be accessed in all scenarios.
	 * Internal network, External network, Clustered, Non Clustered, proxied etc. 
	 */
	@Value("${chp.s2si.canonical.url}")
	private String S2SI_CANNONICAL_URL;
	
	public String getS2SI_CANNONICAL_URL() {
		return S2SI_CANNONICAL_URL;
	}
	
	/**
	 * The port used to start the artemis broker for current s2si instance.
	 */
	@Value("${chp.s2si.artemis.port}")
	private int ARTEMIS_BROKER_PORT;
	
	public int getARTEMIS_BROKER_PORT() {
		return ARTEMIS_BROKER_PORT;
	}
	
	/**
	 * The cluster name which is uniquely identified for the cluster.
	 * This is also used as broadcast and discovery group names.
	 */
	@Value("${chp.s2si.artemis.cluster.name}")
	private String ARTEMIS_CLUSTER_NAME;
	
	public String getARTEMIS_CLUSTER_NAME() {
		return ARTEMIS_CLUSTER_NAME;
	}
	
	/**
	 * The port used for discovery of other s2si clustered artemis instances.
	 */
	@Value("${chp.s2si.artemis.udp.port}")
	private int ARTEMIS_UDP_PORT;
	
	public int getARTEMIS_UDP_PORT() {
		return ARTEMIS_UDP_PORT;
	}
	
	/**
	 * The multicast ip used for broadcast and discovery of other s2si clustered artemis instances.
	 */
	@Value("${chp.s2si.artemis.udp.ip}")
	private String ARTEMIS_UDP_IP;
	
	public String getARTEMIS_UDP_IP() {
		return ARTEMIS_UDP_IP;
	}
	
}
