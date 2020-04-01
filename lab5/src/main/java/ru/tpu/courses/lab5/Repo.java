package ru.tpu.courses.lab5;

public class Repo {
	public String fullName;
	public String url;

	@Override
	public String toString() {
		return "Repo{" +
				"fullName='" + fullName + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
