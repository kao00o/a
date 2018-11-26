package cn.midn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputUtil {
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));
    private InputUtil(){}

    public static String getString(String prompt){
        String returnData = null;
        boolean flag = true;
        while (flag){
            System.out.println(prompt);
            try {
                returnData = KEYBOARD_INPUT.readLine();
                if (returnData == null || "".equals(returnData)){
                    System.err.println(" 输入了 null");
                }else {
                    flag =false;
                }

            } catch (IOException e) {
                System.err.println(" 输入了 错误数据");
            }
        }
        return returnData;
    }
}
