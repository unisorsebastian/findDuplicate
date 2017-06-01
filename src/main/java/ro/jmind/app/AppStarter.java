package ro.jmind.app;

import static ro.jmind.model.Constants.RB;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.activation.FileDataSource;

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
	static String reportFilesMarkedForDeletionLight = folderLocation + "\\" + RB.getString("report.filesMarkedForDeletionLight");

	public static void main(String... a) {
		long startTime = System.currentTimeMillis();
		AppStarter app = new AppStarter();
		// app.generateReports();
		// app.generateFullWithoutJsonReports();
		app.generateFullReports();
		int totalTimeInSec = (int) (System.currentTimeMillis() - startTime) / 1000;
		System.out.println("total time:" + totalTimeInSec + " seconds");
		System.out.println("---DONE---");

	}

	public void generateReports() {
		List<File> fileList = fileService.getFileList(folderLocation);
		List<FileDetail> allFileDetail = fileService.getFileDetailList(fileList);

		// filter some of the files from list .json
		List<FileDetail> fileDetail = new ArrayList<>();
		for (FileDetail fd : allFileDetail) {
			if (!".json".equalsIgnoreCase(fd.getExtension())) {
				fileDetail.add(fd);
			}
		}

		// this is the most time consuming if FileDetail does not contain
		// SampleData
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(fileDetail);
		List<FileDetail> markAllForDeletion = fileService.markForDeletion(duplicates);

		reportService.createFileListReport(fileList, reportFilesFoundInSource);
		reportService.createFileDetailReport(fileDetail, reportFilesDetails);
		reportService.createDuplicatedFileReport(duplicates, reportFilesDuplicated);
		// mark for deletion report
		reportService.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletion);

	}

	public void generateFullWithoutJsonReports() {
		List<File> fileList = fileService.getFileList(folderLocation);
		List<FileDetail> allFileDetail = fileService.getFileDetailList(fileList);

		// filter some of the files from list .json
		List<FileDetail> fileDetail = new ArrayList<>();
		for (FileDetail fd : allFileDetail) {
			if (!".json".equalsIgnoreCase(fd.getExtension())) {
				fileDetail.add(fd);
			}
		}

		// populate SamapleDate on FileDetail
		// this is most time consuming bacause of updateSampleData
		File absoluteFile = null;
		int fileCounter = 0;
		int allFileDetailSize = fileDetail.size();
		for (FileDetail fd : fileDetail) {
			fileService.updateSampleData(fd);
			absoluteFile = fd.getAbsoluteFile();
			System.out.println("updateSampleData file " + (++fileCounter) + " of " + allFileDetailSize + " timeTook:"
			        + fd.getCalculationTime() + " MB:" + fd.getHumanFileSize() + " name:" + absoluteFile.getName());
		}

		// this is NOT the most time consuming because FileDetail does contain
		// SampleData
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(fileDetail);
		List<FileDetail> markAllForDeletion = fileService.markForDeletion(duplicates);

		reportService.createFileListReport(fileList, reportFilesFoundInSource);
		reportService.createFileDetailReport(fileDetail, reportFilesDetails);
		reportService.createDuplicatedFileReport(duplicates, reportFilesDuplicated);
		// mark for deletion report
		reportService.createFileDetailReport(markAllForDeletion, reportFilesMarkedForDeletion);

	}

	public void generateFullReports() {
		List<File> fileList = fileService.getFileList(folderLocation);
		reportService.createFileListReport(fileList, folderLocation + "\\reportAllFiles.txt");
		List<FileDetail> allFileDetail = fileService.getFileDetailList(fileList);
		reportService.createFileDetailReport(allFileDetail, folderLocation + "\\reportLightFileDetail.txt");

		// populate SamapleDate on FileDetail
		// this is most time consuming because of updateSampleData
		File absoluteFile = null;
		int fileCounter = 0;
		int allFileDetailSize = allFileDetail.size();
		for (FileDetail fd : allFileDetail) {
			fileService.updateSampleData(fd);
			absoluteFile = fd.getAbsoluteFile();
			System.out.println("updateSampleData file " + (++fileCounter) + " of " + allFileDetailSize + " timeTook:"
			        + fd.getCalculationTime() + " MB:" + fd.getHumanFileSize() + " name:" + absoluteFile.getName());
		}
		reportService.createFileDetailReport(allFileDetail, folderLocation + "\\reportFullFileDetail.txt");

		// this is NOT the most time consuming because FileDetail already contains SampleData
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(allFileDetail);
		List<FileDetail> markAllForDeletion = fileService.markForDeletion(duplicates);

		reportService.createDuplicatedFileReport(duplicates, folderLocation + "\\reportDuplicates.txt");
		// mark for deletion report
		reportService.createFileDetailReport(markAllForDeletion, folderLocation + "\\reportReadyForDeletion.txt");

	}

}
