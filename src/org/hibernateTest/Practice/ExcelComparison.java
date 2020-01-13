package org.hibernateTest.Practice;

import org.Test.modelClass.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelComparison {

    private static XSSFWorkbook workbook;
    public static final String file_path = "C:\\Users\\spande67\\Documents\\ETAComparisonSheet.xlsx";
    private static XSSFSheet xssfSheet;
    private Row row;
    private Cell cell;
    private static int rowindex;



    public void getQuery(String curr_date, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for UNET-PROVIDER
            List list_1 = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "to_char(CREAT_DTTM, 'yyyy-MM-dd') = '"+ curr_date+"' order by CREAT_DTTM").list();
            System.out.println("transaction_1 for UNET-PROVIDER started");
            printTimeDiff(list_1);

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

    public String getETAinString(Timestamp tm){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YY hh:mm a");
        String df= simpleDateFormat.format(tm); //adding 30 mins to extend the ETA
        return df +" CST";
    }

    public String addExtraMargin(Timestamp tm , long extra){
        return getETAinString(new Timestamp(tm.getTime()+extra));
    }

    public void printTimeDiff(List<frameworkBS> list){


        if(list.size()==0){
            System.out.println("Files not loaded yet! unable to calculate ETA!");
//            logger.warn("Files not loaded yet! unable to calculate ETA!");
            return;
        }

        else if(list.size()<19){
            System.out.println("THE UNET Provider is not yet completed for the day " +new SimpleDateFormat("MM/dd/YY hh:mm a").format(new Timestamp(System.currentTimeMillis())));
            return;
        }
        Timestamp endTime;
        String endTm;
        rowindex = xssfSheet.getLastRowNum()-10;
        for (int i =0; i<list.size(); i++){
            int cellindex = 1;
            if(list.get(i).getBTCH_NM().equals("seqLoad835DbPrePr")){
                endTime = list.get(i).getEND_DTTM();
                System.out.println(TestData4.getEnd_PrePreProcessor());
                endTm = addExtraMargin(TestData4.getEnd_PrePreProcessor(), 1800000);
                xssfSheet.getRow(rowindex).createCell(cellindex++).setCellValue(endTm);
                xssfSheet.getRow(rowindex++).createCell(++cellindex).setCellValue(endTime);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc")){
                endTime = list.get(i).getEND_DTTM();
                endTm = addExtraMargin(TestData4.getEnd_PreProcessor(), 1800000);
                xssfSheet.getRow(rowindex).createCell(cellindex++).setCellValue(endTm);
                xssfSheet.getRow(rowindex++).createCell(++cellindex).setCellValue(endTime);
            }

        }
        writeWorkbook(new File(file_path));


    }

    public void updateFinalETA(){

        rowindex = xssfSheet.getLastRowNum()-10;
        int cellnum =3;
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_PrePreProcessor()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_PreProcessor()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_Intake()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_Scheduling()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_ReleaseNConsolidation()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_PaymentProcessing()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_835EPS_B2B()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_EPSFundingFile()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_FundingReport()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_ProviderPRA()));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(getETAinString(UNETProviderETA.getEnd_PostPaymentExtract()));
        writeWorkbook(new File(file_path));

    }

    public void updateCodeCalculatedETA(){

        rowindex = xssfSheet.getLastRowNum()-10;
        int cellnum =1;
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_PrePreProcessor(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_PreProcessor(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_Intake(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_Scheduling(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_ReleaseNConsolidation(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_PaymentProcessing(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_EPSFundingFile(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_FundingReport(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_ProviderPRA(), 1800000));
        xssfSheet.getRow(rowindex++).createCell(cellnum).setCellValue(addExtraMargin(UNETProviderETA.getEnd_PostPaymentExtract(), 1800000));
        writeWorkbook(new File(file_path));

    }

    public void updateExcelSheet(String curr_date){

        File file = new File(file_path);
        Row row;

        if(!file.exists()){
            workbook = new XSSFWorkbook();
        }
        else {
            try{
                FileInputStream fileInputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(fileInputStream);
                fileInputStream.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (workbook.getNumberOfSheets()!=0){
            xssfSheet = workbook.getSheet("ETA Evaluation sheet");
            rowindex = xssfSheet.getLastRowNum();
        }
        else {
            xssfSheet = workbook.createSheet("ETA Evaluation sheet");
            rowindex =0;
        }

        if(xssfSheet.getRow(rowindex).getCell(3).getStringCellValue().isEmpty()){
            return;
        }
        else{
            rowindex+=2;
            row = xssfSheet.createRow(rowindex++);
            int cellnum =0;
            row.createCell(cellnum++).setCellValue("DATE: " +curr_date);
            row.createCell(cellnum++).setCellValue("Code generated ETA");
            row.createCell(cellnum++).setCellValue("Manually generated ETA");
            row.createCell(cellnum).setCellValue("Actual time of completion");
            cellnum =0;

            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("EPS Enrollment");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Pre-Pre Processor");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Pre-Processor");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Intake");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Scheduling");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Release & Consolidation");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Payment Processing");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("835 EPS/B2B");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("EPS Funding File");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Funding Report");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex++).createCell(cellnum).setCellValue("Provider PRA");
            xssfSheet.createRow(rowindex).createCell(3);
            xssfSheet.createRow(rowindex).createCell(cellnum).setCellValue("Post Payment Extract(OTS,TOPS, UCAS)");
            xssfSheet.createRow(rowindex).createCell(3);

            writeWorkbook(file);

        }


    }

    void writeWorkbook(File file){

        try {
            FileOutputStream fileOutputStream  = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            System.out.println("file successfully written to : "+ file.getName());

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();

        }
    }

}
