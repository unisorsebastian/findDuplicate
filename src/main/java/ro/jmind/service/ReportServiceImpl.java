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

import ro.jmind.model.Constants;
import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class ReportServiceImpl implements ReportService {

	@Override
	public void createFileListReport(List<File> fileList, String reportFile) {
		List<String> linesToWrite = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("file path");
		sb.append(Constants.DELIMITER);
		sb.append("size in bytes");
		linesToWrite.add(sb.toString());
		for (File f : fileList) {
			sb.setLength(0);
			sb.append(f.getAbsolutePath());
			sb.append(Constants.DELIMITER);
			sb.append(f.length());
			linesToWrite.add(sb.toString());
		}
		createReportFile(linesToWrite, reportFile);

	}

	@Override
	public void createFileDetailReport(List<FileDetail> fileDetailList, String reportFile) {
		List<String> linesToWrite = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("file hash");
		sb.append(Constants.DELIMITER);
		sb.append("file ext");
		sb.append(Constants.DELIMITER);
		sb.append("file path");
		sb.append(Constants.DELIMITER);
		sb.append("MB");
		sb.append(Constants.DELIMITER);
		sb.append("time in millis");
		sb.append(Constants.DELIMITER);
		sb.append("used method");
		sb.append(Constants.DELIMITER);
		sb.append("mark for deletion");
		linesToWrite.add(sb.toString());
		for (FileDetail fd : fileDetailList) {
			sb.setLength(0);
			final long calculationTime = fd.getCalculationTime();

			sb.append(fd.getFileHash());
			sb.append(Constants.DELIMITER);
			sb.append(fd.getExtension());
			sb.append(Constants.DELIMITER);
			sb.append(fd.getAbsoluteFile());
			sb.append(Constants.DELIMITER);
			sb.append(fd.getHumanFileSize());
			sb.append(Constants.DELIMITER);
			sb.append(calculationTime);
			sb.append(Constants.DELIMITER);
			if (fd.getSampleData() != null) {
				sb.append(fd.getSampleData().getSampleDataMethod());
			} else {
				sb.append("");
			}
			sb.append(Constants.DELIMITER);
			sb.append(fd.isReadyForDeletion());
			linesToWrite.add(sb.toString());
		}
		createReportFile(linesToWrite, reportFile);
	}

	@Override
	public void createDuplicatedFileReport(List<DuplicateFileDetail> duplicates, String reportFile) {
		List<String> linesToWrite = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sbTemp = new StringBuilder();
		Integer maximumNumberOfDuplicatedFiles = Integer.MIN_VALUE;

		// calculate maximumNumberOfDuplicatedFiles
		for (DuplicateFileDetail dfd : duplicates) {
			final Set<FileDetail> duplicatedFiles = dfd.getDuplicates();
			if (maximumNumberOfDuplicatedFiles <= duplicatedFiles.size()) {
				maximumNumberOfDuplicatedFiles = duplicatedFiles.size();
			}
		}

		// create header
		sb.append("hash");
		sb.append(Constants.DELIMITER);
		for (int i = 0; i < maximumNumberOfDuplicatedFiles; i++) {
			sb.append("file_" + (i + 1));
			sb.append(Constants.DELIMITER);
			sbTemp.append("time in millis file_" + (i + 1));
			sbTemp.append(Constants.DELIMITER);
		}
		sb.append(sbTemp);
		sb.append("MB");
		linesToWrite.add(sb.toString());

		// create body
		for (DuplicateFileDetail dfd : duplicates) {
			sb.setLength(0);
			sbTemp.setLength(0);
			sb.append(dfd.getHash());
			sb.append(Constants.DELIMITER);
			final Set<FileDetail> duplicatedFiles = dfd.getDuplicates();
			int numberOfFiles = duplicatedFiles.size();
			String sizeMB = "";
			for (FileDetail fd : duplicatedFiles) {
				sb.append(fd.getAbsoluteFile().getAbsolutePath());
				sb.append(Constants.DELIMITER);
				sbTemp.append(fd.getCalculationTime());
				sbTemp.append(Constants.DELIMITER);
				sizeMB = fd.getHumanFileSize();
			}
			for (int i = 0; i < maximumNumberOfDuplicatedFiles - numberOfFiles; i++) {
				sb.append(Constants.DELIMITER);
				sbTemp.append(Constants.DELIMITER);
			}
			sb.append(sbTemp);
			sb.append(sizeMB);
			linesToWrite.add(sb.toString());
		}
		createReportFile(linesToWrite, reportFile);
	}

	private void createReportFile(List<String> linesToWrite, String report) {
		Path file = Paths.get(report);
		final File reportFile = file.toFile();
		if (reportFile.exists()) {
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
