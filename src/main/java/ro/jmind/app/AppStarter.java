package ro.jmind.app;

import java.io.File;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.service.FileService;
import ro.jmind.service.FileServiceImpl;
import ro.jmind.service.ReportService;
import ro.jmind.service.ReportServiceImpl;

public class AppStarter {

	public AppStarter() {
		
	}
	public static void main(String ...a){
//		String folderLocation = "D:/_media/photos";
		String folderLocation = "D:\\_media\\photos\\remove";
		
		FileService fileService = new FileServiceImpl();
		ReportService reportService = new ReportServiceImpl();
		
		
		List<File> fileList = fileService.getFileList(folderLocation);
		reportService.createFileListReport(fileList, folderLocation);
		
//		TODO remove
		List<String> fileLocation = new ArrayList<>();
		for(File f:fileList){
			fileLocation.add(f.getAbsolutePath());
		}
		List<FileDetail> test  = fileService.gatherFilesDetailByFileName(fileLocation);
		List<DuplicateFileDetail> duplicatesByString = fileService.calculateDuplicates(test);
//      TODO remove end		
		
		
		List<FileDetail> gatherFilesDetail = fileService.gatherFilesDetail(fileList);
		reportService.createFileDetailReport(gatherFilesDetail, folderLocation);
		
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(gatherFilesDetail);
		reportService.createDuplicatedFileReport(duplicates, folderLocation);
		
		
		String hash = null;
		StringBuilder sb = null;
		Set<FileDetail> fileDetails = null;
		for(DuplicateFileDetail d:duplicates){
			hash = d.getHash();
			sb = new StringBuilder();
			sb.append("hash:"+hash+"\n");
			fileDetails = d.getDuplicates();
			for(FileDetail fd:fileDetails){
				sb.append("\t"+fd.getAbsoluteFile()+"\n");
			}
			System.out.println(sb);
		}
		
	}

}
