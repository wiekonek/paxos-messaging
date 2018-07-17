package edu.put.paxosstm.messaging.core.utils;

import soa.paxosstm.dstm.internal.TransactionOracle;

public class OracleInitializer {
    public static void initOracle(String oracleType) {
        Class<?> c;
        String className = oracleType;
        if (!oracleType.contains("."))
            className = TransactionOracle.class.getName() + "$" + oracleType;

        TransactionOracle oracle = null;
        try {
            c = Class.forName(className);
            oracle = (TransactionOracle) c.newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle " + className + " does not exist. Quitting");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Oracle " + className + " cannot be instantiated. Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        TransactionOracle.setInstance(oracle);
    }

    public static void initDefferedOracle() {
        TransactionOracle.setInstance(new TransactionOracle.DeferredUpdateOracle());
    }
}
