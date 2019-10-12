package ru.tpu.courses.lab4;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

public class Student implements Parcelable {

    @NonNull
    public String firstName;
    @NonNull
    public String secondName;
    @NonNull
    public String lastName;
    @Nullable
    public String photoPath;

    public Student(
            @NonNull String firstName,
            @NonNull String secondName,
            @NonNull String lastName,
            @Nullable String photoPath
    ) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.photoPath = photoPath;
    }

    protected Student(Parcel in) {
        firstName = in.readString();
        secondName = in.readString();
        lastName = in.readString();
        photoPath = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(secondName);
        dest.writeString(lastName);
        dest.writeString(photoPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return firstName.equals(student.firstName) &&
                secondName.equals(student.secondName) &&
                lastName.equals(student.lastName) &&
                ObjectsCompat.equals(photoPath, student.photoPath);
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(firstName, secondName, lastName, photoPath);
    }
}
