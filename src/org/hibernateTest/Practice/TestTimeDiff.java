package org.hibernateTest.Practice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TestTimeDiff {

    public static void main(String[] args){

        System.out.println(new Timestamp(System.currentTimeMillis()));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Timestamp curr_Timestamp = new Timestamp(cal.getTimeInMillis());
        System.out.println(curr_Timestamp);

        System.out.println(new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        long diff= 5161000;
        System.out.println(diff);
        int sec = (int) diff / 1000;
        int hr = sec / 3600;
        int min = (sec % 3600) / 60;
        int sec1 = (sec % 3600) % 60;

        System.out.println("hr = " + hr + " min= " + min + " sec= " + sec1);
            System.out.println( hr + ":" + min + ":" + sec1);

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            Date d = simpleDateFormat.parse(str);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println("diff_time"+ df.format(new Date(sec * 1000L)) );


//            Optional list2 = session.createQuery("from frameworkBS where INVOK_ID = 'UNET' and BTCH_NM in ('seqOPASndRjctReport'," +
//                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
//                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
//                    "'seqOPAProvPRAFile','seqOPAGenDBITopsFeedback') order by CREAT_DTTM desc").list().stream().findFirst();
//            System.out.println("transaction_1 started");
//            demoTime(list2,tx, session);

    }
}
