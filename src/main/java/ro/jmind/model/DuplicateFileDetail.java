package ro.jmind.model;

import java.util.HashSet;
import java.util.Set;

public class DuplicateFileDetail {
	private String hash;
	private Set<FileDetail> duplicates = new HashSet<>();
	
	public String getHash() {
		return hash;
	}

	public void addDuplicate(FileDetail fileDetail) {
		if (hash == null) {
			hash = fileDetail.getFileHash();
		}
		if (fileDetail.getFileHash().equals(hash)) {
			duplicates.add(fileDetail);
		}
	}

	public Set<FileDetail> getDuplicates() {
		return duplicates;
	}

	@Override
	public String toString() {
		return "DuplicateFileDetail hash=" + hash + "\n\tduplicates=" + duplicates +"\n";
	}
	

}
