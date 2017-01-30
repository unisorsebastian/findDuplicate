package ro.jmind.service;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public interface FileService {
	List<File> getFileList(String location);
	List<FileDetail> gatherFilesDetail(List<File> files);
	List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail);
	List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetailLeft, List<FileDetail> filesDetailRight);
}
