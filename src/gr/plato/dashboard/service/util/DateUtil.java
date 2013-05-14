package gr.plato.dashboard.service.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * Compares dates ignoring time
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int diff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (diff == 0) {
			diff = cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
		}
		return diff;
	}

}
