package ru.tpu.courses.lab4.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.courses.lab4.R;

public class StudentHolder extends RecyclerView.ViewHolder {

    public final TextView student;

    public StudentHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab4_item_student, parent, false));
        student = itemView.findViewById(R.id.student);
    }
}
