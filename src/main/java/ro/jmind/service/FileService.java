package ro.jmind.service;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.model.SampleData;

public interface FileService {
	/**
	 * returns the list of files in the location
	 * */
	List<File> getFileList(String location);
	/**
	 * creates <code>FileDetail</code> list from list of <code>File</code>
	 * */
	List<FileDetail> getFileDetailList(List<File> files);
	/**
	 * creates <code>FileDetail</code> list from list of <code>String</code> representing location of the file
	 * */
	List<FileDetail> getFileDetailListByFileName(List<String> files);
	/**
	 * set attribute <code>readyForDeletion</code> on true for all files that are duplicated
	 * */
	List<FileDetail> markForDeletion(List<DuplicateFileDetail> duplicateFilesList);
	
	/**
	 * creates a list of duplicated files grouped by SHA code<br>
	 * if <code>fileDetail</code> does not include SampleData, it will create it
	 * */
	List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail);
	/**
	 * calculates SHA1 from byte array<br>
	 * used to calculate hash just for part of the file(byte array)
	 * */
	String calculateHash(byte[] data);
	/**
	 * calculates SHA1 from file
	 * */
	String calculateHash(File file);
	
	/**
	 * updates <b><code>SampleData</code></b> model introducing <b>SHA1</b> and <b>time took</b> to generate it
	 * */
	void updateSampleData(FileDetail fileDetail);
	
	/**
	 * creates dummy file
	 * */
	File createTestFile(String fileLocation, int fileSizeInMB);
}
