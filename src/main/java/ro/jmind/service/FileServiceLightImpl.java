package ro.jmind.service;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class FileServiceLightImpl extends FileServiceImpl {
    private List<FileDetail> filesDetail = new ArrayList<FileDetail>();

    @Override
    public List<FileDetail> gatherFilesDetail(List<File> files) {
        final List<FileDetail> gatherFilesDetail = createFileDetails(files);
        return gatherFilesDetail;
    }

    @Override
    public String calculateHash(File file) {
        String result = null;
        int oneKbInBytes = 1024;
        int oneMbInBytes = 1024*oneKbInBytes;
        int fiveMbInBytes = 5*oneMbInBytes;
        
        int noOfMB = 5 * 1024;
        int bytesTo5MB=1024*noOfMB;
        final long fileSize = file.length();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];

            int nread = 0;
            int j = 0;


            if (fileSize > 2*fiveMbInBytes) {
                System.out.println("filesize:"+fileSize+" filenae:"+file.getName());
                int readSize=0;
                for (int k = 0; k < noOfMB;k++) {
                    //read 1KB
                    readSize=fis.read(dataBytes, 0, dataBytes.length);
                    if(readSize<1024){
                        System.out.println("do something");
                    }
                }
                //this should got at the last 5MB of the file
                final long skipBytes = fileSize-2*bytesTo5MB-2;
                fis.skip(skipBytes);
                
                //read last 5MB
                for (int k = 0; k < noOfMB;k++) {
                    //read 1KB
                    readSize=fis.read(dataBytes, 0, dataBytes.length);
                    if(readSize<1024){
                        System.out.println("do something");
                    }
                }
            }
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
                j++;
                if (j > noOfMB) {
                    break;
                }
            }
            fis.close();
            byte[] mdbytes = md.digest();

            // convert the byte to hex format
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<FileDetail> createFileDetails(List<File> files) {
        int numberOfFiles = files.size();
        int i = 1;
        Object params[] = new Object[6];

        for (File f : files) {
            FileDetail e = new FileDetail(f.getAbsolutePath());
            long calculationTime = System.currentTimeMillis();
            final String calculateHash = calculateHash(f);
            e.setFileHash(calculateHash);
            e.setCalculationTime(System.currentTimeMillis() - calculationTime);
            filesDetail.add(e);
            params[0] = i;
            params[1] = numberOfFiles;
            params[2] = e.getCalculationTime();
            params[3] = e.getHumanFileSize();
            params[4] = e.getFileHash();
            params[5] = f.getAbsolutePath();

            // TODO add logging
            System.out.println(String.format(
                    "Create FileDetail in light mode, processed file number %1s out of %2s;time took %3s for size %4s; sha1 %5s; file %6s",
                    params));
            i++;
        }
        return filesDetail;
    }
}
