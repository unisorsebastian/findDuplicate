package ro.jmind.service;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public interface FileService {
	List<File> getFileList(String location);
	List<FileDetail> gatherFilesDetail(List<File> files);
	//List<FileDetail> gatherFilesDetailLight(List<File> files);
	List<FileDetail> gatherFilesDetailByFileName(List<String> files);
	//List<FileDetail> gatherFilesDetailLightByFileName(List<String> files);
	List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail);
	List<FileDetail> markForDeletion(List<DuplicateFileDetail> duplicateFilesList);
	String calculateHash(File file);
}
