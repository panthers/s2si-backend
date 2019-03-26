package com.cp.s2si.config;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.artemis.api.core.BroadcastGroupConfiguration;
import org.apache.activemq.artemis.api.core.DiscoveryGroupConfiguration;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.UDPBroadcastEndpointFactory;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.ScaleDownConfiguration;
import org.apache.activemq.artemis.core.config.ha.ColocatedPolicyConfiguration;
import org.apache.activemq.artemis.core.config.ha.ReplicaPolicyConfiguration;
import org.apache.activemq.artemis.core.config.ha.ReplicatedPolicyConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import com.cp.s2si.jms.Listeners;
import com.cp.s2si.jms.tasks.IQueueTask;

@Configuration
@EnableJms
public class QueueConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QueueConfig.class);
	
	@Autowired
	private S2SIProperties s2SIProperties;
	
	@Bean(name = "jmsQueueTemplate")
	public JmsTemplate jmsQueueTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setExplicitQosEnabled(true);
		jmsTemplate.setMessageConverter(new SimpleMessageConverter() {

			@Override
			protected ObjectMessage createMessageForSerializable(Serializable object, Session session)
					throws JMSException {
				ObjectMessage om = super.createMessageForSerializable(object, session);
				om.setJMSPriority(Message.DEFAULT_PRIORITY);
				if (object instanceof IQueueTask) {
					IQueueTask task = (IQueueTask) object;
					switch (task.getTaskPriority()) {
					case HIGH:
						om.setJMSPriority(9);
						break;
					case LOW:
						om.setJMSPriority(0);
						break;
					default:
						break;
					}
				}
				return om;
			}
			
		});
		jmsTemplate.setDefaultDestinationName(Listeners.S2SI_QUEUE);
		return jmsTemplate;
	}
	
	@Bean(name = "jmsTopicTemplate")
	public JmsTemplate jmsTopicTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.setDefaultDestinationName(Listeners.S2SI_TOPIC);
		return jmsTemplate;
	}
	
	@Bean(name = "jmsTopicListenerContainerFactory")
    public DefaultJmsListenerContainerFactory getTopicFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory f = new  DefaultJmsListenerContainerFactory();
        f.setConnectionFactory(connectionFactory);
        f.setSessionTransacted(true);
        f.setPubSubDomain(true);
        return f;
    }
	
	@Bean @Profile("!test")
	public ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
		return configuration -> {
			Map<String, Object> cnaconfig = new HashMap<>(); // connector and acceptor config
			cnaconfig.put("host", "localhost");
			cnaconfig.put("port", s2SIProperties.getARTEMIS_BROKER_PORT());
			
			String connectorName = "netty";
			
			configuration.addConnectorConfiguration(connectorName, new TransportConfiguration(NettyConnectorFactory.class.getName(), cnaconfig));
			configuration.addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName(), cnaconfig));
			
			configuration.addBroadcastGroupConfiguration(new BroadcastGroupConfiguration()
				.setName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
				.setEndpointFactory(new UDPBroadcastEndpointFactory().setGroupAddress(s2SIProperties.getARTEMIS_UDP_IP()).setGroupPort(s2SIProperties.getARTEMIS_UDP_PORT()))
				.setConnectorInfos(Arrays.asList(connectorName))
			);
			
			configuration.addDiscoveryGroupConfiguration(s2SIProperties.getARTEMIS_CLUSTER_NAME(), new DiscoveryGroupConfiguration()
				.setName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
				.setBroadcastEndpointFactory(new UDPBroadcastEndpointFactory().setGroupAddress(s2SIProperties.getARTEMIS_UDP_IP()).setGroupPort(s2SIProperties.getARTEMIS_UDP_PORT()))
			);
			
			configuration.addClusterConfiguration(new ClusterConnectionConfiguration()
				.setName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
				.setAddress("jms")
				.setConnectorName(connectorName)
				.setDiscoveryGroupName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
			);
			
			//HA Not properly tested
			configuration.setHAPolicyConfiguration(new ColocatedPolicyConfiguration()
				.setLiveConfig(new ReplicatedPolicyConfiguration()
					.setCheckForLiveServer(true)
					.setClusterName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
					.setGroupName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
				)
				.setBackupConfig(new ReplicaPolicyConfiguration()
					.setClusterName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
					.setGroupName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
					.setScaleDownConfiguration(new ScaleDownConfiguration()
						.setClusterName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
						.setDiscoveryGroup(s2SIProperties.getARTEMIS_CLUSTER_NAME())
						.setGroupName(s2SIProperties.getARTEMIS_CLUSTER_NAME())
					)
				)
			
			);
			
			// Append s2si before the artemis directory
			configuration.setJournalDirectory(msPathModify(configuration.getJournalLocation()));
			configuration.setBindingsDirectory(msPathModify(configuration.getBindingsLocation()));
			configuration.setLargeMessagesDirectory(msPathModify(configuration.getLargeMessagesLocation()));
			configuration.setPagingDirectory(msPathModify(configuration.getPagingLocation()));
			
			LOGGER.info("Journal Dir {}", configuration.getJournalDirectory());
			LOGGER.info("Bindings Dir {}", configuration.getBindingsDirectory());
			LOGGER.info("LargeMessages Dir {}", configuration.getLargeMessagesDirectory());
			LOGGER.info("Paging Dir {}", configuration.getPagingDirectory());
			
			LOGGER.debug("Artemis configuration complete");
			
		};
	}
	
	private String msPathModify(final File file) {
		Path p = file.getParentFile().toPath();
		p = p.resolve("s2si").resolve(file.getName());
		return p.toFile().toString();
	}
	
}
