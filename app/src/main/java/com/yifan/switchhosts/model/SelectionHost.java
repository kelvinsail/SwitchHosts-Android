package com.yifan.switchhosts.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Hosts选择条目
 *
 * Created by yifan on 2016/11/25.
 */

public class SelectionHost implements Parcelable{

    /**
     * 域名地址
     */
    private String hostFrom;

    /**
     * 重新指向地址
     */
    private String hostTo;

    /**
     * 是否选中
     */
    private boolean isChecked;

    /**
     * 注释或说明，非有效hosts
     */
    private boolean isDescription;

    public SelectionHost() {
    }

    protected SelectionHost(Parcel in) {
        hostFrom = in.readString();
        hostTo = in.readString();
        isChecked = in.readByte() != 0;
        isDescription = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostFrom);
        dest.writeString(hostTo);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isDescription ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectionHost> CREATOR = new Creator<SelectionHost>() {
        @Override
        public SelectionHost createFromParcel(Parcel in) {
            return new SelectionHost(in);
        }

        @Override
        public SelectionHost[] newArray(int size) {
            return new SelectionHost[size];
        }
    };

    public String getHostFrom() {
        return hostFrom;
    }

    public void setHostFrom(String hostFrom) {
        this.hostFrom = hostFrom;
    }

    public String getHostTo() {
        return hostTo;
    }

    public void setHostTo(String hostTo) {
        this.hostTo = hostTo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDescription() {
        return isDescription;
    }

    public void setDescription(boolean description) {
        isDescription = description;
    }
}
