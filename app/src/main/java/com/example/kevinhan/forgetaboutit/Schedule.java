package com.example.kevinhan.forgetaboutit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
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
	private Day[] week;
	private int index;

	private List<Event> events;
	private List<Item> items;

	/**
	 * Constructor for Schedule class
	 * @param  dayWeek day of the week to start from
	 * @return         newly created Schedule object
	 */
	public Schedule(int dayWeek) {
		Day first = null;
		events = new ArrayList<Event>();
		items = new ArrayList<Item>();
		week = new Day[7];
		index = 0;
/*
		for (DayWeek dw : DayWeek.values()) {
			if (currDay == null) {
				currDay = new Day(dw);
				first = currDay;
				week[index] = currDay;
				index++;
			}
			else {
				currDay.setNextDay(new Day(dw));
				week[index] = currDay.nextDay();
				index++;
				currDay = currDay.nextDay();
			}
		}

 */
		for(int dw = 0; dw < 7; dw++){
			currDay = new Day(dw);
			week[index] = currDay;
			index++;
			index = index % 7;
			System.out.println(dw.name() + index);
		}
//		currDay.setNextDay(first);
		while (currDay.getDayWeek() != dayWeek) {
			currDay = week[index];
			index++;
			index = index % 7;
		}
	}

	public Schedule(Parcel source){
		currDay = source.readParcelable(Day.class.getClassLoader());
		events = source.createTypedArrayList(Event.CREATOR);
		items = source.createTypedArrayList(Item.CREATOR);
		week = source.createTypedArray(Day.CREATOR);
		index = source.readInt();
	}

	/**
	 * adds event to specified day of the week
	 * @param dayWeek day of the week
	 * @param event   event to be added
	 */
	public void addEvent(int dayWeek, Event event) {
		events.add(event);
		Day day = currDay;
		while (day.getDayWeek() != dayWeek) {
			index++;
			index = index % 7;
			day = week[index];
		}
		day.add(event);
	}

	public void addItem(Event event, Item item) {
		items.add(item);
		for(Event e : events){
			if( e.equals(event) ){
				e.addItem(item);
			}
		}
	}

	public List<Event> getEvents(){
		return events;
	}

	public List<Item> getItems(){
		return items;
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
			if (this.getTimeDiff(event.getTime(),time) < ADV_NOTIF) {
				for (Item missing : event.getMissingItems(items)) {
					result.add(missing);
				}
			}
		}
		return result;
	}

	/**
	 * get the number of minutes between two times
	 * @param  time1 first time
	 * @param  time2 second time
	 * @return       number of minutes in between
	 */
	private static int getTimeDiff(int time1, int time2) {
		if (time1 < time2) {
			int temp = time1;
			time1 = time2;
			time2 = temp;
		}

		//time1 >= time2
		int hour1 = time1 / 100;
		int hour2 = time2 / 100;
		int min1 = time1 % 100;
		int min2 = time2 % 100;

		if (min1 < min2) {
			hour1--;
			min1 += 60;
		}
		if (hour1 < hour2) {
			hour1 += 24;
		}
		int hour3 = hour1 - hour2;
		int min3 = min1 - min2;
		if (min3 >= 60) {
			min3 -= 60;
			hour3++;
		}
		if (hour3 > 12) {
			hour3 = 24 - hour3;
			min3 = 60 - min3;
		}
		return hour3 * 60 + min3;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(currDay, 0);
		dest.writeTypedList(events);
		dest.writeTypedList(items);
		dest.writeTypedArray(week, 0);
		dest.writeInt(index);
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