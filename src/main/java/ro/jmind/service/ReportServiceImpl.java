package ro.jmind.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class ReportServiceImpl implements ReportService {
    final String delimiter = "\t";
    
    @Override
    public void createFileListReport(List<File> fileList, String reportLocation) {
        reportLocation +="/fileListReport.txt";
        List<String> linesToWrite = new ArrayList<>();
        linesToWrite.add("file path");
        for (File f : fileList) {
            linesToWrite.add(f.getAbsolutePath());
        }
        createReportFile(linesToWrite, reportLocation);

    }

    @Override
    public void createFileDetailReport(List<FileDetail> fileDetailList, String reportLocation) {
    	reportLocation +="/fileDetailReport.txt";
    	List<String> linesToWrite = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder();
        sb.append("file hash");
        sb.append(delimiter);
        sb.append("file ext");
        sb.append(delimiter);
        sb.append("file path");
        sb.append(delimiter);
        sb.append("MB");
        sb.append(delimiter);
        sb.append("time in millis");
        linesToWrite.add(sb.toString());
        for(FileDetail fd:fileDetailList){
            sb.setLength(0);
            final long calculationTime = fd.getCalculationTime();

            sb.append(fd.getFileHash());
            sb.append(delimiter);
            sb.append(fd.getExtension());
            sb.append(delimiter);
            sb.append(fd.getAbsoluteFile());
            sb.append(delimiter);
            sb.append(fd.getHumanFileSize());
            sb.append(delimiter);
            sb.append(calculationTime);
            linesToWrite.add(sb.toString());
        }
        createReportFile(linesToWrite, reportLocation);
    }

    @Override
    public void createDuplicatedFileReport(List<DuplicateFileDetail> duplicates, String reportLocation) {
    	reportLocation +="/fileDuplicateReport.txt";
        List<String> linesToWrite = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sbTemp = new StringBuilder();
        Integer maximumNumberOfDuplicatedFiles = Integer.MIN_VALUE;
        
        //calculate maximumNumberOfDuplicatedFiles
        for(DuplicateFileDetail dfd:duplicates){
            final Set<FileDetail> duplicatedFiles = dfd.getDuplicates();
            if(maximumNumberOfDuplicatedFiles<=duplicatedFiles.size()){
                maximumNumberOfDuplicatedFiles = duplicatedFiles.size(); 
            }
        }
        
        //create header
        sb.append("hash");
        sb.append(delimiter);
        for(int i=0;i<maximumNumberOfDuplicatedFiles;i++){
            sb.append("file_"+(i+1));
            sb.append(delimiter);
            sbTemp.append("time in millis file_"+(i+1));
            sbTemp.append(delimiter);
        }
        sb.append(sbTemp);
        sb.append("MB");
        linesToWrite.add(sb.toString());

        //create body
        
        for(DuplicateFileDetail dfd:duplicates){
            sb.setLength(0);
            sbTemp.setLength(0);
            sb.append(dfd.getHash());
            sb.append(delimiter);
            final Set<FileDetail> duplicatedFiles = dfd.getDuplicates();
            int numberOfFiles = duplicatedFiles.size();
            String sizeMB="";
            for(FileDetail fd:duplicatedFiles){
            	sb.append(fd.getAbsoluteFile().getAbsolutePath());
            	sb.append(delimiter);
            	sbTemp.append(fd.getCalculationTime());
            	sbTemp.append(delimiter);
            	sizeMB=fd.getHumanFileSize();
            }
            for(int i=0;i<maximumNumberOfDuplicatedFiles-numberOfFiles;i++){
            	sb.append(delimiter);
            	sbTemp.append(delimiter);
            }
            sb.append(sbTemp);
            sb.append(sizeMB);
            linesToWrite.add(sb.toString());
        }
        
        createReportFile(linesToWrite, reportLocation);

    }

    
    private void createReportFile(List<String> linesToWrite, String reportLocation) {
        Path file = Paths.get(reportLocation);
        final File reportFile = file.toFile();
        if(reportFile.exists()){
            reportFile.delete();
        }
        try {
            Files.write(file, linesToWrite, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }
}
