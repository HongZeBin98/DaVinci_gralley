package com.example.davinci.bean;

public class FolderBean {

    //当前文件夹的路径
    private String dir;
    private String firstImgPath;
    private String name;
    private int count;

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf + 1);
    }

    public void setName() {
        this.name = "所有图片";
    }

    public String getDir() {
        return dir;
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
