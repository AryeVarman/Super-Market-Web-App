package engine.src.SDMEngine;

import com.sun.corba.se.impl.io.TypeMismatchException;

public class Utils {

    public static boolean tryParseStringToDouble(String str) {
        try{
            double num = Double.parseDouble((str));
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static boolean tryParseStringToInt(String str) {
        try{
            int num = Integer.parseInt((str));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPositiveDouble(String str) {
        if(tryParseStringToDouble(str)) {
            if (Double.parseDouble(str) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotNegativeDouble(String str) {
        if(tryParseStringToDouble(str)) {
            if (Double.parseDouble(str) >= 0) {
                return true;
            }
        }
        return false;
    }


}
