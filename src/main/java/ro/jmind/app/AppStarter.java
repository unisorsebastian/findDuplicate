package ro.jmind.app;

import static ro.jmind.model.Constants.RB;

import java.io.File;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.service.FileService;
import ro.jmind.service.FileServiceImpl;
import ro.jmind.service.FileServiceLightImpl;
import ro.jmind.service.ReportService;
import ro.jmind.service.ReportServiceImpl;

public class AppStarter {
    
    FileService fileService = new FileServiceImpl();
    FileService fileServiceLight = new FileServiceLightImpl();
    ReportService reportService = new ReportServiceImpl();
    
    String folderLocation = RB.getString("base.folder");
    String reportFilesFoundInSource = folderLocation + "\\" + RB.getString("report.filesFoundInSource");
    
    String reportFilesDetails = folderLocation + "\\" + RB.getString("report.filesDetail");
    String reportFilesDetailsLight = folderLocation + "\\" + RB.getString("report.filesDetailLight");
    
    String reportFilesDuplicated = folderLocation + "\\" + RB.getString("report.filesDuplicated");
    String reportFilesDuplicatedLight = folderLocation + "\\" + RB.getString("report.filesDuplicatedLight");
    
    String reportFilesMarkedForDeletion = folderLocation + "\\" + RB.getString("report.filesMarkedForDeletion");
    String reportFilesMarkedForDeletionLight = folderLocation + "\\" + RB.getString("report.filesMarkedForDeletionLight");

    public AppStarter() {

    }
    
    public void generateReportsLight(){
        List<File> fileList = fileServiceLight.getFileList(folderLocation);
        List<FileDetail> fileDetailLight = fileServiceLight.gatherFilesDetail(fileList);
        List<DuplicateFileDetail> duplicates = fileServiceLight.calculateDuplicates(fileDetailLight);
        List<FileDetail> markAllForDeletion = fileServiceLight.markForDeletion(duplicates);
        
        reportService.createFileListReport(fileList, reportFilesFoundInSource);
        reportService.createFileDetailReport(fileDetailLight, reportFilesDetailsLight);
        reportService.createDuplicatedFileReport(duplicates, reportFilesDuplicatedLight);
        reportService.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletionLight);

    }
    
    public void generateReports(){
        List<File> fileList = fileService.getFileList(folderLocation);
        List<FileDetail> fileDetail = fileService.gatherFilesDetail(fileList);
        List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(fileDetail);
        List<FileDetail> markAllForDeletion = fileService.markForDeletion(duplicates);
        
        reportService.createFileListReport(fileList, reportFilesFoundInSource);
        reportService.createFileDetailReport(fileDetail, reportFilesDetails);
        reportService.createDuplicatedFileReport(duplicates, reportFilesDuplicated);
        reportService.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletion);

    }

    public static void main(String... a) {
        AppStarter app = new AppStarter();
        
//        long startTime = System.currentTimeMillis();
//        app.generateReportsLight();
//        System.out.println("Light service done in:" + (System.currentTimeMillis() - startTime));
//        
//        startTime = System.currentTimeMillis();
//        app.generateReports();
//        System.out.println("Full service done in:" + (System.currentTimeMillis() - startTime));
        
        //app.fileServiceLight.calculateHash(new File("C:/Users/sunisor/remove/New folder/20170523_112313.mp4"));
        FileServiceLightImpl serv =(FileServiceLightImpl)app.fileServiceLight;
        serv.createTestFile();

    }

}
