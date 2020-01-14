package org.hibernateTest.Practice;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainClass {
    private static SessionFactory sessionFactory = null;

    public static void main(String[] args)  {

        String current_date = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date());

//        TestData4 testData = new TestData4();
        UNETProviderETA unetProviderETA = new UNETProviderETA();
        ExcelComparison excelComparison = new ExcelComparison();
        sessionFactory = buildSessionFactory();
        excelComparison.updateExcelSheet(current_date);
        UNETMemberETA unetMemberETA= new UNETMemberETA();
        unetProviderETA.getQuery(current_date, sessionFactory);
        unetMemberETA.getQuery(current_date, sessionFactory);

//        testData.getQuery(current_date, sessionFactory);
//        String current_date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
//        excelComparison.getQuery("2020-01-08", sessionFactory);
//        excelComparison.updateCodeCalculatedETA();


    }

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


    private void shutdown() {
        getSessionFactory().close();
    }

}
