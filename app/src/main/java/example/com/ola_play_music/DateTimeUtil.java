package example.com.ola_play_music;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sandipanmitra on 4/6/17.
 */

public class DateTimeUtil {
    public static String getTimeStringFor(long attendanceTime) {
        String DATE_FORMAT = "HH:mm, dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date(attendanceTime*1000l));
    }
    public static String getTimeStringFromMillies(long attendanceTime) {
        String DATE_FORMAT = "HH:mm, dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date(attendanceTime));
    }

    public static String getTimeLeft(long lastSeenTime){
        lastSeenTime *=1000;

        long currentTime = Calendar.getInstance().getTimeInMillis();

        long diff = currentTime-lastSeenTime;

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);

        if(diffHours>0){
            return String.format("Last seen %d hours before", diffHours);
        }else if(diffMinutes>0){
            return String.format("Last seen %d minutes before", diffMinutes);
        }else{
            return String.format("Last seen %d seconds before", diffSeconds);
        }
    }
}
