package ro.jmind.model;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;

public class FileDetail {
    private File absoluteFile;
    private String fileHash;
    private long calculationTime;
    private long calculationTimeLight;
    private long fileSize;
    private String humanFileSize;
    private String extension;

    public FileDetail(File file) {
        long start = System.currentTimeMillis();
        this.absoluteFile = file;
        fileSize = absoluteFile.length();
        try {
            calculateFileHash(file);
            getHumanFileSize();
            getExtension();
            calculationTime = System.currentTimeMillis() - start;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public FileDetail(String fileName) {
        long start = System.currentTimeMillis();
        this.absoluteFile = new File(fileName);
        fileSize = absoluteFile.length();
        try {
            getHumanFileSize();
            getExtension();
            calculationTime = System.currentTimeMillis() - start;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public File getAbsoluteFile() {
        return absoluteFile;
    }

    public String getFileHash() {
//        if (fileHash == null || fileHash.length() < 1) {
//            try {
//                calculateFileHash(absoluteFile);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        return fileHash;
    }

    public long getCalculationTime() {
        return calculationTime;
    }

    public long getCalculationTimeLight() {
        return calculationTimeLight;
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

    private void calculateFileHash(File file) throws Exception {
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
        fileHash = sb.toString();
    }

}
