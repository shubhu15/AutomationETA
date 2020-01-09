package org.hibernateTest.Practice;

import org.Test.modelClass.cycleDate;
import org.Test.modelClass.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UNETMemberETA {

    public static Timestamp start_InrakeFileLoading = null;

    public static Timestamp end_InrakeFileLoading;
    public static Timestamp start_InrakeFileLoading_aWeekAgo = null;
    private static int flag_InrakeFileLoading=1;

    public static Timestamp end_DBIProcessing = null;
    private static int flag_DBIProcessing=1;

    public static Timestamp end_SchedulingOfSub = null;
    private static int flag_SchedulingOfSub=1;

    public static Timestamp end_Harvesting = null;
    private static int flag_Harvesting=1;

    public static Timestamp end_ConsolidationNOverpayment = null;
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
            System.out.println(cycle_dt);
            cycle_dt="08-Jan-20 02.08.08 AM";
//            logger.info("SAVING LOGS FOR " + cycle_dt + "at " +curr_date);


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

    public static Timestamp getStart_InrakeFileLoading() {
        return start_InrakeFileLoading;
    }

    public static void setStart_InrakeFileLoading(Timestamp start_InrakeFileLoading) {
        UNETMemberETA.start_InrakeFileLoading = start_InrakeFileLoading;
    }

    public static Timestamp getEnd_InrakeFileLoading() {
        return end_InrakeFileLoading;
    }

    public static void setEnd_InrakeFileLoading(Timestamp end_InrakeFileLoading) {
        UNETMemberETA.end_InrakeFileLoading = end_InrakeFileLoading;
    }

    public static Timestamp getStart_InrakeFileLoading_aWeekAgo() {
        return start_InrakeFileLoading_aWeekAgo;
    }

    public static void setStart_InrakeFileLoading_aWeekAgo(Timestamp start_InrakeFileLoading_aWeekAgo) {
        UNETMemberETA.start_InrakeFileLoading_aWeekAgo = start_InrakeFileLoading_aWeekAgo;
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
}
