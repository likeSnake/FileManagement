package net.management.utility.file.manager.bean;

public class ImageBean  {
    private boolean isSelect = false;
    private String path;

    public ImageBean(String path) {
        this.path = path;
    }


    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
