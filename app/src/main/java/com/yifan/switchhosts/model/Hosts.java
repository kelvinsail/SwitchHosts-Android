package com.yifan.switchhosts.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Hosts文件数据类
 *
 * Created by yifan on 2016/10/31.
 */
public class Hosts implements Parcelable {

    /**
     * 文件名
     */
    String name;
    /**
     * 内容预览
     */
    String preview;
    /**
     * 内容
     */
    String content;
    /**
     * 使用时间，配合{@link #isUsing}使用，{@link #isUsing}为true时该值有效
     */
    long inUsedTime;
    /**
     * 修改时间
     */
    long modifiedTime;
    /**
     * 是否正在使用
     */
    boolean isUsing;

    /**
     * 文件路径
     */
    String path;

    /**
     * 标记是否为系统当前的Hosts文件
     */
    boolean isSystemHosts;

    public Hosts() {
    }

    protected Hosts(Parcel in) {
        name = in.readString();
        preview = in.readString();
        content = in.readString();
        inUsedTime = in.readLong();
        modifiedTime = in.readLong();
        isUsing = in.readByte() != 0;
        path = in.readString();
        isSystemHosts = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(preview);
        dest.writeString(content);
        dest.writeLong(inUsedTime);
        dest.writeLong(modifiedTime);
        dest.writeByte((byte) (isUsing ? 1 : 0));
        dest.writeString(path);
        dest.writeByte((byte) (isSystemHosts ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Hosts> CREATOR = new Creator<Hosts>() {
        @Override
        public Hosts createFromParcel(Parcel in) {
            return new Hosts(in);
        }

        @Override
        public Hosts[] newArray(int size) {
            return new Hosts[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getInUsedTime() {
        return inUsedTime;
    }

    public void setInUsedTime(long inUsedTime) {
        this.inUsedTime = inUsedTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSystemHosts() {
        return isSystemHosts;
    }

    public void setSystemHosts(boolean systemHosts) {
        isSystemHosts = systemHosts;
    }
}
