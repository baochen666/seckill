package com.example.seckill.util;

import org.springframework.util.StringUtils;

public class ValidatorUtil {

    public static boolean isValidPhoneNumbe(String phoneNumber){
        if (StringUtils.isEmpty(phoneNumber)){
            return false;
        }
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }
}
