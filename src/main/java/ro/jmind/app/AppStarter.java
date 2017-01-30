package ro.jmind.app;

import java.io.File;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;
import ro.jmind.service.FileService;
import ro.jmind.service.FileServiceImpl;

public class AppStarter {

	public AppStarter() {
		
	}
	public static void main(String ...a){
		String folderLocation = "D:/_media/photos";
		FileService fileService = new FileServiceImpl();
		List<File> fileList = fileService.getFileList(folderLocation);
		List<FileDetail> gatherFilesDetail = fileService.gatherFilesDetail(fileList);
		List<DuplicateFileDetail> duplicates = fileService.calculateDuplicates(gatherFilesDetail);
		//System.out.println(duplicates);
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
