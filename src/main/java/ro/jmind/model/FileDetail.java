package ro.jmind.model;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;

public class FileDetail {
	private File absoluteFile;
	private String fileHash;
	private long calculationTime;
	private long fileSize;
	private String humanFileSize;
	private String extension;
	private boolean readyForDeletion = false;
	private SampleData sampleData;

	public FileDetail(File file) {
		this.absoluteFile = file;
		fileSize = absoluteFile.length();
		try {
			getHumanFileSize();
			getExtension();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileDetail(String fileName) {
		this(new File(fileName));
	}

	public File getAbsoluteFile() {
		return absoluteFile;
	}

	public String getFileHash() {
		return fileHash;
	}

	public long getCalculationTime() {
		return calculationTime;
	}

	
	public long getFileSize() {
		return fileSize;
	}

	public String getHumanFileSize() {
		if (humanFileSize != null) {
			return humanFileSize;
		}
		double mega = ((double) fileSize / 1024) / 1024;
		BigDecimal megaBytes = BigDecimal.valueOf(mega);
		megaBytes = megaBytes.setScale(2, BigDecimal.ROUND_HALF_UP);
		if(megaBytes.doubleValue()<0.01){
			megaBytes = new BigDecimal("0.01");
		}
		return humanFileSize = megaBytes.toString();
	}

	public String getExtension() {
		if (extension != null) {
			return extension;
		}
		String name = absoluteFile.getName();
		int extPos = name.lastIndexOf(".");
		if (extPos > 0) {
			extension = name.substring(extPos);
		} else {
			extension = "";
		}
		return extension;
	}

	public SampleData getSampleData() {
		return sampleData;
	}

	public void setSampleData(SampleData sampleData) {
		if(this.sampleData==null){
			this.sampleData = sampleData;
			this.fileHash = sampleData.getHash();
			this.calculationTime = sampleData.getTimeTook();
		}
	}

	public boolean isReadyForDeletion() {
		return readyForDeletion;
	}

	public void setReadyForDeletion(boolean readyForDeletion) {
		this.readyForDeletion = readyForDeletion;
	}

	
	@Override
	public String toString() {
		return fileSize + "\t" + absoluteFile + ", fileHash=" + fileHash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fileSize ^ (fileSize >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileDetail other = (FileDetail) obj;
		if (absoluteFile == null) {
			if (other.absoluteFile != null)
				return false;
		} else if (!absoluteFile.equals(other.absoluteFile))
			return false;
		if (fileHash == null) {
			if (other.fileHash != null)
				return false;
		} else if (!fileHash.equals(other.fileHash))
			return false;
		if (fileSize != other.fileSize)
			return false;
		return true;
	}

}
