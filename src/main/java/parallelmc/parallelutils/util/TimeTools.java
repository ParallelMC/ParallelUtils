package parallelmc.parallelutils.util;

public class TimeTools {

	public enum TimeUnit {
		TICKS(0, 1, "t"),
		SECONDS(1, 20, "s"),
		MINUTES(2, 20*60, "m"),
		HOURS(3, 20*60*60, "h"),
		DAYS(4, 20*60*60*24, "d");

		public int index;
		public int ticks;
		public String val;

		TimeUnit(int index, int ticks, String s) {
			this.index = index;
			this.ticks = ticks;
			val = s;
		}
	}

	/**
	 * Converts the given time from one unit to another
	 * @param time The time to convert
	 * @param original The unit of {@param time}
	 * @param end The unit to convert to
	 * @return The converted time
	 */
	public static double convertTime(double time, TimeUnit original, TimeUnit end) {
		return (time*(double)original.ticks)/(double)end.ticks;
	}

	/**
	 * Converts the given time in the given unit to a full form.
	 * Example: 64d 5h 2m 50s 10t
	 * @param time The starting time
	 * @param unit The unit the starting time is in
	 * @return The full time
	 */
	public static String fullTime(double time, TimeUnit unit) {
		String fulltime = "";

		// Convert to days
		double days = convertTime(time, unit, TimeUnit.DAYS);
		int daysInt = (int)days;
		fulltime += daysInt + TimeUnit.DAYS.val + " ";

		double hours = convertTime(days-daysInt, TimeUnit.DAYS, TimeUnit.HOURS);
		int hoursInt = (int)hours;
		fulltime += hoursInt + TimeUnit.HOURS.val + " ";

		double minutes = convertTime(hours-hoursInt, TimeUnit.HOURS, TimeUnit.MINUTES);
		int minutesInt = (int)minutes;
		fulltime += minutesInt + TimeUnit.MINUTES.val + " ";

		double seconds = convertTime(minutes-minutesInt, TimeUnit.MINUTES, TimeUnit.SECONDS);
		int secondsInt = (int) seconds;
		fulltime += secondsInt + TimeUnit.SECONDS.val + " ";

		double ticks = convertTime(seconds-secondsInt, TimeUnit.SECONDS, TimeUnit.TICKS);
		fulltime += (int)ticks + TimeUnit.TICKS.val;

		return fulltime;
	}
}
