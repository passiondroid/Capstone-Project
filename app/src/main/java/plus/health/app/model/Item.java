package plus.health.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Saquib on 21-Jul-16.
 */

public class Item implements Parcelable {

    public static final int TYPE_DOC = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int STATUS_UPLOADED = 1;
    public static final int STATUS_LOCAL = 2;

    public Item() {
    }

    private String data;

    private int type;

    private String name;

    private int status;

    private int medicationId;

    private int id;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Item(Parcel in) {
        data = in.readString();
        type = in.readInt();
        name = in.readString();
        status = in.readInt();
        medicationId = in.readInt();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeInt(type);
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeInt(medicationId);
        dest.writeInt(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public String toString() {
        return "Item{" +
                "data='" + data + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", medicationId=" + medicationId +
                ", id=" + id +
                '}';
    }
}