package ro.jmind.service;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class FileServiceImpl implements FileService {
    private List<File> files = new ArrayList<File>();
    private List<FileDetail> filesDetail = new ArrayList<FileDetail>();
    private List<DuplicateFileDetail> duplicateFilesList = new ArrayList<>();

    @Override
    public List<File> getFileList(String location) {
        File directory = new File(location);
        gatherFilesInList(files, directory);
        return files;
    }

    @Override
    public String calculateHash(File file) {
        String result = null;
        try {
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
            result = sb.toString();
        } catch (Exception e) {
            //TODO handle exception
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<FileDetail> gatherFilesDetail(List<File> files) {
        return createFileDetails(files);
    }

    @Override
    public List<FileDetail> gatherFilesDetailByFileName(List<String> files) {
        List<File> fileList = new ArrayList<>();
        for (String s : files) {
            fileList.add(new File(s));
        }

        return createFileDetails(fileList);
    }

    @Override
    public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail) {
        return calculateDuplicates(filesDetail, filesDetail);
    }

    @Override
    public List<FileDetail> markForDeletion(List<DuplicateFileDetail> duplicateFilesList) {
        List<FileDetail> result = new ArrayList<>();
        for (DuplicateFileDetail dfd : duplicateFilesList) {
            final Set<FileDetail> allDuplicatesOfTheFile = dfd.getDuplicates();
            int i = 0;
            for (FileDetail fd : allDuplicatesOfTheFile) {
                if (i == 0) {
                    fd.setReadyForDeletion(false);
                } else {
                    fd.setReadyForDeletion(true);
                }
                result.add(fd);
                i++;
            }
        }
        return result;
    }

    private void gatherFilesInList(List<File> fileList, File directory) {
        File[] listFiles = directory.listFiles();
        for (File f : listFiles) {
            if (f.isDirectory()) {
                gatherFilesInList(fileList, f);
            } else {
                fileList.add(f);
            }
        }
    }

    private List<FileDetail> createFileDetails(List<File> files) {
        int numberOfFiles = files.size();
        int i = 1;
        Object params[] = new Object[6];

        for (File f : files) {
            FileDetail e = new FileDetail(f);
            long calculationTime = System.currentTimeMillis();
            final String calculateHash = calculateHash(f);
            e.setFileHash(calculateHash);
            e.setCalculationTime(System.currentTimeMillis()-calculationTime);
            
            filesDetail.add(e);
            
            params[0] = i;
            params[1] = numberOfFiles;
            params[2] = e.getCalculationTime();
            params[3] = e.getHumanFileSize();
            params[4] = e.getFileHash();
            params[5] = f.getAbsolutePath();

            // TODO add logging
            System.out.println(String.format(
                    "Create FileDetail, file number %1s out of %2s;time took %3s for size %4s; sha1 %5s; file %6s",
                    params));
            i++;
        }
        return filesDetail;
    }

    private List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetailLeft,
            List<FileDetail> filesDetailRight) {

        String fileHashLeft, fileHashRight, leftPath, rightPath;
        for (FileDetail fdLeft : filesDetail) {
            fileHashLeft = fdLeft.getFileHash();
            // skip file if hash is missing
            if (fileHashLeft == null || fileHashLeft.length() < 1) {
                continue;
            }
            leftPath = fdLeft.getAbsoluteFile().getAbsolutePath();
            for (FileDetail fdRight : filesDetailRight) {
                if (fdLeft.equals(fdRight)) {
                    continue;
                }
                rightPath = fdRight.getAbsoluteFile().getAbsolutePath();
                fileHashRight = fdRight.getFileHash();
                if (fileHashRight.equals(fileHashLeft) && !leftPath.equals(rightPath)) {
                    boolean duplicateAdded = false;
                    for (DuplicateFileDetail d : duplicateFilesList) {
                        if (d.getHash().equals(fileHashRight)) {
                            d.addDuplicate(fdRight);
                            duplicateAdded = true;
                            break;
                        }
                    }
                    if (!duplicateAdded) {
                        DuplicateFileDetail duplicate = new DuplicateFileDetail();
                        duplicate.addDuplicate(fdLeft);
                        duplicate.addDuplicate(fdRight);
                        duplicateFilesList.add(duplicate);
                    }
                }
            }
        }
        return duplicateFilesList;
    }
}
