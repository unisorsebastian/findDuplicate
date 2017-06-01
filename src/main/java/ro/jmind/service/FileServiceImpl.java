package ro.jmind.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.model.FileDetailSizeComparator;
import ro.jmind.model.SampleData;
import ro.jmind.model.SampleDataMethod;

public class FileServiceImpl implements FileService {
	// private List<FileDetail> filesDetail = new ArrayList<FileDetail>();

	@Override
	public List<File> getFileList(String location) {
		File directory = new File(location);
		List<File> files = new ArrayList<File>();
		gatherFilesInList(files, directory);
		return files;
	}

	@Override
	public String calculateHash(byte[] data) {
		String result = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		md.update(data);
		byte[] mdbytes = md.digest();
		// convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		result = sb.toString();
		return result;
	}

	@Override
	public String calculateHash(File file) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			FileInputStream fis = new FileInputStream(file);
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			fis.close();
			byte[] mdbytes = md.digest();

			// convert the byte to hex format
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			result = sb.toString();
		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<FileDetail> getFileDetailList(List<File> files) {
		return createFileDetails(files);
	}

	@Override
	public List<FileDetail> getFileDetailListByFileName(List<String> files) {
		List<File> fileList = new ArrayList<>();
		for (String s : files) {
			fileList.add(new File(s));
		}

		return createFileDetails(fileList);
	}

	@Override
	public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail) {
		List<FileDetail> rightList = new ArrayList<>(filesDetail);
		return calculateDuplicates(filesDetail, rightList);
	}

	@Override
	public File createTestFile(String fileLocation, int fileSizeInMB) {
		File file = new File(fileLocation);
		try {
			Path path = Paths.get(fileLocation);
			long fileSizeInBytes = (long) fileSizeInMB * 1024 * 1024;
			if (file.exists()) {
				file.delete();
			}

			// 10MB buffer
			final int bufferSize = 10 * 1024 * 1024;
			// final int twoMB = 1024 * 1024 * 2;
			byte[] aBytes = new byte[bufferSize];

			while (fileSizeInBytes > 0) {
				for (int i = 0; i < bufferSize; i++) {
					aBytes[i] = 127;
				}
				Files.write(path, aBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				fileSizeInBytes = fileSizeInBytes - bufferSize;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	@Override
	public void updateSampleData(FileDetail fileDetail) {
		if (fileDetail.getSampleData() != null) {
			return;
		}
		final long startTime = System.currentTimeMillis();
		long timeTook = 0;
		File file = fileDetail.getAbsoluteFile();
		// 1GB
		final long fileMaxSize = 1 * 1024 * 1024 * 1024;
		final long fileSize = file.length();
		// buffer reader set at 4KB
		final int halfBufferSize = 2048;
		final int bufferSize = halfBufferSize * 2;
		final int bufferMultiplicator = 512;
		final byte[] buffer = new byte[bufferSize];
		// 4KB*512=2MB
		final int sampleDataSize = bufferSize * bufferMultiplicator;
		byte[] sampleDataByteArray = new byte[3 * sampleDataSize];

		final long idxMiddle = fileSize / 2 - 1;
		final long idxMiddleStartRead = idxMiddle - sampleDataSize / 2 + 1;
		final long idxMiddleEndRead = idxMiddleStartRead + sampleDataSize - 1;

		final long skipBytesFromEndOfFirstSectionToMiddle = idxMiddleStartRead - sampleDataSize;

		final long idxLastStarRead = fileSize - sampleDataSize;
		final long skipBytesFromMiddleToEnd = idxLastStarRead - idxMiddleEndRead - 1;

		int copyIdx = 0;

		int readSize = 0;
		SampleData result;
		FileInputStream fis;
		try {
			SampleDataMethod sampleMethod = SampleDataMethod.FULL_SIZE_BYTE_ARRAY;
			if (fileSize < sampleDataByteArray.length) {
				timeTook = (System.currentTimeMillis() - startTime) == 0 ? 1 : (System.currentTimeMillis() - startTime);
				result = new SampleData(timeTook, sampleMethod, calculateHash(file));
				fileDetail.setSampleData(result);
				return;
			}

			fis = new FileInputStream(file);
			for (int part = 0; part < 3; part++) {
				sampleMethod = SampleDataMethod.PARTIAL_SIZE_BYTE_ARRAY;
				if (fileSize > fileMaxSize) {
					sampleMethod = SampleDataMethod.PARTIAL_SIZE_BIG_FILE_BYTE_ARRAY;
				}
				if (part == 1) {
					// skip to middle start point
					fis.skip(skipBytesFromEndOfFirstSectionToMiddle);
				}
				if (part == 2) {
					// skip to last part of the file
					fis.skip(skipBytesFromMiddleToEnd);
				}
				for (int i = 0; i < bufferMultiplicator; i++) {
					readSize = fis.read(buffer, 0, buffer.length);
					if (readSize != buffer.length) {
						fis.close();
						throw new RuntimeException("something is wrong, readSize!=1024");
					}
					System.arraycopy(buffer, 0, sampleDataByteArray, copyIdx, buffer.length);
					copyIdx = copyIdx + buffer.length;
				}
			}
			fis.close();
			timeTook = (System.currentTimeMillis() - startTime) == 0 ? 1 : (System.currentTimeMillis() - startTime);
			result = new SampleData(timeTook, sampleMethod, calculateHash(sampleDataByteArray));
			fileDetail.setSampleData(result);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileDetail.setSampleData((new SampleData(1, SampleDataMethod.FAIL, "")));
		return;
	}

	@Override
	public List<FileDetail> markForDeletion(List<DuplicateFileDetail> duplicateFilesList) {
		List<FileDetail> result = new ArrayList<>();
		for (DuplicateFileDetail dfd : duplicateFilesList) {
			final Set<FileDetail> allDuplicatesOfTheFile = dfd.getDuplicates();
			int i = 0;
			for (FileDetail fd : allDuplicatesOfTheFile) {
				if (i == 0) {
					fd.setReadyForDeletion(false);
				} else {
					fd.setReadyForDeletion(true);
				}
				result.add(fd);
				i++;
			}
		}
		return result;
	}

	private void gatherFilesInList(List<File> fileList, File directory) {
		File[] listFiles = directory.listFiles();
		for (File f : listFiles) {
			if (f.isDirectory()) {
				gatherFilesInList(fileList, f);
			} else {
				fileList.add(f);
			}
		}
	}

	private List<FileDetail> createFileDetails(List<File> files) {
		int numberOfFiles = files.size();
		int i = 1;
		Object params[] = new Object[5];
		List<FileDetail> result = new ArrayList<>();
		for (File f : files) {
			FileDetail e = new FileDetail(f);
			result.add(e);
			params[0] = i;
			params[1] = numberOfFiles;
			params[2] = e.getHumanFileSize();
			params[3] = e.getFileHash();
			params[4] = f.getAbsolutePath();

			// TODO add logging
			System.out.println(String.format("Create FileDetail, file number %1s out of %2s; size %3s; sha1 %4s; file %5s", params));
			i++;
		}
		return result;
	}

	private List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetailLeft, List<FileDetail> filesDetailRight) {
		List<DuplicateFileDetail> duplicateFilesList = new ArrayList<>();

		Collections.sort(filesDetailLeft, new FileDetailSizeComparator<>());
		Collections.sort(filesDetailRight, new FileDetailSizeComparator<>());

		String leftPath, rightPath;

		int indexFile = 1;
		FileDetail rightElemToRemove = null;
		for (FileDetail fdLeft : filesDetailLeft) {
			leftPath = fdLeft.getAbsoluteFile().getAbsolutePath();
			System.out.println("Calculate duplicatates " + indexFile + " of " + filesDetailLeft.size() + ", file:"
			        + fdLeft.getAbsoluteFile().getName());
			indexFile++;
			// remove the elem from right list
			if (rightElemToRemove != null) {
				Iterator<FileDetail> it = filesDetailRight.iterator();
				FileDetail next = null;
				while (it.hasNext()) {
					next = it.next();
					if (next == rightElemToRemove) {
						it.remove();
						rightElemToRemove = null;
						break;
					}
				}

			}
			for (FileDetail fdRight : filesDetailRight) {
				rightPath = fdRight.getAbsoluteFile().getAbsolutePath();
				if (leftPath.equals(rightPath)) {
					continue;
				}
				if (fdLeft.getFileSize() == fdRight.getFileSize()) {
					// could be duplicated because of the exact size

					// update sampleData to check for duplicate using SHA1
					updateSampleData(fdLeft);
					updateSampleData(fdRight);

					if (fdLeft.getSampleData().equals(fdRight.getSampleData())) {
						// set it as duplicate because of the same hash in
						// sampleData
						// TODO remove right element from list
						rightElemToRemove = fdRight;
						boolean duplicateAdded = false;
						for (DuplicateFileDetail d : duplicateFilesList) {
							if (d.getHash().equals(fdRight.getSampleData().getHash())) {
								d.addDuplicate(fdRight);
								duplicateAdded = true;
								break;
							}
						}
						if (!duplicateAdded) {
							DuplicateFileDetail duplicate = new DuplicateFileDetail();
							duplicate.addDuplicate(fdLeft);
							duplicate.addDuplicate(fdRight);
							duplicateFilesList.add(duplicate);
						}
					}
				}
			}
		}

		return duplicateFilesList;
	}
}
