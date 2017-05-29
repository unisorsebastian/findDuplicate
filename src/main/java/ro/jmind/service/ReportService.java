package ro.jmind.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public interface ReportService {
    void createFileListReport(List<File> fileList, String reportLocation);

    void createFileDetailReport(List<FileDetail> fileDetailList, String reportLocation);

    void createDuplicatedFileReport(List<DuplicateFileDetail> duplicates, String reportLocation);

}
