package com.example.kevinhan.forgetaboutit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a day. It contains events
 * present that day
 *
 * This class also has a reference to another Day
 * object to allow making linked-lists
 */
public class Day implements Parcelable {

	//list of events
	private List<Event> events;

	//day and date of the week
	private int dayWeek;

	//reference to the next day
	private Day nextDay;

	/**
	 * Constructor for Day class
	 * @param  dayWeek day of the week this object represents
	 * @return         newly created day object
	 */
	public Day(int dayWeek) {
		events = new ArrayList<Event>();
		nextDay = null;
		this.dayWeek = dayWeek;
	}

	public Day(Parcel source){
		events = source.createTypedArrayList(Event.CREATOR);
		dayWeek = source.readInt();
		//nextDay = source.readParcelable(com.example.kevinhan.forgetaboutit.Day.class.getClassLoader());
		nextDay = null;
	}

	/**
	 * add event to this day's list
	 * @param ev event to add
	 */
	public void add(Event ev) {
		events.add(ev);
	}

	/**
	 * remove event from this day's list
	 * @param ev event to remove
	 */
	public void remove(Event ev) {
		events.remove(ev);
	}

	/**
	 * get the day of the week of this object
	 * @return the day of the week
	 */
	public int getDayWeek() {
		return dayWeek;
	}

	/**
	 * change the day of the week this object represents
	 * @param dayWeek new day of the week
	 */
	public void setDayWeek(int dayWeek) {
		this.dayWeek = dayWeek;
	}

	/**
	 * change the next day this object points to
	 * @param next Day object to point to
	 */
	public void setNextDay(Day next) {
		nextDay = next;
	}

	/**
	 * get the next Day being pointed to by this object
	 * @return the next day that is being pointed to
	 */
	public Day nextDay() {
		return nextDay;
	}

	/**
	 * remove all events from the day
	 */
	public void clear() {
		events.clear();
	}

	/**
	 * get a list of events for the day
	 * @return the list of events
	 */
	public List<Event> getEvents() {
		return events;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(events);
		dest.writeInt(dayWeek);
	}

	public static final Parcelable.Creator<Day>CREATOR
			= new Parcelable.Creator<Day>() {
		@Override
		public Day createFromParcel(Parcel source) {
			return new Day(source);
		}

		@Override
		public Day[] newArray(int size) {
			return new Day[size];
		}
	};
}