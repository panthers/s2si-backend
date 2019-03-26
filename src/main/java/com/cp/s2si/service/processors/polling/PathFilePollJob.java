package com.cp.s2si.service.processors.polling;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cp.s2si.exception.FileNotReadyException;
import com.cp.s2si.service.ProcessorManagementService;
import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.ProcessorData;
import com.cp.s2si.service.properties.polling.PollingProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype")
public class PathFilePollJob implements Job {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathFilePollJob.class);
	
	public static final String PROCESSOR_ID = "PROCESSOR_ID";
	public static final String PROPS = "PROPS";
	
	@Autowired
	private ProcessorManagementService processorManagementService;
	
	private PollingProperties props;
	private Processor processor;
	final ExecutorService pool = Executors.newCachedThreadPool();
	final Map<Path, Future<Integer>> futures = new ConcurrentHashMap<>();
	
	protected PollingProperties getProps() {
		return props;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		//Init
		try {
			ObjectMapper om = new ObjectMapper();
			String processorId = context.getJobDetail().getJobDataMap().getString(PROCESSOR_ID);
			LOGGER.info("Poll job triggered for processor Id {}", processorId);
			processor = processorManagementService.getProcessorById(UUID.fromString(processorId));
			props = om.readValue(context.getJobDetail().getJobDataMap().getString(PROPS), PollingProperties.class);
		} catch (Exception e) {
			LOGGER.error("Failed setting properties {}", e);
			return;
		}
		
		//Check
		try {
			checkIfAnyFileReadyForPickUp();
		} catch (FileNotReadyException e) {
			LOGGER.warn("Files not ready for pickup {}", e.getMessage());
			return;
		}
		
		//Send to process
		try(DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(getProps().getPathToPoll()))) {
			for(final Path filePath : ds) {
				if(!Files.isRegularFile(filePath)) {
					continue;
				}
				if(null != getProps().getFileNameToCheck() && filePath.getFileName().toString().equalsIgnoreCase(getProps().getFileNameToCheck())) {
					continue;
				}
				try {
					validFilePrefixPickup(filePath);
					validFileSuffixPickup(filePath);
					validFileRegexPickup(filePath);
				} catch (FileNotReadyException e) {
					LOGGER.error("File not ready {} with error {}", filePath, e.getMessage());
					continue;
				}
				runProcessor(filePath);
			}
		} catch (IOException e) {
			LOGGER.error("File listing error {}", e);
		}
		
		//Post process
		for(Path filePath : futures.keySet()) {
			try {
				Future<Integer> future = futures.get(filePath);
				Integer result = future.get();
				if(result.intValue() < 0) {
					LOGGER.warn("Ignoring file {} since no processing happened", filePath);
					continue;
				}
				if(getProps().isDeleteAfterPickup()) {
					deletePostProcess(filePath);
				} else if(getProps().isMoveAfterPickup()) {
					if(result.intValue() == 0) {
						movePostProcessForFailure(filePath);
					} else {
						movePostProcessForSuccess(filePath);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("Error processing futures {}", e);
			}
		}
	}
	
	protected void deletePostProcess(final Path filePath) {
		try {
			Files.delete(filePath);
		} catch (IOException e) {
			LOGGER.error("File delete error after pickup {} {}", filePath, e);
		}
	}
	
	protected void movePostProcessForFailure(final Path filePath) {
		if(!StringUtils.hasText(getProps().getMoveToPathOnFailure())) {
			LOGGER.warn("Move to failure path is not configured {}", filePath);
			return;
		}
		
		Path targetFolder = Paths.get(getProps().getPathToPoll()).resolve(getProps().getMoveToPathOnFailure());
		try {
			Files.createDirectories(targetFolder);
		} catch (IOException e) {
			LOGGER.error("Failure target folder creation failed with error {}", e);
		}
		
		try {
			Files.move(filePath, targetFolder.resolve(renameFile(filePath.getFileName().toString())), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Move to Failure target folder failed with error {}", e);
		}
	}
	
	protected String renameFile(String filename) {
		if(!getProps().isRenameFile()) {
			return filename;
		}
		String prefix = "";
		if(getProps().getRenamePrefix() != null) {
			prefix = getProps().getRenamePrefix();
		}
		String namesuffix = "";
		if(getProps().getRenameNameSuffix() != null) {
			namesuffix = getProps().getRenameNameSuffix();
		}
		String extensionsuffix = "";
		if(getProps().getRenameExtensionSuffix() != null) {
			extensionsuffix = getProps().getRenameExtensionSuffix();
		}
		return prefix + StringUtils.stripFilenameExtension(filename) + namesuffix + "." + StringUtils.getFilenameExtension(filename) + extensionsuffix;
	}
	
	protected void movePostProcessForSuccess(final Path filePath) {
		if(!StringUtils.hasText(getProps().getMoveToPathOnSuccess())) {
			LOGGER.warn("Move to success path is not configured {}", filePath);
			return;
		}
		
		Path targetFolder = Paths.get(getProps().getPathToPoll()).resolve(getProps().getMoveToPathOnSuccess());
		try {
			Files.createDirectories(targetFolder);
		} catch (IOException e) {
			LOGGER.error("Success target folder creation failed with error {}", e);
		}
		
		try {
			Files.move(filePath, targetFolder.resolve(renameFile(filePath.getFileName().toString())), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Move to Success target folder failed with error {}", e);
		}
	}
	
	protected void validFilePrefixPickup(final Path filePath) throws FileNotReadyException {
		if(!StringUtils.hasText(getProps().getFileNamePrefixToPickup())) {
			LOGGER.warn("No file pickup prefix configured. Skipping check.");
			return;
		}
		if(!filePath.getFileName().toString().startsWith(getProps().getFileNamePrefixToPickup())) {
			throw new FileNotReadyException("File pickup prefix doesnot match");
		}
	}
	
	protected void validFileSuffixPickup(final Path filePath) throws FileNotReadyException {
		if(!StringUtils.hasText(getProps().getFileNameSuffixToPickup())) {
			LOGGER.warn("No file pickup suffix configured. Skipping check.");
			return;
		}
		if(!filePath.getFileName().toString().endsWith(getProps().getFileNameSuffixToPickup())) {
			throw new FileNotReadyException("File pickup suffix doesnot match");
		}
	}
 	
	protected void validFileRegexPickup(final Path filePath) throws FileNotReadyException {
		if(!StringUtils.hasText(getProps().getFileNameRegexToPickup())) {
			LOGGER.warn("No file pickup regex configured. Skipping check.");
			return;
		}
		Pattern pattern = Pattern.compile(getProps().getFileNameRegexToPickup());
		Matcher matcher = pattern.matcher(filePath.getFileName().toString());
		if(!matcher.find()) {
			throw new FileNotReadyException("File pickup regex doesnot match");
		}
	}
	
	protected void checkIfAnyFileReadyForPickUp() throws FileNotReadyException {
		checkIfBaseFolderExists();
		checkIfSpecificFileExists();
	}
	
	protected void checkIfBaseFolderExists() throws FileNotReadyException {
		try {
			Path folder = Paths.get(getProps().getPathToPoll());
			if(!Files.isDirectory(folder)) {
				throw new FileNotReadyException("Base folder doesnot exist");
			}
		} catch (Exception e) {
			throw new FileNotReadyException(e);
		}
	}
	
	protected void checkIfSpecificFileExists() throws FileNotReadyException {
		if(!StringUtils.hasText(getProps().getFileNameToCheck())) {
			LOGGER.warn("No specific file check configured.");
			return;
		}
		try {
			Path checkFilePath = Paths.get(getProps().getPathToPoll()).resolve(getProps().getFileNameToCheck());
			if(!Files.isRegularFile(checkFilePath)) {
				throw new FileNotReadyException("Check file configured but doesnot exist");
			}
		} catch (Exception e) {
			throw new FileNotReadyException(e);
		}
	}
	
	protected void runProcessor(final Path filePath) {
		Future<Integer> future = pool.submit(() -> {
			
			try {
				checkIfFileIsReadyForReading(filePath);
			} catch (FileNotReadyException e) {
				return -1;
			}
			
			try (
					RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw");
					FileChannel fc = raf.getChannel();
					InputStream is = Channels.newInputStream(raf.getChannel());
					FileLock lock = fc.tryLock();
				) {
				if(lock == null || !lock.isValid()) {
					LOGGER.warn("Failed to acquire lock on file");
				}
				ProcessorData pd = new ProcessorData();
				pd.setStream(is);
				ProcessorData returnData = processor.run(pd);
				return returnData.isStringData() && returnData.getStringData().equalsIgnoreCase("true") ? +1 : 0;
				
			} catch (Exception e) {
				LOGGER.error("File poll run failed with error {}", e);
				return 0;
			}
		});
		futures.put(filePath, future);
	}
	
	protected void checkIfFileIsReadyForReading(final Path filePath) throws FileNotReadyException {
		checkFileLockFile(filePath);
		checkFileLock(filePath);
		checkSizeAfterDelay(filePath);
	}
	
	protected void checkFileLock(final Path filePath) throws FileNotReadyException {
		try (
				RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw");
				FileChannel fc = raf.getChannel();
			) {
			FileLock lock = fc.tryLock();
			if(lock == null || !lock.isValid()) {
				throw new FileNotReadyException("Couldnot acquire lock");
			}
			lock.release();
		} catch (Exception e) {
			throw new FileNotReadyException(e);
		}
	}
	
	protected void checkFileLockFile(final Path filePath) throws FileNotReadyException {
		if(getProps().getLockFilePreffix() == null || getProps().getLockFileSuffix() == null) {
			LOGGER.warn("lockFilePreffix or lockFileSuffix is null, skipping lockfile check");
			return;
		}
		if(!StringUtils.hasText(getProps().getLockFilePreffix() + getProps().getLockFileSuffix())) {
			LOGGER.warn("lockFilePreffix and lockFileSuffix concatenates to nothing, skipping lockfile check");
			return;
		}
		String filename = filePath.getFileName().toString();
		String lockFilename = getProps().getLockFilePreffix() + filename + getProps().getLockFileSuffix();
		if(Files.exists(filePath.getParent().resolve(lockFilename))) {
			throw new FileNotReadyException("Lock file exists");
		}
	}
	
	protected void checkSizeAfterDelay(final Path filePath) throws FileNotReadyException {
		//Delay exists
		if(getProps().getSizeCheckIntervalInMilliseconds() == null) {
			LOGGER.warn("sizeCheckIntervalInMilliseconds is null, skipping size check");
			return;
		}
		
		//Before size
		long initFileSize = 0;
		try (
				FileInputStream is = new FileInputStream(filePath.toFile());
				FileChannel fc = is.getChannel();
			) {
			initFileSize = fc.size();
		} catch (Exception e) {
			LOGGER.error("Error reading size pass #1 {}", e);
			throw new FileNotReadyException(e);
		}
		
		//Delay
		try {
			Thread.sleep(getProps().getSizeCheckIntervalInMilliseconds());
		} catch (InterruptedException e) {
			LOGGER.error("Thread size check interval interrupted {}", e);
		}
		
		//After size
		long newFileSize = 0;
		try (
				FileInputStream is = new FileInputStream(filePath.toFile());
				FileChannel fc = is.getChannel();
			) {
			newFileSize = fc.size();
		} catch (Exception e) {
			LOGGER.error("Error reading size pass #2 {}", e);
			throw new FileNotReadyException(e);
		}
		
		//Size compare
		if(initFileSize != newFileSize) {
			throw new FileNotReadyException("File still being written, size doesnot match");
		}
	}
	
}
