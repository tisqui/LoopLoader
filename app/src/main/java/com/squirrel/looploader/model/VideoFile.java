package com.squirrel.looploader.model;

import java.io.File;

/**
 * Created by squirrel on 4/20/16.
 */
public class VideoFile {
    private String mFilePath;
    private File mFile;
    private String mFileName;

    public VideoFile(String filePath, File file) {
        mFilePath = filePath;
        mFile = file;
        String[] path = mFilePath.split("/");
        mFileName = path[path.length-1];
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    @Override
    public String toString() {
        return "VideoFile{" +
                "mFilePath='" + mFilePath + '\'' +
                ", mFile=" + mFile +
                ", mFileName='" + mFileName + '\'' +
                '}';
    }
}
