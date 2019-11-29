package com.example.kevinhan.forgetaboutit;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a day. It contains events
 * present that day
 *
 * This class also has a reference to another Day
 * object to allow making linked-lists
 */
public class Day {

	//list of events
	private List<Event> events;

	//day and date of the week
	private DayWeek dayWeek;

	//reference to the next day
	private Day nextDay;

	/**
	 * Constructor for Day class
	 * @param  dayWeek day of the week this object represents
	 * @return         newly created day object
	 */
	public Day(DayWeek dayWeek) {
		events = new ArrayList<Event>();
		nextDay = null;
		this.dayWeek = dayWeek;
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
	public DayWeek getDayWeek() {
		return dayWeek;
	}

	/**
	 * change the day of the week this object represents
	 * @param dayWeek new day of the week
	 */
	public void setDayWeek(DayWeek dayWeek) {
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
}