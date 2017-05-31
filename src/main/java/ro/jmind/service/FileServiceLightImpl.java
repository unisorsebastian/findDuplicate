package ro.jmind.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.jmind.model.FileDetail;

public class FileServiceLightImpl extends FileServiceImpl {
    private List<FileDetail> filesDetail = new ArrayList<FileDetail>();

    @Override
    public List<FileDetail> gatherFilesDetail(List<File> files) {
        final List<FileDetail> gatherFilesDetail = createFileDetails(files);
        return gatherFilesDetail;
    }

    public void createTestFile() {
        try {
            final int fileSizeInBytes = 1024 * 1024 * 7;
            final int twoMB = 1024 * 1024 * 2;
            byte[] aBytes = new byte[fileSizeInBytes];
            for (int i = 0; i < fileSizeInBytes; i++) {
                if (i >= 0 && i < twoMB) {
                    aBytes[i] = 1;
                    continue;
                }
                if (i >= twoMB && i < fileSizeInBytes - twoMB) {
                    aBytes[i] = 2;
                    continue;
                }
                if (i >= fileSizeInBytes - twoMB) {
                    aBytes[i] = 3;
                    continue;
                }
            }

            final String fileLocation = "C:/Users/sunisor/remove/fixedFileSize.bin";
            Path path = Paths.get(fileLocation);
            Files.write(path, aBytes); // creates, overwrites

            parseFile(new File(fileLocation));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseFile(File file) throws IOException {
        final long startTime = System.currentTimeMillis(); 
        // buffer reader set at 4KB
        final int halfBufferSize = 2048;
        final int bufferSize = halfBufferSize * 2;
        final int bufferMultiplicator = 512;
        final byte[] buffer = new byte[bufferSize];
        // 4KB*512=2MB
        final int sampleDataSize = bufferSize * bufferMultiplicator;
        final byte[] sampleData = new byte[3 * sampleDataSize];

        final long fileSize = file.length();

        final long idxMiddle = fileSize / 2 - 1;
        final long idxMiddleStartRead = idxMiddle - sampleDataSize / 2 + 1;
        final long idxMiddleEndRead = idxMiddleStartRead + sampleDataSize - 1;

        final long skipBytesFromEndOfFirstSectionToMiddle = idxMiddleStartRead - sampleDataSize;

        final long idxLastStarRead = fileSize - sampleDataSize;
        final long skipBytesFromMiddleToEnd = idxLastStarRead - idxMiddleEndRead - 1;

        int copyIdx = 0;

        int readSize = 0;
        FileInputStream fis = new FileInputStream(file);

        for (int part = 0; part < 3; part++) {
            if (part == 1) {
                // skip to middle start point
                fis.skip(skipBytesFromEndOfFirstSectionToMiddle);
            }
            if (part == 2) {
                // skip to last part of the file
                fis.skip(skipBytesFromMiddleToEnd);
            }
            for (int i = 0; i < bufferMultiplicator; i++) {
                readSize = fis.read(buffer, 0, buffer.length);
                if(readSize!=buffer.length){
                    throw new RuntimeException("something is wrong, readSize!=1024");
                }
                System.arraycopy(buffer, 0, sampleData, copyIdx, buffer.length);
                copyIdx = copyIdx + buffer.length;
            }
        }
        fis.close();
        System.out.println("filesize:" + fileSize + " filename:" + file.getName()+" time took:"+(System.currentTimeMillis()-startTime));
    }

    @Override
    public String calculateHash(File file) {
        String result = null;

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
