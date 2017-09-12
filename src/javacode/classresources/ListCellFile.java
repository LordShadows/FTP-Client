package javacode.classresources;

import org.apache.commons.net.ftp.FTPFile;

public class ListCellFile {
    private String path;
    private String name;
    private long size;
    private String date;
    private boolean isFolder;

    public ListCellFile(String path, String name, long size, String date, boolean isFolder) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.date = date;
        this.isFolder = isFolder;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
