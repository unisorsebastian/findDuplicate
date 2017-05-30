package ro.jmind.model;

import java.util.HashSet;
import java.util.Set;

public class DuplicateFileDetail {
	private String hash;
	private Set<FileDetail> duplicates = new HashSet<>();
	
	public String getHash() {
		return hash;
	}

	public boolean addDuplicate(FileDetail fileDetail) {
		boolean result = false;
	    if (hash == null) {
			hash = fileDetail.getFileHash();
		}
		if (fileDetail.getFileHash().equals(hash)) {
			result = duplicates.add(fileDetail);
		}
		return result;
	}

	public Set<FileDetail> getDuplicates() {
		return duplicates;
	}

	@Override
	public String toString() {
		return "DuplicateFileDetail hash=" + hash + "\n\tduplicates=" + duplicates +"\n";
	}
	

}
