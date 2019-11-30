package com.example.kevinhan.forgetaboutit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * this class contains a circular linked
 * list of days
 */
public class Schedule implements Parcelable {

	//constant: time in advance to send notifications
	//(in minutes)
	public static int ADV_NOTIF = 15;

	//reference to the current day
	private Day currDay;

	/**
	 * Constructor for Schedule class
	 * @param  dayWeek day of the week to start from
	 * @return         newly created Schedule object
	 */
	public Schedule(DayWeek dayWeek) {
		Day first = null;
		for (DayWeek dw : DayWeek.values()) {
			if (currDay == null) {
				currDay = new Day(dw);
				first = currDay;
			}
			else {
				currDay.setNextDay(new Day(dw));
				currDay = currDay.nextDay();
			}
		}
		currDay.setNextDay(first);
		while (currDay.getDayWeek() != dayWeek) {
			currDay = currDay.nextDay();
		}
	}

	public Schedule(Parcel source){
		currDay = source.readParcelable(com.example.kevinhan.forgetaboutit.Day.class.getClassLoader());
	}

	/**
	 * adds event to specified day of the week
	 * @param dayWeek day of the week
	 * @param event   event to be added
	 */
	public void addEvent(DayWeek dayWeek, Event event) {
		Day day = currDay;
		while (day.getDayWeek() != dayWeek) {
			day = day.nextDay();
		}
		day.add(event);
	}

	/**
	 * get the current Day object
	 * @return current Day object
	 */
	public Day getDay() {
		return currDay;
	}

	/**
	 * go to the next day
	 */
	public void advanceDay() {
		currDay = currDay.nextDay();
	}

	/**
	 * clear all events in this Day object
	 */
	public void clear() {
		currDay.clear();
	}

	/**
	 * get a list of items that are missing
	 * @param  time  current time in 24 hour clock
	 * @param  items set of items already present
	 * @return       set of items that are missing
	 */
	public Set<Item> getMissingItems(int time, Set<Item> items) {
		Set<Item> result = new HashSet<Item>();
		for (Event event : currDay.getEvents()) {
			if (event.getTime() - time < ADV_NOTIF ||
				(time > 2400 - ADV_NOTIF &&
				event.getTime() + 2400 - time < ADV_NOTIF)) {
				for (Item missing : event.getMissingItems(items)) {
					result.add(missing);
				}
			}
		}
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(currDay, 0);
	}

	public static final Parcelable.Creator<Schedule>CREATOR
			= new Parcelable.Creator<Schedule>() {
		@Override
		public Schedule createFromParcel(Parcel source) {
			return new Schedule(source);
		}

		@Override
		public Schedule[] newArray(int size) {
			return new Schedule[size];
		}
	};
}