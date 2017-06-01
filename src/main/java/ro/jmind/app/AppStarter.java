package ro.jmind.app;

import static ro.jmind.model.Constants.RB;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.model.FileDetailSizeComparator;
import ro.jmind.model.SampleData;
import ro.jmind.service.FileService;
import ro.jmind.service.FileServiceImpl;
import ro.jmind.service.ReportService;
import ro.jmind.service.ReportServiceImpl;

public class AppStarter {

	private FileService fileService = new FileServiceImpl();
	private ReportService reportService = new ReportServiceImpl();

	static String folderLocation = RB.getString("base.folder");
	static String reportFilesFoundInSource = folderLocation + "\\" + RB.getString("report.filesFoundInSource");

	static String reportFilesDetails = folderLocation + "\\" + RB.getString("report.filesDetail");
	static String reportFilesDetailsLight = folderLocation + "\\" + RB.getString("report.filesDetailLight");

	static String reportFilesDuplicated = folderLocation + "\\" + RB.getString("report.filesDuplicated");
	static String reportFilesDuplicatedLight = folderLocation + "\\" + RB.getString("report.filesDuplicatedLight");

	static String reportFilesMarkedForDeletion = folderLocation + "\\" + RB.getString("report.filesMarkedForDeletion");
	static String reportFilesMarkedForDeletionLight = folderLocation + "\\"
			+ RB.getString("report.filesMarkedForDeletionLight");

	public void generateReports() {
		List<File> fileList = fileService.getFileList(folderLocation);
		List<FileDetail> fileDetail = fileService.getFileDetailList(fileList);
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(fileDetail);
		List<FileDetail> markAllForDeletion = fileService.markForDeletion(duplicates);

		reportService.createFileListReport(fileList, reportFilesFoundInSource);
		reportService.createFileDetailReport(fileDetail, reportFilesDetails);
		reportService.createDuplicatedFileReport(duplicates, reportFilesDuplicated);
		// mark for deletion report
		reportService.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletion);

	}

	public static void main(String... a) {
		AppStarter app = new AppStarter();
		FileService service = app.fileService;
		ReportService report = app.reportService;

		// String bigDummyFile = "D:/_media/photos/remove/fixedFileSize.bin";
		// service.createTestFile(bigDummyFile, 2500);

		List<File> fileListAll = service.getFileList(folderLocation);
		report.createFileListReport(fileListAll, folderLocation+"\\allFiles.txt");
		
		List<FileDetail> fileDetailLight = service.getFileDetailList(fileListAll);
		Collections.sort(fileDetailLight, new FileDetailSizeComparator<>());
		report.createFileDetailReport(fileDetailLight, folderLocation+"\\allFileDetailLight.txt");
		
		
		// clean filelist - remove .json files
		FileDetail fd = null;
		String extension = null;
		List<File> fileList = new ArrayList<>();
		for (File f : fileListAll) {
			fd = new FileDetail(f);
			extension = fd.getExtension();
			// fileList.add(f);
			if (!".json".equalsIgnoreCase(extension)) {
				fileList.add(f);
			}
		}

		List<FileDetail> fileDetail = service.getFileDetailList(fileList);
		Collections.sort(fileDetail, new FileDetailSizeComparator<>());
		int i=0;
		long startTime = System.currentTimeMillis();
//		for (FileDetail fda : fileDetail) {
//			SampleData calculateSampleData = service.calculateSampleData(fda);
//			System.out.println("file "+(++i)+" of "+" "+fileDetail.size()+" "+fda.getAbsoluteFile() + "\t"
//					+ calculateSampleData.getTimeTook() + "\t" +fda.getFileSize()+"\t"+calculateSampleData.getSampleDataMethod()+"\t"+ fda.getFileHash());
//
//		}
		System.out.println("done sample data in "+(System.currentTimeMillis()-startTime));
		report.createFileDetailReport(fileDetail, folderLocation+"\\filterFileDetail.txt");

		List<DuplicateFileDetail> calculateDuplicates = service.calculateDuplicates(fileDetail);
		report.createDuplicatedFileReport(calculateDuplicates, folderLocation+"\\reportDuplicates.txt");
//		List<FileDetail> markAllForDeletion = service.markForDeletion(calculateDuplicates);

//		report.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletion);

	}

}
