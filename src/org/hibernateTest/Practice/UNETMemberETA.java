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

public class UNETMemberETA {
    private static Logger logger = LogManager.getLogger(TestTimeDiff.class);


    private static SessionFactory sessionFactory = null;
    public static Timestamp start_IntakeFileLoading = null;

    public static Timestamp end_IntakeFileLoading;
    public static Timestamp start_IntakeFileLoading_aWeekAgo = null;
    private static int flag_IntakeFileLoading =1;

    public static Timestamp end_DBIProcessing = null;
    private static int flag_DBIProcessing=1;

    public static Timestamp end_SchedulingOfSub = null;
    private static int flag_SchedulingOfSub=1;

    public static Timestamp end_Harvesting = null;
    private static int flag_Harvesting=1;

    public static Timestamp end_ConsolidationNOverpayment = null;
    private Timestamp end_Payment = null;
    private static int flag_ConsolidationNOverpayment=1;

    public static Timestamp end_CorePayment = null;
    private static int flag_CorePayment=1;

    public static Timestamp end_BenefitHeaderProcessing = null;
    private static int flag_BenefitHeaderProcessing=1;

    public static Timestamp end_MemberEhealthFeedbackFile = null;
    private static int flag_MemberEhealthFeedbackFile=1;

    public static Timestamp end_PostPayExtract = null;
    private static int flag_PostPayExtract=1;



    public void getQuery(String curr_date, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for cycle date
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
            System.out.println(cycle_dt+ " at " +curr_date);
//            cycle_dt="08-Jan-20 02.08.08 AM";
            logger.info("SAVING LOGS FOR " + cycle_dt + " at " +curr_date);


            //Query for UNET-MEMBER
            List list_2 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqMbrEmptyStgBkt'," +
                    "'seqMbrITKExtrc','seqProcessTopsDBI','seqOPAGenDBITopsFeedback','seqMbrSchedule','seqMbrEmptyRlseBkt'," +
                    "'seqMbrExtLoadHarvesting','seqMbrPayment1','seqMbrOTSUnlockUnused','seqMbrOFSPmtData','seqBenHdrIDMbrGenReqFile'," +
                    "'seqBenHdrIDMbrLoadRespTbl','seqMbrEOBFile','seqeHealthMbrCreateFeedBckFiles','seqMbr03CreateFICSFile') and " +
                            "CREAT_DTTM >= '"+ cycle_dt+"' order by CREAT_DTTM").list();
            System.out.println("transaction_2 for UNET-MEMBER started");
            printTimeDiff_member(list_2, session);
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

    private void printTimeDiff_member(List<frameworkBS> list, Session session) {
        List<frameworkBS> l1;
        List<frameworkBS> l2;


        if(list.size()==0){
            System.out.println("Files not loaded yet! unable to calculate ETA!");
            logger.warn("Files not loaded yet! unable to calculate ETA!");
            return;
        }

        for(int i=0; i<list.size(); i++) {
            System.out.println(i);


            if (i == 0 && list.get(i).getBTCH_NM().equals("seqMbrEmptyStgBkt")) {
                setStart_IntakeFileLoading(list.get(i).getSTRT_DTTM());
                l1 = aWeekBackObject("NONE", "seqMbrEmptyStgBkt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                setStart_IntakeFileLoading_aWeekAgo(l1.get(0).getSTRT_DTTM());
            }


            if (!list.get(i).getBTCH_NM().equals("seqMbrITKExtrc") && flag_IntakeFileLoading == 1 && list.size() < 2) {
                if (UNETMemberETA.getEnd_IntakeFileLoading() == null) {
                    flag_IntakeFileLoading = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqMbrITKExtrc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), getStart_IntakeFileLoading_aWeekAgo());
                    setEnd_IntakeFileLoading(new Timestamp(sf+UNETMemberETA.getStart_IntakeFileLoading().getTime()));
                    logger.info(" Intake File Loading on plan with ETA = " + addExtraMargin(getEnd_IntakeFileLoading(), 1800000));
                    System.out.println("the end time of Intake File Loading " + addExtraMargin(UNETMemberETA.getEnd_IntakeFileLoading(), 1800000) /* added to end time of prepreprocessor*/);
                }
            }
            if (list.get(i).getBTCH_NM().equals("seqMbrITKExtrc") && list.size()<3) {
                flag_IntakeFileLoading = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")) {
                    setEnd_IntakeFileLoading(list.get(i).getEND_DTTM());
                } else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqMbrITKExtrc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), start_IntakeFileLoading_aWeekAgo);
                    setEnd_IntakeFileLoading(new Timestamp(sf+UNETMemberETA.getStart_IntakeFileLoading().getTime()));
                    logger.info(" Intake File Loading in progress with ETA = " + addExtraMargin(getEnd_IntakeFileLoading(), 1800000));
                    System.out.println("the end time of Intake File Loading " + addExtraMargin(UNETMemberETA.getEnd_IntakeFileLoading(), 1800000)/* added to end time of prepreprocessor*/);
                }

            }

            if(!list.get(i).getBTCH_NM().equals("seqProcessTopsDBI") && flag_DBIProcessing==1 && list.size()<3){
                if(getEnd_IntakeFileLoading()!=null && getEnd_DBIProcessing() == null){
                    flag_DBIProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAGenDBITopsFeedback", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_DBIProcessing( new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 120000));
                    logger.info(" DBIProcessing on plan with ETA = "+ addExtraMargin(getEnd_DBIProcessing(), 1800000));
                    System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                flag_DBIProcessing=0;
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAGenDBITopsFeedback", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 120000));
                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(getEnd_DBIProcessing(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }

            if (!list.get(i).getBTCH_NM().equals("seqMbrSchedule") && flag_SchedulingOfSub == 1 && list.size() <=5) {
                if (getEnd_DBIProcessing()!=null  && UNETMemberETA.getEnd_SchedulingOfSub() == null) {
                    flag_SchedulingOfSub = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqMbrSchedule", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_SchedulingOfSub(new Timestamp(sf+UNETMemberETA.getEnd_DBIProcessing().getTime()));
                    logger.info(" Scheduling Of Sub on plan with ETA = " + addExtraMargin(getEnd_SchedulingOfSub(), 1800000));
                    System.out.println("the end time of Scheduling Of Sub " + addExtraMargin(getEnd_SchedulingOfSub(), 1800000) /* added to end time of prepreprocessor*/);
                }
            }

            //////////////////
            if (list.get(i).getBTCH_NM().equals("seqMbrSchedule")) {
                flag_IntakeFileLoading = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")) {
                    UNETMemberETA.setEnd_IntakeFileLoading(list.get(i).getEND_DTTM());
                } else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqMbrITKExtrc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), start_IntakeFileLoading_aWeekAgo);
                    UNETMemberETA.setEnd_IntakeFileLoading(new Timestamp(sf+UNETMemberETA.getStart_IntakeFileLoading().getTime()));
//                    logger.info(" Intake File Loading in progress with ETA = " + addExtraMargin(end_PreProcessor, 1800000));
                    System.out.println("the end time of Intake File Loading " + addExtraMargin(UNETMemberETA.getEnd_IntakeFileLoading(), 1800000)/* added to end time of prepreprocessor*/);
                }

            }

            if(!list.get(i).getBTCH_NM().equals("seqMbrEmptyRlseBkt") && flag_Harvesting==1 && list.size()<7){
                if(getEnd_SchedulingOfSub()!=null && getEnd_Harvesting() == null){
                    flag_Harvesting =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrEmptyRlseBkt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("PE", "seqMbrExtLoadHarvesting", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_Harvesting( new Timestamp(getEnd_SchedulingOfSub().getTime()+sf));
                    logger.info(" Harvesting on plan with ETA = "+ addExtraMargin(getEnd_Harvesting(), 1800000));
                    System.out.println("the end time of Harvesting "+ addExtraMargin(getEnd_Harvesting(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrEmptyRlseBkt") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAGenDBITopsFeedback", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }


            if(!list.get(i).getBTCH_NM().equals("seqMbrPayment1") && flag_ConsolidationNOverpayment==1 && list.size()<9){
                if(getEnd_Harvesting()!=null && getEnd_ConsolidationNOverpayment() == null){
                    flag_ConsolidationNOverpayment =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrPayment1", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_Payment = new Timestamp(getEnd_Harvesting().getTime()+300000+timeDiffCalculate(l1.get(0).getEND_DTTM(),l1.get(0).getSTRT_DTTM()));
                    setEnd_ConsolidationNOverpayment( new Timestamp(getEnd_Harvesting().getTime()+sf+300000));
                    logger.info(" Consolidation N Overpayment on plan with ETA = "+ addExtraMargin(getEnd_ConsolidationNOverpayment(), 1800000));
                    System.out.println("the end time of Consolidation N Overpayment "+ addExtraMargin(getEnd_ConsolidationNOverpayment(), 1800000) /* added to end time of Payment Processing*/);
                }
            }

            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }

            if(!list.get(i).getBTCH_NM().equals("seqBenHdrIDMbrGenReqFile") && flag_BenefitHeaderProcessing==1 && list.size()<10){
                if(getEnd_ConsolidationNOverpayment()!=null && getEnd_BenefitHeaderProcessing() == null){
                    flag_BenefitHeaderProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqBenHdrIDMbrGenReqFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqBenHdrIDMbrLoadRespTbl", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_BenefitHeaderProcessing( new Timestamp(end_Payment.getTime()+sf+180000));
                    logger.info(" Benefit Header Processing on plan with ETA = "+ addExtraMargin(getEnd_BenefitHeaderProcessing(), 1800000));
                    System.out.println("the end time of Benefit Header Processing "+ addExtraMargin(getEnd_BenefitHeaderProcessing(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }


            if(!list.get(i).getBTCH_NM().equals("seqMbrOFSPmtData") && flag_CorePayment==1 && list.size()<12){
                if(getEnd_ConsolidationNOverpayment()!=null && getEnd_CorePayment() == null){
                    flag_CorePayment =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrOFSPmtData", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_CorePayment( new Timestamp(getEnd_ConsolidationNOverpayment().getTime()+sf));
                    logger.info(" Core Payment on plan with ETA = "+ addExtraMargin(getEnd_CorePayment(), 1800000));
                    System.out.println("the end time of Core Payment "+ addExtraMargin(getEnd_CorePayment(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }

            if(!list.get(i).getBTCH_NM().equals("seqMbrEOBFile") && flag_MemberEhealthFeedbackFile==1 && list.size()<14){
                if(getEnd_BenefitHeaderProcessing()!=null && getEnd_MemberEhealthFeedbackFile() == null){
                    flag_MemberEhealthFeedbackFile =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrEOBFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqeHealthMbrCreateFeedBckFiles", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_MemberEhealthFeedbackFile( new Timestamp(getEnd_BenefitHeaderProcessing().getTime()+sf+180000));
                    logger.info(" Member Ehealth FeedbackFile on plan with ETA = "+ addExtraMargin(getEnd_MemberEhealthFeedbackFile(), 1800000));
                    System.out.println("the end time of Member Ehealth FeedbackFile "+ addExtraMargin(getEnd_MemberEhealthFeedbackFile(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }

            if(!list.get(i).getBTCH_NM().equals("seqMbr03CreateFICSFile") && flag_PostPayExtract==1 && list.size()<16){
                if(getEnd_MemberEhealthFeedbackFile()!=null && getEnd_PostPayExtract() == null){
                    flag_PostPayExtract =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("401","seqMbr03CreateFICSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_PostPayExtract( new Timestamp(getEnd_MemberEhealthFeedbackFile().getTime()+sf));
                    logger.info(" Post Pay Extract on plan with ETA = "+ addExtraMargin(getEnd_PostPayExtract(), 1800000));
                    System.out.println("the end time of  Post Pay Extract "+ addExtraMargin(getEnd_PostPayExtract(), 1800000) /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());}
            }
        }
    }


    public static Timestamp getStart_IntakeFileLoading() {
        return start_IntakeFileLoading;
    }

    public static void setStart_IntakeFileLoading(Timestamp start_IntakeFileLoading) {
        UNETMemberETA.start_IntakeFileLoading = start_IntakeFileLoading;
    }

    public static Timestamp getEnd_IntakeFileLoading() {
        return end_IntakeFileLoading;
    }

    public static void setEnd_IntakeFileLoading(Timestamp end_IntakeFileLoading) {
        UNETMemberETA.end_IntakeFileLoading = end_IntakeFileLoading;
    }

    public static Timestamp getStart_IntakeFileLoading_aWeekAgo() {
        return start_IntakeFileLoading_aWeekAgo;
    }

    public static void setStart_IntakeFileLoading_aWeekAgo(Timestamp start_IntakeFileLoading_aWeekAgo) {
        UNETMemberETA.start_IntakeFileLoading_aWeekAgo = start_IntakeFileLoading_aWeekAgo;
    }

    public static Timestamp getEnd_DBIProcessing() {
        return end_DBIProcessing;
    }

    public static void setEnd_DBIProcessing(Timestamp end_DBIProcessing) {
        UNETMemberETA.end_DBIProcessing = end_DBIProcessing;
    }

    public static Timestamp getEnd_SchedulingOfSub() {
        return end_SchedulingOfSub;
    }

    public static void setEnd_SchedulingOfSub(Timestamp end_SchedulingOfSub) {
        UNETMemberETA.end_SchedulingOfSub = end_SchedulingOfSub;
    }

    public static Timestamp getEnd_Harvesting() {
        return end_Harvesting;
    }

    public static void setEnd_Harvesting(Timestamp end_Harvesting) {
        UNETMemberETA.end_Harvesting = end_Harvesting;
    }

    public static Timestamp getEnd_ConsolidationNOverpayment() {
        return end_ConsolidationNOverpayment;
    }

    public static void setEnd_ConsolidationNOverpayment(Timestamp end_ConsolidationNOverpayment) {
        UNETMemberETA.end_ConsolidationNOverpayment = end_ConsolidationNOverpayment;
    }

    public static Timestamp getEnd_CorePayment() {
        return end_CorePayment;
    }

    public static void setEnd_CorePayment(Timestamp end_CorePayment) {
        UNETMemberETA.end_CorePayment = end_CorePayment;
    }

    public static Timestamp getEnd_BenefitHeaderProcessing() {
        return end_BenefitHeaderProcessing;
    }

    public static void setEnd_BenefitHeaderProcessing(Timestamp end_BenefitHeaderProcessing) {
        UNETMemberETA.end_BenefitHeaderProcessing = end_BenefitHeaderProcessing;
    }

    public static Timestamp getEnd_MemberEhealthFeedbackFile() {
        return end_MemberEhealthFeedbackFile;
    }

    public static void setEnd_MemberEhealthFeedbackFile(Timestamp end_MemberEhealthFeedbackFile) {
        UNETMemberETA.end_MemberEhealthFeedbackFile = end_MemberEhealthFeedbackFile;
    }

    public static Timestamp getEnd_PostPayExtract() {
        return end_PostPayExtract;
    }

    public static void setEnd_PostPayExtract(Timestamp end_PostPayExtract) {
        UNETMemberETA.end_PostPayExtract = end_PostPayExtract;
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

}
