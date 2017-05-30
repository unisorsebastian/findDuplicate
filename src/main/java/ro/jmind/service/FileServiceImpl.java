package ro.jmind.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ro.jmind.model.DuplicateFileDetail;
import ro.jmind.model.FileDetail;

public class FileServiceImpl implements FileService {
    private List<File> files = new ArrayList<File>();
    private List<FileDetail> filesDetail = new ArrayList<FileDetail>();
    private List<DuplicateFileDetail> duplicateFilesList = new ArrayList<>();

    public List<FileDetail> gatherFilesDetail(List<File> files) {
        return gatherFilesDetail(files, false);
    }

    public List<FileDetail> gatherFilesDetailLight(List<File> files) {
        return gatherFilesDetail(files, true);
    }

    @Override
    public List<FileDetail> gatherFilesDetailByFileName(List<String> files) {
        List<File> fileList = new ArrayList<>();
        for (String s : files) {
            fileList.add(new File(s));
        }

        return gatherFilesDetail(fileList, false);
    }

    @Override
    public List<FileDetail> gatherFilesDetailLightByFileName(List<String> files) {
        List<File> fileList = new ArrayList<>();
        for (String s : files) {
            fileList.add(new File(s));
        }
        return gatherFilesDetail(fileList, true);
    }

    public List<File> getFileList(String location) {
        File directory = new File(location);
        gatherFilesInList(files, directory);
        return files;
    }

    @Override
    public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetail) {
        return calculateDuplicates(filesDetail, filesDetail);
    }

    @Override
    public List<DuplicateFileDetail> calculateDuplicates(List<FileDetail> filesDetailLeft,
            List<FileDetail> filesDetailRight) {
        String fileHashLeft, fileHashRight, leftPath, rightPath;
        for (FileDetail fdLeft : filesDetail) {
            fileHashLeft = fdLeft.getFileHash();
            //skip file if hash is missing
            if(fileHashLeft==null||fileHashLeft.length()<1){
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

    private List<FileDetail> gatherFilesDetail(List<File> files, boolean useLightData) {
        int numberOfFiles = files.size();
        int i = 1;
        Object params[] = new Object[6];

        for (File f : files) {
            FileDetail e;
            if (useLightData) {
                e = new FileDetail(f.getAbsolutePath());
            } else {
                e = new FileDetail(f);
            }
            filesDetail.add(e);
            params[0] = i;
            params[1] = numberOfFiles;
            params[2] = e.getCalculationTime();
            params[3] = e.getHumanFileSize();
            params[4] = e.getFileHash();
            params[5] = f.getAbsolutePath();

            //TODO add logging
            System.out.println(String.format(
                    "GatherFilesDetail processed file number %1s out of %2s;time took %3s for size %4s; sha1 %5s; file %6s",
                    params));
            i++;
        }
        return filesDetail;
    }
}
