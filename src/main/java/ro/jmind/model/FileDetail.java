package ro.jmind.model;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileDetail {
	private File absoluteFile;
	private String fileHash;
	
	
	public FileDetail(File file) {
		this.absoluteFile = file;
		try {
			calculateFileHash(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public File getAbsoluteFile() {
		return absoluteFile;
	}



	public String getFileHash() {
		return fileHash;
	}
	
	
	
	@Override
	public String toString() {
		return absoluteFile.getAbsolutePath();
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((absoluteFile == null) ? 0 : absoluteFile.hashCode());
		result = prime * result + ((fileHash == null) ? 0 : fileHash.hashCode());
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
		return true;
	}


	private void calculateFileHash(File file) throws Exception{
	    MessageDigest md = MessageDigest.getInstance("SHA1");
	    FileInputStream fis = new FileInputStream(file);
	    byte[] dataBytes = new byte[1024];

	    int nread = 0;

	    while ((nread = fis.read(dataBytes)) != -1) {
	      md.update(dataBytes, 0, nread);
	    };
	    fis.close();
	    byte[] mdbytes = md.digest();

	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < mdbytes.length; i++) {
	    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
		fileHash = sb.toString();
	}

}
