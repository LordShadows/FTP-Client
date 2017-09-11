package javacode.classresources;

import java.io.File;

public class ListCellFile {
    private File file;
    private String name;
    private long size;
    private String date;
    private boolean isFolder;

    public ListCellFile(File file, String name, long size, String date, boolean isFolder) {
        this.file = file;
        this.name = name;
        this.size = size;
        this.date = date;
        this.isFolder = isFolder;
    }

    public File getFile() {
        return file;
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
