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

    public FileDetail(File file) {
        this.absoluteFile = file;
        fileSize = absoluteFile.length();
        try {
            //calculateFileHash(file);
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

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getFileHash() {
        return fileHash;
    }

    public long getCalculationTime() {
        return calculationTime;
    }

    public void setCalculationTime(long calculationTime) {
        this.calculationTime = calculationTime;
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

    public boolean isReadyForDeletion() {
        return readyForDeletion;
    }

    public void setReadyForDeletion(boolean readyForDeletion) {
        this.readyForDeletion = readyForDeletion;
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
        } else if (!absoluteFile.getAbsolutePath().equals(other.absoluteFile.getAbsolutePath()))
            return false;
        return true;
    }

    

}
