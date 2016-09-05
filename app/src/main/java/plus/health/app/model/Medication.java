package plus.health.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saquib on 28-Jul-16.
 */


public class Medication implements Parcelable {

    private String problem;

    private int id;

    private String dateModified;

    private List<Doctor> doctorList;
    private List<Item> prescriptionList;
    private List<Item> reportList;

    private int status;

    public Medication() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public List<Item> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<Item> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    public List<Item> getReportList() {
        return reportList;
    }

    public void setReportList(List<Item> reportList) {
        this.reportList = reportList;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "problem='" + problem + '\'' +
                ", doctorList=" + doctorList +
                ", precriptionList=" + prescriptionList +
                ", reportList=" + reportList +
                '}';
    }
    protected Medication(Parcel in) {
        problem = in.readString();
        id = in.readInt();
        dateModified = in.readString();
        if (in.readByte() == 0x01) {
            doctorList = new ArrayList<Doctor>();
            in.readList(doctorList, Doctor.class.getClassLoader());
        } else {
            doctorList = null;
        }
        if (in.readByte() == 0x01) {
            prescriptionList = new ArrayList<Item>();
            in.readList(prescriptionList, Item.class.getClassLoader());
        } else {
            prescriptionList = null;
        }
        if (in.readByte() == 0x01) {
            reportList = new ArrayList<Item>();
            in.readList(reportList, Item.class.getClassLoader());
        } else {
            reportList = null;
        }
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest,int flags){
        dest.writeString(problem);
        dest.writeInt(id);
        dest.writeString(dateModified);
        if (doctorList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(doctorList);
        }
        if (prescriptionList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(prescriptionList);
        }
        if (reportList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reportList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Medication> CREATOR = new Parcelable.Creator<Medication>() {
        @Override
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };
}

