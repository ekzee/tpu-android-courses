package ru.tpu.courses.lab4.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.courses.lab4.R;

public class NumberHolder extends RecyclerView.ViewHolder {

	public final TextView number;

	public NumberHolder(ViewGroup parent) {
		super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab4_item_number, parent, false));
		number = itemView.findViewById(R.id.number);
	}

	public void bind(int studentIndex) {
		number.setText((studentIndex + 1) + ")");
	}
}
