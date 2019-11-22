package org.hibernateTest.Practice;

import org.Test.modelClass.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestData3 {

    private static SessionFactory sessionFactory = null;
    private static List<frameworkBS> list_1;
    private static int i;




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


    public static void main(String[] args)  {

        TestData3 testData = new TestData3();

        String current_date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        sessionFactory = testData.buildSessionFactory();
        testData.getQuery(current_date);

    }

    public void getQuery(String current_date){

        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for UNET-PROVIDER
            List list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '"+ current_date+
                    "' order by CREAT_DTTM").list();

            //Sample list to compare the objects not present in the current list
            list_1 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-18' order by CREAT_DTTM").list();
            System.out.println("transaction_1 for UNET-PROVIDER started");
            printTimeDiff(list_main, session);

            //Query for UNET-MEMBER
            List list_2 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqMbrEmptyStgBkt'," +
                    "'seqMbrITKExtrc','seqProcessTopsDBI','seqOPAGenDBITopsFeedback','seqMbrSchedule','seqMbrEmptyRlseBkt'," +
                    "'seqMbrExtLoadHarvesting','seqMbrPayment1','seqMbrOTSUnlockUnused','seqMbrOFSPmtData','seqBenHdrIDMbrGenReqFile'," +
                    "'seqBenHdrIDMbrLoadRespTbl','seqMbrEOBFile','seqeHealthMbrCreateFeedBckFiles','seqMbr03CreateFICSFile') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-18' order by CREAT_DTTM").list();
            System.out.println("transaction_2 for UNET-MEMBER started");
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

    public void printTimeDiff(List list_main ,Session session) {
        Iterator itr_1 = list_main.iterator();
        System.out.println(list_main.size());

        Timestamp start_PrePreProcessor = null;

        Timestamp end_PrePreProcessor = null;
        Timestamp start_PrePreProcessor_aWeekAgo = null;
        Timestamp end_PrePreProcessor_aWeekAgo= null;

        Timestamp end_PreProcessor = null;
        Timestamp start_PreProcessor_aWeekAgo = null;
        Timestamp end_PreProcessor_aWeekAgo = null;

        Timestamp end_Intake = null;
        Timestamp start_Intake_aWeekAgo=null;
        Timestamp end_Intake_aWeekAgo = null;

        Timestamp end_Scheduling = null;
        Timestamp start_Scheduling_aWeekAgo = null;
        Timestamp end_Scheduling_aWeekAgo = null;

        Timestamp end_ReleaseNConsolidation = null;
        Timestamp start_ReleaseNConsolidation_aWeekAgo= null;
        Timestamp end_ReleaseNConsolidation_aWeekAgo = null;

        Timestamp end_PaymentProcessing = null;
        Timestamp start_PaymentProcessing_aWeekAgo = null;
        Timestamp end_PaymentProcessing_aWeekAgo = null;

        Timestamp end_PostPaymentExtract = null;
        Timestamp start_PostPaymentExtract_aWeekAgo = null;
        Timestamp end_PostPaymentExtract_aWeekAgo = null;

        Timestamp end_835EPS_B2B = null;
        Timestamp start_835EPS_B2B_aWeekAgo = null;
        Timestamp end_835EPS_B2B_aWeekAgo = null;

        Timestamp end_EPSFundingFile = null;
        Timestamp start_EPSFundingFile_aWeekAgo = null;
        Timestamp end_EPSFundingFile_aWeekAgo = null;

        Timestamp end_FundingReport = null;
        Timestamp start_FundingReport_aWeekAgo = null;
        Timestamp end_FundingReport_aWeekAgo = null;

        Timestamp end_ProviderPRA = null;
        Timestamp start_ProviderPRA_aWeekAgo = null;
        Timestamp end_ProviderPRA_aWeekAgo = null;

        List<frameworkBS> l1;
        List<frameworkBS> l2;



        //Pre-Pre Processor (file loading) ---to get the start time of the UNET-Provider
        if(itr_1.hasNext()){
            frameworkBS fb = (frameworkBS) itr_1.next();
            System.out.println(fb.getBTCH_NM() +" " + fb.getBTCH_STS_CD() );
            if(fb.getBTCH_NM().equals("seqEmptyClmStg") && fb.getBTCH_STS_CD().equals("C")){
                start_PrePreProcessor = fb.getSTRT_DTTM();
                if(list_main.size()<4){ System.out.println("in progress");}
                l1 = aWeekBackObject(fb, session);
                start_PrePreProcessor_aWeekAgo = l1.get(0).getSTRT_DTTM();
            }
        }

        //Pre-Pre Processor (completion) ---to get ETA of Pre-Pre Processor
        //if the process is not yet completed.
        if (list_main.size()==4){
            i =3;
            frameworkBS fb = (frameworkBS) list_main.get(i);
            if(fb.getBTCH_NM().equals("seqLoad835DbPrePr ") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                end_PrePreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PrePreProcessor_aWeekAgo, start_PrePreProcessor_aWeekAgo);
//               long diff_new = timeDiffCalculate(current_time, start_PrePreProcessor);
                end_PrePreProcessor = new Timestamp(sf+start_PrePreProcessor.getTime());
                System.out.println("the end time of Pre-Pre Processor"+ end_PrePreProcessor /* added to current start time of file loaded*/);

                onPlanETA(i+1);
            }

        }


        //if Pre-Pre Processor is already completed
        else if(list_main.size()==5){
            i= 4;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_PrePreProcessor = fb.getEND_DTTM();
            frameworkBS fb2 = (frameworkBS) list_main.get(i);

            //Pre-Processor started but not completed
            if(fb2.getBTCH_NM().equals("seqOPAUnetPreProc") && !fb2.getBTCH_STS_CD().equals("C")){

                end_PreProcessor = calculateETAwithStart(fb2, end_PrePreProcessor,session);
                System.out.println("the end time of Pre-Process"+ end_PreProcessor/* added to end time of prepreprocessor*/);
                onPlanETA(i+1);

            }
        }


        //Intake started but not completed
        else if(list_main.size()==7){
            i= 5;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_PreProcessor = fb.getEND_DTTM();
            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(++i);
            if(fb2.getBTCH_NM().equals("seqOPASndRjctReport") && !fb2.getBTCH_STS_CD().equals("C")){

                end_Intake =  calculateETAwithStartEnd(fb1,fb2,end_PreProcessor,session);

                 /*   //debugging to check the time difference of current day
                    long df =timeDiffCalculate(fb2.getEND_DTTM(), fb.getSTRT_DTTM());
                    System.out.println(sf +"  " + df);
                    System.out.println("New end time"+ new Timestamp(end_PreProcessor.getTime()+df));  this shows correct results  */

                System.out.println("the end time of Intake"+ end_Intake /* added to end time of preprocessor*/);
                onPlanETA(i+1);

            }
        }

        //Scheduling started but not completed
        else if(list_main.size()==9){
            i= 7;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_Intake = fb.getEND_DTTM();
            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(++i);
            if(fb2.getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") && !fb2.getBTCH_STS_CD().equals("C")){
                end_Scheduling =  calculateETAwithStartEnd(fb1,fb2,end_Intake,session);
                System.out.println("the end time of Scheduling"+ end_Scheduling /* added to end time of intake*/);
                onPlanETA(i+1);

            }
        }

        //Release & Consolidation started but not yet completed
        else if(list_main.size()==11){
            i= 9;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_Scheduling = fb.getEND_DTTM();
            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(++i);
            if(fb2.getBTCH_NM().equals("seqOPAFSPrvConsldtData") && !fb2.getBTCH_STS_CD().equals("C")){
                end_ReleaseNConsolidation =  calculateETAwithStartEnd(fb1,fb2,end_Scheduling,session);
                System.out.println("the end time of Release & Consolidation"+ end_ReleaseNConsolidation /* added to end time of Scheduling*/);
                onPlanETA(i+1);

            }
        }

        //Payment Processing started but not yet completed
        else if(list_main.size()==13){
            i= 11;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_ReleaseNConsolidation = fb.getEND_DTTM();
            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(++i);
            if(fb2.getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn") && !fb2.getBTCH_STS_CD().equals("C")){
                end_PaymentProcessing =  calculateETAwithStartEnd(fb1,fb2,end_ReleaseNConsolidation,session);
                System.out.println("the end time of Payment Processing"+ end_PaymentProcessing /* added to end time of Release n consolidation*/);
                onPlanETA(i+1);

            }
        }

        else if(list_main.size()==14){
            i= 13;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_PaymentProcessing = fb.getEND_DTTM();
//            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(i);
            if(fb2.getBTCH_NM().equals("seqCreateUCASDailyExt") && !fb2.getBTCH_STS_CD().equals("C")){
                end_PostPaymentExtract =  calculateETAwithStart(fb2,end_PaymentProcessing,session);
                System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ end_PostPaymentExtract /* added to end time of Payment Processing*/);
                onPlanETA(i+1);

            }
        }

        //835 EPS/B2B started but not yet completed
        else if(list_main.size()==16){
            i= 14;
//            System.out.println("job completed");
//            frameworkBS fb = (frameworkBS) list_main.get(i-1);
//            end_ReleaseNConsolidation = fb.getEND_DTTM();
            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(++i);
            if(fb2.getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") && !fb2.getBTCH_STS_CD().equals("C")){
                end_835EPS_B2B =  calculateETAwithStartEnd(fb1,fb2,end_PaymentProcessing,session);
                System.out.println("the end time of 835 EPS/B2B"+ end_835EPS_B2B /* added to end time of Payment Processing*/);
                onPlanETA(i+1);

            }
        }

        else if(list_main.size()==17){
            i= 16;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_835EPS_B2B = fb.getEND_DTTM();
//            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(i);
            if(fb2.getBTCH_NM().equals("seqOPACreateEPSFile") && !fb2.getBTCH_STS_CD().equals("C")){
                end_EPSFundingFile =  calculateETAwithStart(fb2,end_835EPS_B2B,session);
                System.out.println("the end time of EPS Funding File"+ end_EPSFundingFile /* added to end time of 835EPS_B2B*/);
                onPlanETA(i+1);

            }
        }

        else if(list_main.size()==18){
            i= 17;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_EPSFundingFile = fb.getEND_DTTM();
//            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(i);
            if(fb2.getBTCH_NM().equals("seqOPAEPSReport_FS") && !fb2.getBTCH_STS_CD().equals("C")){
                end_FundingReport =  calculateETAwithStart(fb2,end_EPSFundingFile,session);
                System.out.println("the end time of Funding Report "+ end_FundingReport /* added to end time of EPS Funding file*/);
                onPlanETA(i+1);

            }
        }

        else if(list_main.size()==19){
            i= 18;
//            System.out.println("job completed");
            frameworkBS fb = (frameworkBS) list_main.get(i-1);
            end_EPSFundingFile = fb.getEND_DTTM();
//            frameworkBS fb1 = (frameworkBS) list_main.get(i);
            frameworkBS fb2 = (frameworkBS) list_main.get(i);
            if(fb2.getBTCH_NM().equals("seqOPAProvPRAFile") && !fb2.getBTCH_STS_CD().equals("C")){
                end_ProviderPRA =  calculateETAwithStart(fb2,end_EPSFundingFile,session);
                System.out.println("the end time of Provider PRA "+ end_ProviderPRA /* added to end time of EPS Funding file*/);
                onPlanETA(i+1);

            }
        }


        else {
            System.out.println("all jobs done");
        }




    }

    public Timestamp calculateETAwithStartEnd(frameworkBS fb1, frameworkBS fb2, Timestamp endTime, Session session){
        System.out.println("On Plan");
        List<frameworkBS> l1 = aWeekBackObject(fb1, session);
        List<frameworkBS> l2 = aWeekBackObject(fb2, session);
        long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
        return new Timestamp(endTime.getTime()+sf);
    }

    public Timestamp calculateETAwithStart(frameworkBS fb, Timestamp endTime,Session session){
        System.out.println("On Plan");
        List<frameworkBS> l1 = aWeekBackObject(fb, session);
//        start_ProviderPRA_aWeekAgo = l1.get(0).getSTRT_DTTM();
//        end_ProviderPRA_aWeekAgo = l1.get(0).getEND_DTTM();
        long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
//        end_ProviderPRA = new Timestamp(end_EPSFundingFile.getTime()+sf);
        return new Timestamp(endTime.getTime()+sf);
    }

    public void onPlanETA(int i){

        for(int j = i; j<list_1.size();j++){

        }


    }


    public List<frameworkBS> aWeekBackObject(frameworkBS fb,Session session){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fb.getCREAT_DTTM().getTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        Timestamp tm = new Timestamp(cal.getTime().getTime());
        String s = tm.toString().substring(0, 10);
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ fb.getINVOK_ID()+"' and BTCH_NM = '"+ fb.getBTCH_NM()+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + s + "'");
        return q2.list();

    }

    public long timeDiffCalculate(Timestamp t_end, Timestamp t_start){
        long diff = t_end.getTime() - t_start.getTime();
        return diff;

    }

    public String timeInString(long diff){
        int sec = (int) diff / 1000;
        int hr = sec / 3600;
        int min = (sec % 3600) / 60;
        int sec1 = (sec % 3600) % 60;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date(sec * 1000L));
    }
}
