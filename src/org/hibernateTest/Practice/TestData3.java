package org.hibernateTest.Practice;

import org.Test.modelClass.cycleDate;
import org.Test.modelClass.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TestData3 {

    Timestamp curr_Timestamp = new Timestamp(System.currentTimeMillis());
    private static SessionFactory sessionFactory = null;
    private static Timestamp start_PrePreProcessor = null;

    private static Timestamp end_PrePreProcessor = null;
    private static Timestamp start_PrePreProcessor_aWeekAgo = null;
    private static Timestamp end_PrePreProcessor_aWeekAgo= null;

    private static Timestamp end_PreProcessor = null;
    private static Timestamp start_PreProcessor_aWeekAgo = null;
    private static Timestamp end_PreProcessor_aWeekAgo = null;

    private static Timestamp end_Intake = null;
    private static Timestamp start_Intake_aWeekAgo=null;
    private static Timestamp end_Intake_aWeekAgo = null;

    private static Timestamp end_Scheduling = null;
    private static Timestamp start_Scheduling_aWeekAgo = null;
    private static Timestamp end_Scheduling_aWeekAgo = null;

    private static Timestamp end_ReleaseNConsolidation = null;
    private static Timestamp start_ReleaseNConsolidation_aWeekAgo= null;
    private static Timestamp end_ReleaseNConsolidation_aWeekAgo = null;

    private static Timestamp end_PaymentProcessing = null;
    private static Timestamp start_PaymentProcessing_aWeekAgo = null;
    private static Timestamp end_PaymentProcessing_aWeekAgo = null;

    private static Timestamp end_PostPaymentExtract = null;
    private static Timestamp start_PostPaymentExtract_aWeekAgo = null;
    private static Timestamp end_PostPaymentExtract_aWeekAgo = null;

    private static Timestamp end_835EPS_B2B = null;
    private static Timestamp start_835EPS_B2B_aWeekAgo = null;
    private static Timestamp end_835EPS_B2B_aWeekAgo = null;

    private static Timestamp end_EPSFundingFile = null;
    private static Timestamp start_EPSFundingFile_aWeekAgo = null;
    private static Timestamp end_EPSFundingFile_aWeekAgo = null;

    private static Timestamp end_FundingReport = null;
    private static Timestamp start_FundingReport_aWeekAgo = null;
    private static Timestamp end_FundingReport_aWeekAgo = null;

    private static Timestamp end_ProviderPRA = null;
    private static Timestamp start_ProviderPRA_aWeekAgo = null;
    private static Timestamp end_ProviderPRA_aWeekAgo = null;


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
        current_date = "2019-11-26";
        sessionFactory = testData.buildSessionFactory();
        testData.getQuery(current_date);

    }

    public void getQuery(String curr_date){

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -2);
        curr_Timestamp = new Timestamp(cal.getTimeInMillis());

        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            System.out.println(list_n.get(0).getCREAT_DTTM());
            //Query for UNET-PROVIDER
            List<frameworkBS> list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '"+ curr_date+
                    "' order by CREAT_DTTM").list();
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


    public String dateALastWeek(Timestamp current_date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current_date.getTime());
        cal.add(Calendar.DAY_OF_MONTH, days);

        Timestamp tm = new Timestamp(cal.getTime().getTime());
        String s = tm.toString().substring(0, 10);
        return s;
    }

    public List<frameworkBS> aWeekBackObject(String INVOK_ID, String  BTCH_NM, String dateAweekBack,Session session){
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekBack + "' order by CREAT_DTTM desc");
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


    public void printTimeDiff(List<frameworkBS> list, Session session){

        List<frameworkBS> l1;
        List<frameworkBS> l2;
        System.out.println(list.size());

        for(int i=0; i<list.size(); i++){
            System.out.println(i);

            System.out.println(list.get(i).getBTCH_NM());
            if(i==0 && list.get(i).getBTCH_NM().equals("seqEmptyClmStg")){
                start_PrePreProcessor = list.get(i).getSTRT_DTTM();

                l1 = aWeekBackObject("NONE", "seqEmptyClmStg", dateALastWeek(curr_Timestamp,-7), session);
                start_PrePreProcessor_aWeekAgo = l1.get(1).getSTRT_DTTM();
            }
            if(i==3 && list.get(i).getBTCH_NM().equals("seqLoad835DbPrePr")){
                if(list.get(i).getBTCH_STS_CD().equals("C")){
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqLoad835DbPrePr", dateALastWeek(curr_Timestamp, -7),session);
                    end_PrePreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PrePreProcessor_aWeekAgo, start_PrePreProcessor_aWeekAgo);
                    end_PrePreProcessor = new Timestamp(sf+start_PrePreProcessor.getTime());
                    System.out.println("the end time of Pre-Pre Processor"+ end_PrePreProcessor /* added to current start time of file loaded*/);
                }
//                else if(list.get(i).getBTCH_STS_CD().equals("C")){
//                    end_PrePreProcessor = list.get(i).getEND_DTTM();
//                }
            }
//            else if(i<3)



            else if(list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc")){
                if(end_PrePreProcessor != null && end_PreProcessor == null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqOPAUnetPreProc", dateALastWeek(curr_Timestamp,-7), session);
                    start_PreProcessor_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_PreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PreProcessor_aWeekAgo, start_PreProcessor_aWeekAgo);
                    end_PreProcessor = new Timestamp(end_PrePreProcessor.getTime()+sf);
                    System.out.println("the end time of Pre-Process"+ end_PreProcessor/* added to end time of prepreprocessor*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_PreProcessor = list.get(i).getEND_DTTM();
//            }


            else if(list.get(i).getBTCH_NM().equals("seqOPAITKLdStg")){
                if(end_PreProcessor != null && end_Intake == null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAITKLdStg", dateALastWeek(curr_Timestamp, -7), session);
                    l2 = aWeekBackObject("UNET", "seqOPASndRjctReport", dateALastWeek(curr_Timestamp, -7), session);
                    start_Intake_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_Intake_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Intake_aWeekAgo, start_Intake_aWeekAgo);
                    end_Intake = new Timestamp(end_PreProcessor.getTime()+sf);
                    System.out.println("the end time of Intake"+ end_Intake /* added to end time of preprocessor*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPASndRjctReport") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_Intake = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPATruncateRlseTables")){
                if(end_Intake != null && end_Scheduling == null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPATruncateRlseTables", dateALastWeek(curr_Timestamp, -7), session);
                    l2 = aWeekBackObject("UNET", "seqOPAPrvdrSchedulingFS", dateALastWeek(curr_Timestamp, -7),session);
                    start_Scheduling_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_Scheduling_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Scheduling_aWeekAgo, start_Scheduling_aWeekAgo);
                    end_Scheduling = new Timestamp(end_Intake.getTime()+sf);
                    System.out.println("the end time of Scheduling"+ end_Scheduling /* added to end time of intake*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_Scheduling = list.get(i).getEND_DTTM();
//            }

            else if (list.get(i).getBTCH_NM().equals("seqOPALoadReleaseProcessing")){
                if (end_Scheduling != null && end_ReleaseNConsolidation== null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPALoadReleaseProcessing", dateALastWeek(curr_Timestamp, -7), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFSPrvConsldtData", dateALastWeek(curr_Timestamp,-7),session);
                    start_ReleaseNConsolidation_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_ReleaseNConsolidation_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_ReleaseNConsolidation_aWeekAgo, start_ReleaseNConsolidation_aWeekAgo);
                    end_ReleaseNConsolidation = new Timestamp(end_Scheduling.getTime()+sf);
                    System.out.println("the end time of Release & Consolidation"+ end_ReleaseNConsolidation /* added to end time of Scheduling*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAFSPrvConsldtData") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_ReleaseNConsolidation = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPAPaymentProcessing")){
                if(end_ReleaseNConsolidation != null && end_PaymentProcessing== null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAPaymentProcessing", dateALastWeek(curr_Timestamp,-7), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFullSrcPrvPymtPrcsngFnlzn", dateALastWeek(curr_Timestamp, -7),session);
                    start_PaymentProcessing_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_PaymentProcessing_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PaymentProcessing_aWeekAgo, start_PaymentProcessing_aWeekAgo);
                    end_PaymentProcessing = new Timestamp(end_ReleaseNConsolidation.getTime()+sf);
                    System.out.println("the end time of Payment Processing"+ end_PaymentProcessing /* added to end time of Release n consolidation*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_PaymentProcessing = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt")){
                if(end_PaymentProcessing!= null && end_PostPaymentExtract==null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqCreateUCASDailyExt", dateALastWeek(curr_Timestamp, -7), session);
                    start_PostPaymentExtract_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_PostPaymentExtract_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PostPaymentExtract_aWeekAgo, start_PostPaymentExtract_aWeekAgo);
                    end_PostPaymentExtract = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ end_PostPaymentExtract /* added to end time of Payment Processing*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_PostPaymentExtract = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPA835PostpaymentLoad")){
                if(end_PaymentProcessing!=null && end_835EPS_B2B == null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPA835PostpaymentLoad", dateALastWeek(curr_Timestamp,-7), session);
                    l2 = aWeekBackObject("UNET", "seqOPA835ValX12FileCreationPayables", dateALastWeek(curr_Timestamp, -7),session);
                    start_835EPS_B2B_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_835EPS_B2B_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_835EPS_B2B_aWeekAgo, start_835EPS_B2B_aWeekAgo);
                    end_835EPS_B2B = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    System.out.println("the end time of 835 EPS/B2B"+ end_835EPS_B2B /* added to end time of Payment Processing*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_835EPS_B2B = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile")){
                if(end_835EPS_B2B!=null && end_EPSFundingFile== null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPACreateEPSFile", dateALastWeek(curr_Timestamp,-7), session);
                    start_EPSFundingFile_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_EPSFundingFile_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_EPSFundingFile_aWeekAgo, start_EPSFundingFile_aWeekAgo);
                    end_EPSFundingFile = new Timestamp(end_835EPS_B2B.getTime()+sf);
                    System.out.println("the end time of EPS Funding File"+ end_EPSFundingFile /* added to end time of 835EPS_B2B*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_EPSFundingFile = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS")){
                if(end_EPSFundingFile!=null && end_FundingReport==null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAEPSReport_FS", dateALastWeek(curr_Timestamp, -7), session);
                    start_FundingReport_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_FundingReport_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_FundingReport_aWeekAgo, start_FundingReport_aWeekAgo);
                    end_FundingReport = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    System.out.println("the end time of Funding Report "+ end_FundingReport /* added to end time of EPS Funding file*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_FundingReport = list.get(i).getEND_DTTM();
//            }

            else if(list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile")){
                if(end_EPSFundingFile!=null && end_ProviderPRA==null){
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAProvPRAFile", dateALastWeek(curr_Timestamp,-7), session);
                    start_ProviderPRA_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_ProviderPRA_aWeekAgo = l1.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_ProviderPRA_aWeekAgo, start_ProviderPRA_aWeekAgo);
                    end_ProviderPRA = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    System.out.println("the end time of Provider PRA "+ end_ProviderPRA /* added to end time of EPS Funding file*/);
                }
            }
//            else if(list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile") && list.get(i).getBTCH_STS_CD().equals("C")){
//                end_ProviderPRA = list.get(i).getEND_DTTM();
//            }
        }


    }
}
