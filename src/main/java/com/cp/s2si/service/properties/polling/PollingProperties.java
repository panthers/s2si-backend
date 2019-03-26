package com.cp.s2si.service.properties.polling;

public class PollingProperties {
	
	private String pathToPoll; //Always a folder.
	private boolean checkForAnyFileInFolderPath; //Any file in folder triggers pickup.
	private String fileNameToCheck; //Only this filename exist triggers pickup. This file will not be picked up.
	private String fileNamePrefixToPickup; //File names with prefix to pickup.
	private String fileNameSuffixToPickup; //File names with suffix to pickup.
	private String fileNameRegexToPickup; //File names with regex to pickup.
	private boolean deleteAfterPickup; //Should delete after pickup of file.
	private boolean moveAfterPickup; //Should move after pickup of file.
	private String moveToPathOnSuccess; //Path to move files to after pickup. Relative to path to poll.
	private String moveToPathOnFailure; //Path to move files to after pickup. Relative to path to poll.
	private boolean renameFile; //Should rename file or not.
	private String renamePrefix; //Rename prefix to use if rename is required.
	private String renameNameSuffix; //Rename suffix to use if rename is required after name and before extension.
	private String renameExtensionSuffix; //Rename suffix to use if rename is required after name and before extension.
	private String lockFilePreffix; //Lock file prefix for a file that has not been completely written.
	private String lockFileSuffix; //Lock file suffix for a file that has not been completely written.
	private Integer sizeCheckIntervalInMilliseconds; //Check size on this interval to make sure the file is not still being written to.
	
	public String getPathToPoll() {
		return pathToPoll;
	}
	public void setPathToPoll(String pathToPoll) {
		this.pathToPoll = pathToPoll;
	}
	public boolean isCheckForAnyFileInFolderPath() {
		return checkForAnyFileInFolderPath;
	}
	public void setCheckForAnyFileInFolderPath(boolean checkForAnyFileInFolderPath) {
		this.checkForAnyFileInFolderPath = checkForAnyFileInFolderPath;
	}
	public String getFileNameToCheck() {
		return fileNameToCheck;
	}
	public void setFileNameToCheck(String fileNameToCheck) {
		this.fileNameToCheck = fileNameToCheck;
	}
	public String getFileNamePrefixToPickup() {
		return fileNamePrefixToPickup;
	}
	public void setFileNamePrefixToPickup(String fileNamePrefixToPickup) {
		this.fileNamePrefixToPickup = fileNamePrefixToPickup;
	}
	public String getFileNameSuffixToPickup() {
		return fileNameSuffixToPickup;
	}
	public void setFileNameSuffixToPickup(String fileNameSuffixToPickup) {
		this.fileNameSuffixToPickup = fileNameSuffixToPickup;
	}
	public String getFileNameRegexToPickup() {
		return fileNameRegexToPickup;
	}
	public void setFileNameRegexToPickup(String fileNameRegexToPickup) {
		this.fileNameRegexToPickup = fileNameRegexToPickup;
	}
	public boolean isDeleteAfterPickup() {
		return deleteAfterPickup;
	}
	public void setDeleteAfterPickup(boolean deleteAfterPickup) {
		this.deleteAfterPickup = deleteAfterPickup;
	}
	public boolean isMoveAfterPickup() {
		return moveAfterPickup;
	}
	public void setMoveAfterPickup(boolean moveAfterPickup) {
		this.moveAfterPickup = moveAfterPickup;
	}
	public String getMoveToPathOnSuccess() {
		return moveToPathOnSuccess;
	}
	public void setMoveToPathOnSuccess(String moveToPathOnSuccess) {
		this.moveToPathOnSuccess = moveToPathOnSuccess;
	}
	public String getMoveToPathOnFailure() {
		return moveToPathOnFailure;
	}
	public void setMoveToPathOnFailure(String moveToPathOnFailure) {
		this.moveToPathOnFailure = moveToPathOnFailure;
	}
	public boolean isRenameFile() {
		return renameFile;
	}
	public void setRenameFile(boolean renameFile) {
		this.renameFile = renameFile;
	}
	public String getRenamePrefix() {
		return renamePrefix;
	}
	public void setRenamePrefix(String renamePrefix) {
		this.renamePrefix = renamePrefix;
	}
	public String getLockFilePreffix() {
		return lockFilePreffix;
	}
	public void setLockFilePreffix(String lockFilePreffix) {
		this.lockFilePreffix = lockFilePreffix;
	}
	public String getLockFileSuffix() {
		return lockFileSuffix;
	}
	public void setLockFileSuffix(String lockFileSuffix) {
		this.lockFileSuffix = lockFileSuffix;
	}
	public Integer getSizeCheckIntervalInMilliseconds() {
		return sizeCheckIntervalInMilliseconds;
	}
	public void setSizeCheckIntervalInMilliseconds(Integer sizeCheckIntervalInMilliseconds) {
		this.sizeCheckIntervalInMilliseconds = sizeCheckIntervalInMilliseconds;
	}
	public String getRenameNameSuffix() {
		return renameNameSuffix;
	}
	public void setRenameNameSuffix(String renameNameSuffix) {
		this.renameNameSuffix = renameNameSuffix;
	}
	public String getRenameExtensionSuffix() {
		return renameExtensionSuffix;
	}
	public void setRenameExtensionSuffix(String renameExtensionSuffix) {
		this.renameExtensionSuffix = renameExtensionSuffix;
	}
	
}