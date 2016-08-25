package com.xuxl.common.utils;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaTimeUitl {
	
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	
	public static int getDayOfYear() {
		return LocalDateTime.now().getDayOfYear();
	}
	
	public static int getDayOfYear(Date date) {
		return LocalDateTime.fromDateFields(date).getDayOfYear();
	}
	
	public static int getDayOfYear(Calendar calendar) {
		return LocalDateTime.fromCalendarFields(calendar).getDayOfYear();
	}
	
	public static int getDayOfMonth() {
		return LocalDateTime.now().getDayOfMonth();
	}
	
	public static int getDayOfMonth(Date date) {
		return LocalDateTime.fromDateFields(date).getDayOfMonth();
	}

	public static int getDayOfMonth(Calendar calendar) {
		return LocalDateTime.fromCalendarFields(calendar).getDayOfMonth();
	}
	
	public static int getDayOfWeek() {
		return LocalDateTime.now().getDayOfWeek();
	}
	
	public static int getDayOfWeek(Date date) {
		return LocalDateTime.fromDateFields(date).getDayOfWeek();
	}
	
	public static int getDayOfWeek(Calendar calendar) {
		return LocalDateTime.fromCalendarFields(calendar).getDayOfWeek();
	}
	
	public static boolean isBefore(Date start,Date end) {
		LocalDateTime startDay = LocalDateTime.fromDateFields(start);
		LocalDateTime endDay = LocalDateTime.fromDateFields(end);
		return startDay.isBefore(endDay);
	}
	
	public static boolean isAfter(Date start,Date end) {
		LocalDateTime startDay = LocalDateTime.fromDateFields(start);
		LocalDateTime endDay = LocalDateTime.fromDateFields(end);
		return startDay.isAfter(endDay);
	}
	
	public static int daysBetween(Date start,Date end) {
		LocalDateTime startDay = LocalDateTime.fromDateFields(start);
		LocalDateTime endDay = LocalDateTime.fromDateFields(end);
		return Days.daysBetween(startDay, endDay).getDays();
	}
	
	public static int hoursBetween(Date start,Date end) {
		LocalDateTime startDay = LocalDateTime.fromDateFields(start);
		LocalDateTime endDay = LocalDateTime.fromDateFields(end);
		return Hours.hoursBetween(startDay, endDay).getHours();
	}
	
	
	public static Date parse(String dateStr,String... patterns) {
		DateTimeFormatter format = null;
		if(patterns.length == 0) {
			format = DateTimeFormat.forPattern(DEFAULT_PATTERN);
		} else {
			String pattern = patterns[0];
			format = DateTimeFormat.forPattern(pattern);
		}
		LocalDateTime dateTime = LocalDateTime.parse(dateStr, format);
		return dateTime.toDate();
	}
	
	public static String format(Date date,String... patterns) {
		if(patterns.length == 0) {
			return LocalDateTime.fromDateFields(date).toString(DEFAULT_PATTERN);
		} else {
			String pattern = patterns[0];
			return LocalDateTime.fromDateFields(date).toString(pattern);
		}
	}
	
	public static Date plusDays(int days) {
		return plusDays(new Date(), days);
	}
	
	public static Date plusDays(Date date,int days) {
		LocalDateTime dateTime = LocalDateTime.fromDateFields(date);
		return dateTime.plusDays(days).toDate();
	}
	
	public static Date plusHours(int hours) {
		return plusHours(new Date(), hours);
	}
	
	public static Date plusHours(Date date,int hours) {
		LocalDateTime dateTime = LocalDateTime.fromDateFields(date);
		return dateTime.plusHours(hours).toDate();
	}
	
	
}
