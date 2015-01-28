package com.company;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hugo on 1/28/15.
 */
public class Utils {


    public static Calendar mdtmParaCalendar(String response){
        String calendarExtenso = response.substring(4,response.length());
        Calendar calendar = getCalendarFromFormat(calendarExtenso);
        return calendar;
    }


    //03/27/2014 12:16:56
    public static Calendar lastModifiedParaCalendar(long value){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = getCalendarFromFormat(sdf.format(value));
        return calendar;
    }

    //format "YYYYMMDDhhmmss" -- 20141113231002 -- 2014 11 13 23 10 02
    private static Calendar getCalendarFromFormat(String calendarExtenso) {
        Calendar calendar = Calendar.getInstance();
        Integer ano = Integer.valueOf(calendarExtenso.substring(0, 4));
        Integer mes = Integer.valueOf(calendarExtenso.substring(4, 6));
        Integer dia = Integer.valueOf(calendarExtenso.substring(6, 8));
        Integer hora = Integer.valueOf(calendarExtenso.substring(8, 10));
        Integer minuto = Integer.valueOf(calendarExtenso.substring(10, 12));
        Integer segundo = Integer.valueOf(calendarExtenso.substring(12, 14));
        calendar.set(ano,mes,dia,hora,minuto,segundo);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }
}
