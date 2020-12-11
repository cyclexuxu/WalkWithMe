package neu.madcourse.walkwithme.Pedometer;

import java.util.Calendar;
import java.util.Date;


public class GetHistory {
    //final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    //to get past 6 days data in "yyyy-MM-dd" string

    public static Date getToday()
    {
        Date d=new Date();
        return new Date(d.getYear(),d.getMonth(),d.getDate());
    }

    public static Date add(Date day, int n)
    {
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(day);
        calendar.add(Calendar.DAY_OF_MONTH,n);
        return calendar.getTime();
    }

    public static String[] get7days()
    {
        Date today=getToday();

        String[] days=new String[7];
        for (int i=0;i<7;i++)
        {
            Date t=add(today,i-6);
            days[i]=t.getMonth()+1+"."+t.getDate();
        }
        return days;
    }
}
