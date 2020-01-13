package org.hibernateTest.Practice;

import org.Test.modelClass.cycleDate;
import org.Test.modelClass.frameworkBS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class TestData4 {

    private static Logger logger = LogManager.getLogger(TestTimeDiff.class);

    //    Timestamp curr_Timestamp = new Timestamp(System.currentTimeMillis());
    public static Timestamp start_PrePreProcessor = null;

    public static Timestamp end_PrePreProcessor;
    public static Timestamp start_PrePreProcessor_aWeekAgo = null;
    private static int flag_PrePreProcessor=1;

    public static Timestamp end_PreProcessor = null;
    private static int flag_PreProcessor=1;

    public static Timestamp end_Intake = null;
    private static int flag_Intake=1;

    public static Timestamp end_Scheduling = null;
    private static int flag_Scheduling=1;

    public static Timestamp end_ReleaseNConsolidation = null;
    private static int flag_ReleaseNConsolidation=1;

    public static Timestamp end_PaymentProcessing = null;
    private static int flag_PaymentProcessing=1;

    public static Timestamp end_PostPaymentExtract = null;
    private static int flag_PostPaymentExtract=1;

    public static Timestamp end_835EPS_B2B = null;
    private static int flag_835EPS_B2B=1;

    public static Timestamp end_EPSFundingFile = null;
    private static int flag_EPSFundingFile=1;

    public static Timestamp end_FundingReport = null;
    private static int flag_FundingReport=1;

    public static Timestamp end_ProviderPRA = null;
    private static int flag_ProviderPRA=1;




    public void getQuery(String curr_date, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for cycle date
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
            System.out.println(cycle_dt);
            logger.info("SAVING LOGS FOR " + cycle_dt);


            //Query for UNET-PROVIDER
            List<frameworkBS> list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "CREAT_DTTM >= '"+ cycle_dt+
                    "' order by CREAT_DTTM").list();
            System.out.println("transaction_1 for UNET-PROVIDER started");
            logger.info("transaction_1 for UNET-PROVIDER started");
            printTimeDiff(list_main, session);

            //Query for UNET-MEMBER
            List list_2 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqMbrEmptyStgBkt'," +
                    "'seqMbrITKExtrc','seqProcessTopsDBI','seqOPAGenDBITopsFeedback','seqMbrSchedule','seqMbrEmptyRlseBkt'," +
                    "'seqMbrExtLoadHarvesting','seqMbrPayment1','seqMbrOTSUnlockUnused','seqMbrOFSPmtData','seqBenHdrIDMbrGenReqFile'," +
                    "'seqBenHdrIDMbrLoadRespTbl','seqMbrEOBFile','seqeHealthMbrCreateFeedBckFiles','seqMbr03CreateFICSFile') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-18' order by CREAT_DTTM").list();
            System.out.println("transaction_2 for UNET-MEMBER started");
//            printTimeDiff_member(list_2, session);
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


    public String addExtraMargin(Timestamp tm , long extra){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YY hh:mm a");
        String df= simpleDateFormat.format(new Timestamp(tm.getTime()+extra)); //adding 30 mins to extend the ETA
        return df +" CST";
    }


    public String dateALastWeek(Timestamp current_date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current_date.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        Timestamp tm = new Timestamp(cal.getTime().getTime());
        String s = tm.toString().substring(0, 10);
        return s;
    }

    public List<frameworkBS> aWeekBackObject(String INVOK_ID, String  BTCH_NM, String dateAweekBack,Session session){
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekBack + "' order by CREAT_DTTM desc");
        if(q2.list().size()<1){
            String day = dateAweekBack.substring(8,10);
            String dateAweekback2 = dateAweekBack.replace(day,Integer.toString(Integer.valueOf(day)+1));
            return session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekback2 + "' order by CREAT_DTTM").list();
        }
        else{
        return q2.list();}

    }

    public long timeDiffCalculate(Timestamp t_end, Timestamp t_start){
        long diff = t_end.getTime() - t_start.getTime();
        return diff;

    }


    public static Timestamp getEnd_PrePreProcessor() {
        return end_PrePreProcessor;
    }

    public static void setEnd_PrePreProcessor(Timestamp end_PrePreProcessor) {
        TestData4.end_PrePreProcessor = end_PrePreProcessor;
    }

    public static Timestamp getEnd_PreProcessor() {
        return end_PreProcessor;
    }

    public static void setEnd_PreProcessor(Timestamp end_PreProcessor) {
        TestData4.end_PreProcessor = end_PreProcessor;
    }

    public static Timestamp getEnd_Intake() {
        return end_Intake;
    }

    public static void setEnd_Intake(Timestamp end_Intake) {
        TestData4.end_Intake = end_Intake;
    }

    public static Timestamp getEnd_Scheduling() {
        return end_Scheduling;
    }

    public static void setEnd_Scheduling(Timestamp end_Scheduling) {
        TestData4.end_Scheduling = end_Scheduling;
    }

    public static Timestamp getEnd_ReleaseNConsolidation() {
        return end_ReleaseNConsolidation;
    }

    public static void setEnd_ReleaseNConsolidation(Timestamp end_ReleaseNConsolidation) {
        TestData4.end_ReleaseNConsolidation = end_ReleaseNConsolidation;
    }

    public static Timestamp getEnd_PaymentProcessing() {
        return end_PaymentProcessing;
    }

    public static void setEnd_PaymentProcessing(Timestamp end_PaymentProcessing) {
        TestData4.end_PaymentProcessing = end_PaymentProcessing;
    }

    public static Timestamp getEnd_PostPaymentExtract() {
        return end_PostPaymentExtract;
    }

    public static void setEnd_PostPaymentExtract(Timestamp end_PostPaymentExtract) {
        TestData4.end_PostPaymentExtract = end_PostPaymentExtract;
    }

    public static Timestamp getEnd_835EPS_B2B() {
        return end_835EPS_B2B;
    }

    public static void setEnd_835EPS_B2B(Timestamp end_835EPS_B2B) {
        TestData4.end_835EPS_B2B = end_835EPS_B2B;
    }

    public static Timestamp getEnd_EPSFundingFile() {
        return end_EPSFundingFile;
    }

    public static void setEnd_EPSFundingFile(Timestamp end_EPSFundingFile) {
        TestData4.end_EPSFundingFile = end_EPSFundingFile;
    }

    public static Timestamp getEnd_FundingReport() {
        return end_FundingReport;
    }

    public static void setEnd_FundingReport(Timestamp end_FundingReport) {
        TestData4.end_FundingReport = end_FundingReport;
    }

    public static Timestamp getEnd_ProviderPRA() {
        return end_ProviderPRA;
    }

    public static void setEnd_ProviderPRA(Timestamp end_ProviderPRA) {
        TestData4.end_ProviderPRA = end_ProviderPRA;
    }

    public void printTimeDiff(List<frameworkBS> list, Session session){

        List<frameworkBS> l1;
        List<frameworkBS> l2;


        if(list.size()==0){
            System.out.println("Files not loaded yet! unable to calculate ETA!");
            logger.warn("Files not loaded yet! unable to calculate ETA!");
            return;
        }

        for(int i=0; i<list.size(); i++){
            System.out.println(i);


            if(i==0 && list.get(i).getBTCH_NM().equals("seqEmptyClmStg")){
                start_PrePreProcessor = list.get(i).getSTRT_DTTM();
                l1 = aWeekBackObject("NONE", "seqEmptyClmStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                start_PrePreProcessor_aWeekAgo = l1.get(1).getSTRT_DTTM();
            }

            if(list.size()<4 && flag_PrePreProcessor==1){
                flag_PrePreProcessor=0;
                System.out.println("on plan");
                l1 = aWeekBackObject("NONE", "seqLoad835DbPrePr", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), start_PrePreProcessor_aWeekAgo);
                TestData4.setEnd_PrePreProcessor(new Timestamp(sf+start_PrePreProcessor.getTime()));
//                df= simpleDateFormat.format(new Timestamp(end_PrePreProcessor.getTime()+1800000)); //adding 30 mins to extend the ETA
                System.out.println("the end time of Pre-Pre Processor "+ addExtraMargin(TestData4.getEnd_PrePreProcessor(), 1800000)/* added to current start time of file loaded*/);
                logger.info(" PrePreProcessor on plan with ETA = "+ addExtraMargin(end_PrePreProcessor, 1800000));
            }

            if(i==3 && list.get(i).getBTCH_NM().equals("seqLoad835DbPrePr")){
                if(!list.get(i).getBTCH_STS_CD().equals("C") && flag_PrePreProcessor==1){
                    flag_PrePreProcessor=0;
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqLoad835DbPrePr", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), start_PrePreProcessor_aWeekAgo);
                    end_PrePreProcessor = new Timestamp(sf+start_PrePreProcessor.getTime());
                    System.out.println("the end time of Pre-Pre Processor"+ addExtraMargin(end_PrePreProcessor, 1800000) /* added to current start time of file loaded*/);
                    logger.info(" PrePreProcessor in progress with ETA = "+addExtraMargin(end_PrePreProcessor, 1800000));
                }
                else if(list.get(i).getBTCH_STS_CD().equals("C")){
                    flag_PrePreProcessor =0;
                    end_PrePreProcessor = list.get(i).getEND_DTTM();
                }
            }



            if(!list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc") && flag_PreProcessor==1 && list.size()<5){
                if(end_PrePreProcessor != null && end_PreProcessor == null){
                    flag_PreProcessor =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqOPAUnetPreProc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_PreProcessor = new Timestamp(end_PrePreProcessor.getTime()+sf);
                    logger.info(" Pre Process on plan with ETA = "+addExtraMargin(end_PreProcessor, 1800000));
                    System.out.println("the end time of Pre-Process "+ addExtraMargin(end_PreProcessor, 1800000) /* added to end time of prepreprocessor*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc")) {
                flag_PreProcessor =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    end_PreProcessor = list.get(i).getEND_DTTM();}
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqOPAUnetPreProc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_PreProcessor = new Timestamp(end_PrePreProcessor.getTime()+sf);
                    logger.info(" Pre Process in progress with ETA = "+ addExtraMargin(end_PreProcessor, 1800000));
                    System.out.println("the end time of Pre-Process"+ addExtraMargin(end_PreProcessor, 1800000)/* added to end time of prepreprocessor*/);
                }

            }


            if(!list.get(i).getBTCH_NM().equals("seqOPAITKLdStg") && flag_Intake==1 && list.size()<6){
                if(end_PreProcessor != null && end_Intake == null){
                    flag_Intake =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAITKLdStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPASndRjctReport", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_Intake = new Timestamp(end_PreProcessor.getTime()+sf);
                    logger.info(" Intake on plan with ETA = "+ addExtraMargin(end_Intake, 1800000));
                    System.out.println("the end time of Intake"+ addExtraMargin(end_Intake, 1800000) /* added to end time of preprocessor*/);
                }
            }
            if (list.get(i).getBTCH_NM().equals("seqOPAITKLdStg") && (list.get(i).getBTCH_STS_CD().equals("C") || list.get(i).getBTCH_STS_CD().equals("R"))){
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPAITKLdStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPASndRjctReport", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                end_Intake = new Timestamp(end_PreProcessor.getTime()+sf);
                logger.info(" Intake in progress with ETA = "+ addExtraMargin(end_Intake, 1800000));
                System.out.println("the end time of Intake"+ addExtraMargin(end_Intake, 1800000) /* added to end time of preprocessor*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPASndRjctReport")){
                flag_Intake =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    end_Intake = list.get(i).getEND_DTTM(); }
          }

            if(!list.get(i).getBTCH_NM().equals("seqOPATruncateRlseTables") && flag_Scheduling== 1 && list.size()<8){
                if(end_Intake != null && end_Scheduling == null){
                    flag_Scheduling =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPATruncateRlseTables", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAPrvdrSchedulingFS", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_Scheduling = new Timestamp(end_Intake.getTime()+sf);
                    logger.info(" Scheduling on plan with ETA = "+ addExtraMargin(end_Scheduling, 1800000));
                    System.out.println("the end time of Scheduling"+ addExtraMargin(end_Scheduling, 1800000) /* added to end time of intake*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPATruncateRlseTables") && !list.get(i).getBTCH_STS_CD().equals("C")){
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPATruncateRlseTables", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAPrvdrSchedulingFS", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                end_Scheduling = new Timestamp(end_Intake.getTime()+sf);
                logger.info(" Scheduling in progress with ETA = "+ addExtraMargin(end_Scheduling, 1800000));
                System.out.println("the end time of Scheduling"+ addExtraMargin(end_Scheduling, 1800000) /* added to end time of intake*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") ){
                flag_Scheduling =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_Scheduling = list.get(i).getEND_DTTM();}
            }

            if (!list.get(i).getBTCH_NM().equals("seqOPALoadReleaseProcessing") && flag_ReleaseNConsolidation==1 && list.size()<10){
                if (end_Scheduling != null && end_ReleaseNConsolidation== null){
                    flag_ReleaseNConsolidation =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPALoadReleaseProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFSPrvConsldtData", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_ReleaseNConsolidation = new Timestamp(end_Scheduling.getTime()+sf);
                    logger.info(" Release & Consolidation on plan with ETA = "+ addExtraMargin(end_ReleaseNConsolidation, 1800000));
                    System.out.println("the end time of Release & Consolidation"+ addExtraMargin(end_ReleaseNConsolidation, 1800000) /* added to end time of Scheduling*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPALoadReleaseProcessing") && list.size()<11) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPALoadReleaseProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAFSPrvConsldtData", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                end_ReleaseNConsolidation = new Timestamp(end_Scheduling.getTime()+sf);
                logger.info(" Release & Consolidation in progress with ETA = "+ addExtraMargin(end_ReleaseNConsolidation, 1800000));
                System.out.println("the end time of Release & Consolidation"+ addExtraMargin(end_ReleaseNConsolidation, 1800000) /* added to end time of Scheduling*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAFSPrvConsldtData")){
                flag_ReleaseNConsolidation =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_ReleaseNConsolidation = list.get(i).getEND_DTTM();}
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAPaymentProcessing") && flag_PaymentProcessing==1 && list.size()<12){
                if(end_ReleaseNConsolidation != null && end_PaymentProcessing== null){
                    flag_PaymentProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAPaymentProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFullSrcPrvPymtPrcsngFnlzn", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(),  l1.get(0).getSTRT_DTTM());
                    end_PaymentProcessing = new Timestamp(end_ReleaseNConsolidation.getTime()+sf);
                    logger.info(" Payment Processing on plan with ETA = "+ addExtraMargin(end_PaymentProcessing, 1800000));
                    System.out.println("the end time of Payment Processing"+ addExtraMargin(end_PaymentProcessing, 1800000) /* added to end time of Release n consolidation*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAPaymentProcessing") && !list.get(i).getBTCH_STS_CD().equals("C")) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPAPaymentProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAFullSrcPrvPymtPrcsngFnlzn", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(),  l1.get(0).getSTRT_DTTM());
                end_PaymentProcessing = new Timestamp(end_ReleaseNConsolidation.getTime()+sf);
                logger.info(" Payment Processing in progress with ETA = "+ addExtraMargin(end_PaymentProcessing, 1800000));
                System.out.println("the end time of Payment Processing"+ addExtraMargin(end_PaymentProcessing, 1800000) /* added to end time of Release n consolidation*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn")){
                flag_PaymentProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_PaymentProcessing = list.get(i).getEND_DTTM();}
            }

            if(!list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt") && flag_PostPaymentExtract==1 && list.size()<14){
                if(end_PaymentProcessing!= null && end_PostPaymentExtract==null){
                    flag_PostPaymentExtract =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqCreateUCASDailyExt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate( l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_PostPaymentExtract = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    logger.info(" Post Payment Extract (OTS,TOPS, UCAS on plan with ETA = "+ addExtraMargin(end_PostPaymentExtract, 1800000));
                    System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ addExtraMargin(end_PostPaymentExtract, 1800000) /* added to end time of Payment Processing*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt")){
                flag_PostPaymentExtract =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_PostPaymentExtract = list.get(i).getEND_DTTM();}
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqCreateUCASDailyExt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate( l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_PostPaymentExtract = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    logger.info(" Post Payment Extract (OTS,TOPS, UCAS in progress with ETA = "+ addExtraMargin(end_PostPaymentExtract, 1800000));
                    System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ addExtraMargin(end_PostPaymentExtract, 1800000) /* added to end time of Payment Processing*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPA835PostpaymentLoad") && flag_835EPS_B2B==1 && list.size()<15){
                if(end_PaymentProcessing!=null && end_835EPS_B2B == null){
                    flag_835EPS_B2B =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPA835PostpaymentLoad", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPA835ValX12FileCreationPayables", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_835EPS_B2B = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    logger.info(" 835 EPS/B2B on plan with ETA = "+ addExtraMargin(end_835EPS_B2B, 1800000));
                    System.out.println("the end time of 835 EPS/B2B"+ addExtraMargin(end_835EPS_B2B, 1800000) /* added to end time of Payment Processing*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPA835PostpaymentLoad") && !list.get(i).getBTCH_STS_CD().equals("C")) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPA835PostpaymentLoad", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPA835ValX12FileCreationPayables", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                end_835EPS_B2B = new Timestamp(end_PaymentProcessing.getTime()+sf);
                logger.info(" 835 EPS/B2B in progress with ETA = "+ addExtraMargin(end_835EPS_B2B, 1800000));
                System.out.println("the end time of 835 EPS/B2B"+ addExtraMargin(end_835EPS_B2B, 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") ){
                flag_835EPS_B2B =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_835EPS_B2B = list.get(i).getEND_DTTM();}
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile") && flag_EPSFundingFile==1 && list.size()<17){
                if(end_835EPS_B2B!=null && end_EPSFundingFile== null){
                    flag_EPSFundingFile =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPACreateEPSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_EPSFundingFile = new Timestamp(end_835EPS_B2B.getTime()+sf);
                    logger.info(" EPS Funding File on plan with ETA = "+  addExtraMargin(end_EPSFundingFile, 1800000) );
                    System.out.println("the end time of EPS Funding File"+ addExtraMargin(end_EPSFundingFile, 1800000) /* added to end time of 835EPS_B2B*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile")){
                flag_EPSFundingFile =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_EPSFundingFile = list.get(i).getEND_DTTM();}
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPACreateEPSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_EPSFundingFile = new Timestamp(end_835EPS_B2B.getTime()+sf);
                    logger.info(" EPS Funding File in progress with ETA = "+  addExtraMargin(end_EPSFundingFile, 1800000) );
                    System.out.println("the end time of EPS Funding File"+ addExtraMargin(end_EPSFundingFile, 1800000) /* added to end time of 835EPS_B2B*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS") && flag_FundingReport==1 && list.size()<18) {
                if(end_EPSFundingFile!=null && end_FundingReport==null){
                    flag_FundingReport = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAEPSReport_FS", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_FundingReport = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    logger.info(" Funding Report on plan with ETA = "+ addExtraMargin(end_FundingReport, 1800000));
                    System.out.println("the end time of Funding Report "+ addExtraMargin(end_FundingReport, 1800000) /* added to end time of EPS Funding file*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS")){
                flag_FundingReport = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_FundingReport = list.get(i).getEND_DTTM();}
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPAEPSReport_FS", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_FundingReport = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    logger.info(" Funding Report in progress with ETA = "+ addExtraMargin(end_FundingReport, 1800000));
                    System.out.println("the end time of Funding Report "+ addExtraMargin(end_FundingReport, 1800000) /* added to end time of EPS Funding file*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile") && flag_ProviderPRA==1 && list.size()<19){
                if(end_EPSFundingFile!=null && end_ProviderPRA==null){
                    flag_ProviderPRA =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAProvPRAFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_ProviderPRA = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    logger.info(" Provider PRA on plan with ETA = "+ addExtraMargin(end_ProviderPRA, 1800000));
                    System.out.println("the end time of Provider PRA "+ addExtraMargin(end_ProviderPRA, 1800000) /* added to end time of EPS Funding file*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile")){
                flag_ProviderPRA =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                end_ProviderPRA = list.get(i).getEND_DTTM();}
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPAProvPRAFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_ProviderPRA = new Timestamp(end_EPSFundingFile.getTime()+sf);
                    logger.info(" Provider PRA in progress with ETA = "+ addExtraMargin(end_ProviderPRA, 1800000));
                    System.out.println("the end time of Provider PRA "+ addExtraMargin(end_ProviderPRA, 1800000) /* added to end time of EPS Funding file*/);
                }
            }

                  }


    }
}
