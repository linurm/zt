// ISTServiceAIDLInterface.aidl
package com.zj.stock;

// Declare any non-default types here with import statements

interface ISTServiceAIDLInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    boolean IsServerRun();
    boolean HaveStock();
    boolean IsServerPause();
    void setDisNum(int n);

}
