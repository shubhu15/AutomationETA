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

            //Query for just UNET-PROVIDER
            List list_1 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '2019-11-18' order by CREAT_DTTM").list();
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

    public void printTimeDiff(List list_1, Session session) {
        Iterator itr_1 = list_1.iterator();
        System.out.println(list_1.size());

        Timestamp current_time = new Timestamp(System.currentTimeMillis());
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


        //Pre-Pre Processor (file loading) ---to get the start time of the UNET-Provider
        if(itr_1.hasNext()){
            frameworkBS fb = (frameworkBS) itr_1.next();
            System.out.println(fb.getBTCH_NM() +" " + fb.getBTCH_STS_CD() );
            if(fb.getBTCH_NM().equals("seqEmptyClmStg") && fb.getBTCH_STS_CD().equals("C")){
                start_PrePreProcessor = fb.getSTRT_DTTM();
                if(list_1.size()<4){ System.out.println("in progress");}
                l1 = aWeekBackObject(fb, session);
                start_PrePreProcessor_aWeekAgo = l1.get(0).getSTRT_DTTM();
            }
        }

        //Pre-Pre Processor (completion) ---to get ETA of Pre-Pre Processor
        //if the process is not yet completed.
        if (list_1.size()==4){
            frameworkBS fb = (frameworkBS) list_1.get(3);
            if(fb.getBTCH_NM().equals("seqLoad835DbPrePr ") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                end_PrePreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PrePreProcessor_aWeekAgo, start_PrePreProcessor_aWeekAgo);
//               long diff_new = timeDiffCalculate(current_time, start_PrePreProcessor);
                end_PrePreProcessor = new Timestamp(sf+start_PrePreProcessor.getTime());
                System.out.println("the end time of Pre-Pre Processor"+ end_PrePreProcessor /* added to current start time of file loaded*/);

            }

        }


        //if Pre-Pre Processor is already completed
        else {
            System.out.println("job done");
            frameworkBS fb = (frameworkBS) list_1.get(3);
            end_PrePreProcessor = fb.getEND_DTTM();
            System.out.println("the end time of Pre-Pre Processor"+ end_PrePreProcessor);
        }

//        itr_1.next();
//        itr_1.next();
//        itr_1.next();


        for (int i = 4; i<list_1.size();i++){
            frameworkBS fb = (frameworkBS) list_1.get(i);

            //Pre-Processor started but not completed
            if(fb.getBTCH_NM().equals("seqOPAUnetPreProc") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_PreProcessor_aWeekAgo = l1.get(0).getSTRT_DTTM();
                end_PreProcessor_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PreProcessor_aWeekAgo, start_PreProcessor_aWeekAgo);
                end_PreProcessor = new Timestamp(end_PrePreProcessor.getTime()+sf);
                System.out.println("the end time of Pre-Process"+ end_PreProcessor/* added to end time of prepreprocessor*/);
            }

            //Intake started but not completed
            else if(fb.getBTCH_NM().equals("seqOPAITKLdStg") && fb.getBTCH_STS_CD().equals("C")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPASndRjctReport") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_Intake_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_Intake_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Intake_aWeekAgo, start_Intake_aWeekAgo);
                    end_Intake = new Timestamp(end_PreProcessor.getTime()+sf);

                 /*   //debugging to check the time difference of current day
                    long df =timeDiffCalculate(fb2.getEND_DTTM(), fb.getSTRT_DTTM());
                    System.out.println(sf +"  " + df);
                    System.out.println("New end time"+ new Timestamp(end_PreProcessor.getTime()+df));  this shows correct results  */

                    System.out.println("the end time of Intake"+ end_Intake /* added to end time of preprocessor*/);

                }
            }

            //Scheduling started but not completed
            else if(fb.getBTCH_NM().equals("seqOPATruncateRlseTables") && fb.getBTCH_STS_CD().equals("C")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_Scheduling_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_Scheduling_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_Scheduling_aWeekAgo, start_Scheduling_aWeekAgo);
                    end_Scheduling = new Timestamp(end_Intake.getTime()+sf);
                    System.out.println("the end time of Scheduling"+ end_Scheduling /* added to end time of intake*/);

                }

            }

            //Release & Consolidation started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPALoadReleaseProcessing")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAFSPrvConsldtData") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_ReleaseNConsolidation_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_ReleaseNConsolidation_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_ReleaseNConsolidation_aWeekAgo, start_ReleaseNConsolidation_aWeekAgo);
                    end_ReleaseNConsolidation = new Timestamp(end_Scheduling.getTime()+sf);
                    System.out.println("the end time of Release & Consolidation"+ end_ReleaseNConsolidation /* added to end time of Scheduling*/);

                }
            }

            //Payment Processing started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPAPaymentProcessing")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_PaymentProcessing_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_PaymentProcessing_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_PaymentProcessing_aWeekAgo, start_PaymentProcessing_aWeekAgo);
                    end_PaymentProcessing = new Timestamp(end_ReleaseNConsolidation.getTime()+sf);
                    System.out.println("the end time of Payment Processing"+ end_PaymentProcessing /* added to end time of Release n consolidation*/);
                }
            }

            //Post Payment Extract (OTS,TOPS, UCAS ) started but not yet completed
            else if(fb.getBTCH_NM().equals("seqCreateUCASDailyExt") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_PostPaymentExtract_aWeekAgo = l1.get(0).getSTRT_DTTM();
                end_PostPaymentExtract_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_PostPaymentExtract_aWeekAgo, start_PostPaymentExtract_aWeekAgo);
                end_PostPaymentExtract = new Timestamp(end_PaymentProcessing.getTime()+sf);
                System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ end_PostPaymentExtract /* added to end time of Payment Processing*/);
            }

            //835 EPS/B2B started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPA835PostpaymentLoad")){
                frameworkBS fb2 = (frameworkBS) list_1.get(++i);
                if(fb2.getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") && !fb2.getBTCH_STS_CD().equals("C")){
                    System.out.println("On Plan");
                    l1 = aWeekBackObject(fb, session);
                    List<frameworkBS> l2 = aWeekBackObject(fb2, session);
                    start_835EPS_B2B_aWeekAgo = l1.get(0).getSTRT_DTTM();
                    end_835EPS_B2B_aWeekAgo = l2.get(0).getEND_DTTM();
                    long sf = timeDiffCalculate(end_835EPS_B2B_aWeekAgo, start_835EPS_B2B_aWeekAgo);
                    end_835EPS_B2B = new Timestamp(end_PaymentProcessing.getTime()+sf);
                    System.out.println("the end time of 835 EPS/B2B"+ end_835EPS_B2B /* added to end time of Payment Processing*/);
                }
            }

            //EPS Funding File started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPACreateEPSFile") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_EPSFundingFile_aWeekAgo = l1.get(0).getSTRT_DTTM();
                end_EPSFundingFile_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_EPSFundingFile_aWeekAgo, start_EPSFundingFile_aWeekAgo);
                end_EPSFundingFile = new Timestamp(end_835EPS_B2B.getTime()+sf);
                System.out.println("the end time of EPS Funding File"+ end_EPSFundingFile /* added to end time of 835EPS_B2B*/);
            }

            //Funding Report started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPAEPSReport_FS") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_FundingReport_aWeekAgo = l1.get(0).getSTRT_DTTM();
                end_FundingReport_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_FundingReport_aWeekAgo, start_FundingReport_aWeekAgo);
                end_FundingReport = new Timestamp(end_EPSFundingFile.getTime()+sf);
                System.out.println("the end time of Funding Report "+ end_FundingReport /* added to end time of EPS Funding file*/);
            }

            //Provider PRA started but not yet completed
            else if(fb.getBTCH_NM().equals("seqOPAProvPRAFile") && !fb.getBTCH_STS_CD().equals("C")){
                System.out.println("On Plan");
                l1 = aWeekBackObject(fb, session);
                start_ProviderPRA_aWeekAgo = l1.get(0).getSTRT_DTTM();
                end_ProviderPRA_aWeekAgo = l1.get(0).getEND_DTTM();
                long sf = timeDiffCalculate(end_ProviderPRA_aWeekAgo, start_ProviderPRA_aWeekAgo);
                end_ProviderPRA = new Timestamp(end_EPSFundingFile.getTime()+sf);
                System.out.println("the end time of Provider PRA "+ end_ProviderPRA /* added to end time of EPS Funding file*/);
            }


            //all jobs are started and completed
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
