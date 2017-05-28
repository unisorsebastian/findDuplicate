package ro.jmind.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class FileServiceImpl implements FileService {
	private List<File> files = new ArrayList<File>();
	private List<FileDetail> filesDetail = new ArrayList<FileDetail>();
	private List<DuplicateFileDetail> duplicateFilesList = new ArrayList<>();
	
	public List<FileDetail> gatherFilesDetail(List<File> files){
		int numberOfFiles = files.size();
		int i=1;
		for (File f : files) {
			FileDetail e = new FileDetail(f);
			filesDetail.add(e);
			System.out.println("processing file "+(i++)+" of "+numberOfFiles+";sha1: "+e.getFileHash()+";"+f.getAbsolutePath());
		}
		return filesDetail;
	}
	
	public List<File> getFileList(String location) {
		File directory = new File(location);
		gatherFilesInList(files,directory);
		return files;
	}
	
	@Override
	public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail) {
		return calculateDuplicates(filesDetail, filesDetail);
	}
	
	@Override
	public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetailLeft, List<FileDetail> filesDetailRight) {
		for(FileDetail fdLeft:filesDetail){
			String fileHashLeft = fdLeft.getFileHash();
			for(FileDetail fdRight:filesDetailRight){
				if(fdRight.getFileHash().equals(fileHashLeft) && fdLeft!=fdRight){
					boolean duplicateAdded = false;
					for(DuplicateFileDetail d:duplicateFilesList){
						if(d.getHash().equals(fdRight.getFileHash())){
							d.addDuplicate(fdRight);
							duplicateAdded = true;
							break;
						}
					}
					if(!duplicateAdded){
						DuplicateFileDetail duplicate = new DuplicateFileDetail();
						duplicate.addDuplicate(fdLeft);
						duplicate.addDuplicate(fdRight);
						duplicateFilesList.add(duplicate);
					}
				}
			}
			
		}
		
		return duplicateFilesList;
	}	

	private void gatherFilesInList(List<File> fileList, File directory) {
		File[] listFiles = directory.listFiles();
		for (File f : listFiles) {
			if (f.isDirectory()) {
				gatherFilesInList(fileList,f);
			} else {
				fileList.add(f);
			}
		}

	}

}