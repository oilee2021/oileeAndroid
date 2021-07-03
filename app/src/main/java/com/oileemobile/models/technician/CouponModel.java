package com.oileemobile.models.technician;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-12-14 00:44
 **/
public class CouponModel implements Parcelable {

    /**
     * id : 2
     * code : NOVEMBER PROMOTION
     * discount : $10
     */

    private int id;
    private String code;
    private String discount;

    private boolean isSelected;

    protected CouponModel(Parcel in) {
        id = in.readInt();
        code = in.readString();
        discount = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(discount);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CouponModel> CREATOR = new Creator<CouponModel>() {
        @Override
        public CouponModel createFromParcel(Parcel in) {
            return new CouponModel(in);
        }

        @Override
        public CouponModel[] newArray(int size) {
            return new CouponModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDiscount() {
        return discount;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
