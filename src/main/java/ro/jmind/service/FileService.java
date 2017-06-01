package ro.jmind.service;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.model.SampleData;

public interface FileService {
	List<File> getFileList(String location);
	List<FileDetail> getFileDetailList(List<File> files);
	List<FileDetail> getFileDetailListByFileName(List<String> files);
	List<FileDetail> markForDeletion(List<DuplicateFileDetail> duplicateFilesList);
	List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail);
	String calculateHash(byte[] data);
	String calculateHash(File file);
	SampleData calculateSampleData(FileDetail fileDetail);
	File createTestFile(String fileLocation, int fileSizeInMB);
}
