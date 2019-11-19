package org.hibernateTest.Practice;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.Test.modelClass.frameworkBS;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class TestData {

 private static  SessionFactory sessionFactory = null;




    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public void shutdown() {
        getSessionFactory().close();
    }


    public static void main(String[] args) throws ParseException {

      TestData testData = new TestData();

      sessionFactory = testData.buildSessionFactory();
      testData.getQuery();

    }

        public void getQuery() throws ParseException {

        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List<frameworkBS> list_2 = session.createQuery("from frameworkBS where INVOK_ID = 'NONE' and BTCH_NM in ('seqLoad835DbPrePr','seqEmptyClmStg') and" +
                    " to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-13' order by CREAT_DTTM desc").list();

            startTimeDiff(list_2,session);

//
//            frameworkBS fb1 = list_2.get(list_2.size()-1);
//            Timestamp t_start_first = fb1.getSTRT_DTTM();
//
//            frameworkBS fb2 = list_2.get(0);
//            Timestamp t_end_first = fb2.getEND_DTTM();





            List list_1 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAGenDBITopsFeedback','seqMbrExtLoadHarvesting'," +
                    "'seqOPAUnetPreProc','seqCreateUCASDailyExt','seqMbrITKExtrc','seqMbrSchedule','seqMbrOTSUnlockUnused'," +
                    "'seqMbrOFSPmtData','seqBenHdrIDMbrLoadRespTbl','seqeHealthMbrCreateFeedBckFiles') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-13' order by CREAT_DTTM desc").list();
            System.out.println("transaction_1 started");
            printTimeDiff(list_1, session);


            tx.commit();
            }
        catch(HibernateException ex){
                if (tx != null) {
                    tx.rollback();
                }


                ex.printStackTrace(System.err);
            } finally{
                session.close();
            }



    }

    public void startTimeDiff(List<frameworkBS> ll, Session session){

//        List<frameworkBS> sub_list = new ArrayList<>();
//        sub_list.add(ll.get(ll.size()-1));
//        sub_list.add(ll.get(0));

//        List<Timestamp> list_timeSt = new ArrayList<Timestamp>();

//        Iterator itr = sub_list.iterator();
        frameworkBS fb1 = ll.get(ll.size()-1);
        frameworkBS fb2 = ll.get(0);

        if(fb1.getBTCH_STS_CD().equals("C") && !fb2.getBTCH_STS_CD().equals("C")) {
            List<frameworkBS> l1 = aWeekBackObject(fb1, session);
            List<frameworkBS> l2 = aWeekBackObject(fb2, session);

            long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());

            System.out.println("diff_time" + sf);
            Timestamp startTime_file = fb1.getSTRT_DTTM();
            long diff_new = timeDiffCalculate(new Timestamp(new Date().getTime()), startTime_file);
            System.out.println("the end time"+ (sf-diff_new) /* add to current time*/);

        }

        else if(fb2.getBTCH_STS_CD().equals("C")){
            System.out.println("completed.");
        }
//        while (itr.hasNext()){
//
//
//
//            System.out.print(fb.getINVOK_ID()+" "+ fb.getBTCH_NM()+" ");
//
//        if(fb.getBTCH_NM().equals("seqEmptyClmStg") && fb.getBTCH_STS_CD()) {
//        }
//            System.out.println(fb.getCREAT_DTTM() + "  ");
//
//        }
    }
    /*public void demoTime(Optional o, Transaction tx, Session session) throws ParseException {

        if (o.isPresent()){
            frameworkBS fb = (frameworkBS)o.get();
            System.out.println(fb.getINVOK_ID()+" "+ fb.getBTCH_NM()+"  "+ fb.getSTRT_DTTM());

            System.out.print(fb.getCREAT_DTTM() + "  ");

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fb.getCREAT_DTTM().getTime());
            cal.add(Calendar.DAY_OF_MONTH, -7);

            Timestamp tm = new Timestamp(cal.getTime().getTime());
            System.out.println(tm);
            String s = tm.toString().substring(0, 10);
//            System.out.println(s);
            Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ fb.getINVOK_ID()+"' and BTCH_NM = '"+ fb.getBTCH_NM()+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + s + "'");
            List<frameworkBS> l2 = (List<frameworkBS>) q2.list();

//               System.out.println(l2.get(0).getEND_DTTM());
//            System.out.println(l2.get(0).getSTRT_DTTM());
            Timestamp t_end = l2.get(0).getEND_DTTM();
            Timestamp t_start = l2.get(0).getSTRT_DTTM();

            long diff = t_end.getTime() - t_start.getTime();


            System.out.println(diff);
            int sec = (int) diff / 1000;
            int hr = sec / 3600;
            int min = (sec % 3600) / 60;
            int sec1 = (sec % 3600) % 60;

            System.out.println("hr = " + hr + " min= " + min + " sec= " + sec1);
//            String str = hr + ":" + min + ":" + sec;

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            Date d = simpleDateFormat.parse(str);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            System.out.println("diff_time"+ df.format(new Date(sec * 1000L)) );


        }
    } */

    public void printTimeDiff(List list_1, Session session) throws ParseException {


        Iterator itr_1 = list_1.iterator();
        while (itr_1.hasNext()) {

            frameworkBS fb = (frameworkBS) itr_1.next();
            System.out.print(fb.getINVOK_ID()+" "+ fb.getBTCH_NM()+" ");

            System.out.println(fb.getCREAT_DTTM() + "  ");
            List<frameworkBS> l2 = aWeekBackObject(fb,session);
//            if(fb.getBTCH_STS_CD().equals("C") && fb.getBTCH_NM().equals("seqEmptyClmStg") ){
//
//
//            }


//               System.out.println(l2.get(0).getEND_DTTM());
//            System.out.println(l2.get(0).getSTRT_DTTM());
            Timestamp t_end = l2.get(0).getEND_DTTM();
            Timestamp t_start = l2.get(0).getSTRT_DTTM();

            long sf = timeDiffCalculate(t_end,t_start);

            System.out.println("diff_time"+ timeInString(sf));



        }

    }

    public List<frameworkBS> aWeekBackObject(frameworkBS fb,Session session){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fb.getCREAT_DTTM().getTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        Timestamp tm = new Timestamp(cal.getTime().getTime());
//            System.out.println(tm);
        String s = tm.toString().substring(0, 10);
//            System.out.println(s);
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ fb.getINVOK_ID()+"' and BTCH_NM = '"+ fb.getBTCH_NM()+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + s + "'");

        return q2.list();

    }

    public long timeDiffCalculate(Timestamp t_end, Timestamp t_start){
        long diff = t_end.getTime() - t_start.getTime();


//            System.out.println(diff);

        return diff;

    }

    public String timeInString(long diff){
        int sec = (int) diff / 1000;
        int hr = sec / 3600;
        int min = (sec % 3600) / 60;
        int sec1 = (sec % 3600) % 60;

//            System.out.println("hr = " + hr + " min= " + min + " sec= " + sec1);
//            String str = hr + ":" + min + ":" + sec;

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            Date d = simpleDateFormat.parse(str);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date(sec * 1000L));
    }
}
