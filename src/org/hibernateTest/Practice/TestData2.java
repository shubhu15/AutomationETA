package org.hibernateTest.Practice;

import org.Test.modelClass.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestData2 {

    private static SessionFactory sessionFactory = null;




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

        TestData2 testData = new TestData2();

        sessionFactory = testData.buildSessionFactory();
        testData.getQuery();

    }

    public void getQuery() throws ParseException {

        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List list_1 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-13' order by CREAT_DTTM desc").list();
            System.out.println("transaction_1 for UNET-PROVIDER started");
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

    public void printTimeDiff(List list_1, Session session) throws ParseException {


        Iterator itr_1 = list_1.iterator();
        Timestamp start_PrePreProcessor = null;
        Timestamp start_PrePreProcessor_aWeekAgo = null;
        Timestamp end_PrePreProcessor_aWeekAgo= null;

        Timestamp start_PreProcessor  = null;
        Timestamp end_PreProcessor = null;

        Timestamp start_Intake =null;
        Timestamp end_Intake = null;

        Timestamp start_Scheduling = null;
        Timestamp end_Scheduling = null;

        Timestamp start_ReleaseNConsolidation = null;
        Timestamp end_ReleaseNConsolidation = null;

        Timestamp start_PaymentProcessing = null;
        Timestamp end_PaymentProcessing = null;

        Timestamp start_PostPaymentExtract = null;
        Timestamp end_PostPaymentExtract = null;

        Timestamp start_835EPS_B2B = null;
        Timestamp end_835EPS_B2B = null;

        Timestamp start_EPSFundingFile = null;
        Timestamp end_EPSFundingFile = null;

        Timestamp start_FundingReport = null;
        Timestamp end_FundingReport = null;

        Timestamp start_ProviderPRA = null;
        Timestamp end_ProviderPRA = null;


        List<frameworkBS> l1;


        if(itr_1.hasNext()){
            frameworkBS fb = (frameworkBS) itr_1.next();
            if(fb.getBTCH_NM().equals("seqEmptyClmStg") && fb.getBTCH_STS_CD().equals("C")){
                if(list_1.size()<4){ System.out.println("in progress");}
                l1 = aWeekBackObject(fb, session);
                start_PrePreProcessor = fb.getSTRT_DTTM();
                start_PrePreProcessor_aWeekAgo = l1.get(0).getSTRT_DTTM();
            }
        }

        if (list_1.size()==4){
            frameworkBS fb = (frameworkBS) list_1.get(3);
            if(fb.getBTCH_NM().equals("seqLoad835DbPrePr ") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                end_PrePreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PrePreProcessor_aWeekAgo, start_PrePreProcessor_aWeekAgo);
                long diff_new = timeDiffCalculate(new Timestamp(new Date().getTime()), start_PrePreProcessor);
                System.out.println("the end time"+ (sf-diff_new) /* add to current time*/);

            }

        }

//        itr_1.next();
//        itr_1.next();
//        itr_1.next();

        for (int i = 4; i<list_1.size();i++){
            frameworkBS fb = (frameworkBS) list_1.get(i);
            if(fb.getBTCH_NM().equals("seqOPAUnetPreProc") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_PreProcessor = l1.get(0).getSTRT_DTTM();
                end_PreProcessor = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PreProcessor, start_PreProcessor);
                System.out.println("the end time"+ sf /* add to end time of prepreprocessor*/);
            }

            else if(fb.getBTCH_NM().equals("seqOPAITKLdStg") && fb.getBTCH_STS_CD().equals("C")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPASndRjctReport") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_Intake = l1.get(0).getSTRT_DTTM();
                    end_Intake = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Intake, start_Intake);
                    System.out.println("the end time"+ sf /* add to end time of preprocessor*/);

                }
            }

            else if(fb.getBTCH_NM().equals("seqOPATruncateRlseTables") && fb.getBTCH_STS_CD().equals("C")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_Scheduling = l1.get(0).getSTRT_DTTM();
                    end_Scheduling = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Scheduling, start_Scheduling);
                    System.out.println("the end time"+ sf /* add to end time of intake*/);

                }

            }

            else if(fb.getBTCH_NM().equals("seqOPALoadReleaseProcessing")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAFSPrvConsldtData") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_ReleaseNConsolidation = l1.get(0).getSTRT_DTTM();
                    end_ReleaseNConsolidation = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_ReleaseNConsolidation, start_ReleaseNConsolidation);
                    System.out.println("the end time"+ sf /* add to end time of Scheduling*/);

                }
            }

            else if(fb.getBTCH_NM().equals("seqOPAPaymentProcessing")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_PaymentProcessing = l1.get(0).getSTRT_DTTM();
                    end_PaymentProcessing = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PaymentProcessing, start_PaymentProcessing);
                    System.out.println("the end time"+ sf /* add to end time of Release n consolidation*/);
                }
            }

            else if(fb.getBTCH_NM().equals("seqCreateUCASDailyExt") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_PostPaymentExtract = l1.get(0).getSTRT_DTTM();
                end_PostPaymentExtract = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PostPaymentExtract, start_PostPaymentExtract);
                System.out.println("the end time"+ sf /* add to end time of Payment Processing*/);
            }

            else if(fb.getBTCH_NM().equals("seqOPA835PostpaymentLoad")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_835EPS_B2B = l1.get(0).getSTRT_DTTM();
                    end_835EPS_B2B = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_835EPS_B2B, start_835EPS_B2B);
                    System.out.println("the end time"+ sf /* add to end time of Payment Processing*/);
                }
            }

            else if(fb.getBTCH_NM().equals("seqOPACreateEPSFile") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_EPSFundingFile = l1.get(0).getSTRT_DTTM();
                end_EPSFundingFile = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_EPSFundingFile, start_EPSFundingFile);
                System.out.println("the end time"+ sf /* add to end time of 835EPS_B2B*/);
            }

            else if(fb.getBTCH_NM().equals("seqOPAEPSReport_FS") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_FundingReport = l1.get(0).getSTRT_DTTM();
                end_FundingReport = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_FundingReport, start_FundingReport);
                System.out.println("the end time"+ sf /* add to end time of EPS Funding file*/);
            }

            else if(fb.getBTCH_NM().equals("seqOPAProvPRAFile") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_ProviderPRA = l1.get(0).getSTRT_DTTM();
                end_ProviderPRA = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_ProviderPRA, start_ProviderPRA);
                System.out.println("the end time"+ sf /* add to end time of EPS Funding file*/);
            }


            else {
                System.out.println("all jobs done");
            }

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
