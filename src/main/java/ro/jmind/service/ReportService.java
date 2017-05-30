package ro.jmind.service;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public interface ReportService {
    void createFileListReport(List<File> fileList, String reportFile);

    void createFileDetailReport(List<FileDetail> fileDetailList, String reportFile);


    void createDuplicatedFileReport(List<DuplicateFileDetail> duplicates, String reportFile);

}
