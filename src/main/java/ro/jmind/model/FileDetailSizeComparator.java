package ro.jmind.model;

import java.util.Comparator;

public class FileDetailSizeComparator<T extends FileDetail> implements Comparator<FileDetail> {

	@Override
	public int compare(FileDetail o1, FileDetail o2) {
		long fileSize = o1.getFileSize()-o2.getFileSize();
		return fileSize>0?1:(fileSize<0?-1:0);
	}

	

}
