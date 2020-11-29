package neu.madcourse.walkwithme.Test;

import java.util.Calendar;
import java.util.Date;


public class DateTimeHelper {
    //final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

    public static Date getToday()
    {
        Date d=new Date();
        return new Date(d.getYear(),d.getMonth(),d.getDate());
    }

    public static Date add(Date d, int n)
    {
        Calendar c= Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH,n);
        return c.getTime();

    }

    public static Date[] get6days()
    {
        Date d=getToday();
        Date[] days=new Date[6];
        for (int i=0;i<6;i++)
        {
            days[i]=add(d,i-5);
        }
        return days;
    }

    public static String[] get6days(boolean returnString)
    {
        Date d=getToday();


        String[] days=new String[6];
        for (int i=0;i<6;i++)
        {
            Date t=add(d,i-5);
            days[i]=t.getMonth()+1+"."+t.getDate();
        }
        return days;
    }
}
