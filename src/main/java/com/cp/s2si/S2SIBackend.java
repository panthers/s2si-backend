package com.cp.s2si;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.nimbusds.jose.util.IOUtils;

@SpringBootApplication
public class S2SIBackend {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(S2SIBackend.class);

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		if(null == args || 0 == args.length) {
			LOGGER.info("Spring Boot is starting core service.");
			SpringApplication.run(S2SIBackend.class, args);
			LOGGER.info("Spring Boot has completed and core service has started.");
		} else {
			for (String arg: args) {
				if("--help".equals(arg) || "-h".equals(arg) || args.length > 1) {
					help();
				} else if("--service".equals(arg) || "-s".equals(arg)) {
					if(isLinux()) {
						dumpUnit();
					} else if(isWindows()) {
						dumpWinSW();
					} else {
						System.out.println("Neither linux or windows OS found");
					}
				} else if("--version".equals(arg) || "-v".equals(arg)) {
					dumpVersion();
				} else if("--props".equals(arg) || "-p".equals(arg)) {
					dumpProperties();
				}
			}
		}
	}
	
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

        	LOGGER.debug("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
            	LOGGER.debug(beanName);
            }

        };
    }
	
	protected static void help() {
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/README")) {
			String help = IOUtils.readInputStreamToString(is, Charset.forName("UTF-8"));
			System.out.println(help);
		} catch (Exception e) {
			// gulp
		}
	}
	
	protected static boolean isLinux() {
		String OS = System.getProperty("os.name").toLowerCase();
		return ( OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	protected static boolean isWindows() {
		String OS = System.getProperty("os.name").toLowerCase();
		return ( OS.indexOf("win") >= 0 );
	}
	
	protected static void dumpUnit() {
		String user = System.getProperty("user.name");
		try (
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/s2si.service");
				InputStream conf_is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/s2si.conf");
				FileOutputStream fos = new FileOutputStream(new File("./s2si.service"));
			) {
			File conf_target = new File("./s2si.conf");
			Files.copy(conf_is, conf_target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			String path = new File("./s2si.war").getAbsolutePath();
			String service = IOUtils.readInputStreamToString(is, Charset.forName("UTF-8"));
			service = service.replace("<user>", user).replace("<path>", path);
			fos.write(service.getBytes());
		} catch (Exception e) {
			// gulp
		}
	}
	
	protected static void dumpWinSW() {
		try (
				InputStream exe_is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/s2si.exe");
				InputStream xml_is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/s2si.xml");
			) {
			File exe_target = new File("./s2si.exe");
			File xml_target = new File("./s2si.xml");
			Files.copy(exe_is, exe_target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(xml_is, xml_target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			// gulp
		}
	}
	
	protected static void dumpProperties() {
		try (
				InputStream props_is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cmd/application-sample.properties");
			) {
			File props_target = new File("./application-sample.properties");
			Files.copy(props_is, props_target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			// gulp
		}
	}
	
	protected static void dumpVersion() {
		System.out.println(S2SIBackend.class.getPackage().getImplementationVersion());
	}
}
