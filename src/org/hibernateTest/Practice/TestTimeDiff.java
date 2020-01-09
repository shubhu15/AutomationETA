package org.hibernateTest.Practice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestTimeDiff {

    private static Logger logger = LogManager.getLogger(TestTimeDiff.class);
    public static void main(String[] args){

        logger.info("done logging");
String s = "bob wrote"+7+" java";
        System.out.println(s);
        System.out.println(new Timestamp(System.currentTimeMillis()));


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Timestamp tm = new Timestamp(cal.getTime().getTime());
        s = tm.toString().substring(0, 10);

        Timestamp curr_Timestamp = new Timestamp(cal.getTimeInMillis()+1800000);
        System.out.println(curr_Timestamp);
        System.out.println(new SimpleDateFormat("MM/dd/YY hh:mm a").format(curr_Timestamp));

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

        TestTimeDiff testTimeDiff = new TestTimeDiff();
        testTimeDiff.test();
        System.out.println("nowwwwwww");

//            Optional list2 = session.createQuery("from frameworkBS where INVOK_ID = 'UNET' and BTCH_NM in ('seqOPASndRjctReport'," +
//                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
//                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
//                    "'seqOPAProvPRAFile','seqOPAGenDBITopsFeedback') order by CREAT_DTTM desc").list().stream().findFirst();
//            System.out.println("transaction_1 started");
//            demoTime(list2,tx, session);

    }


    public void test(){
        System.out.println("hello");
        return;
    }
}
