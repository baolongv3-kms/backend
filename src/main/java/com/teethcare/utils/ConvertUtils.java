package com.teethcare.utils;

import com.teethcare.exception.BadRequestException;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ConvertUtils {
    private ConvertUtils(){};
    public static int covertID(String inputId){
        int theID = 0;
        if(!NumberUtils.isCreatable(inputId)){
            throw new BadRequestException("Id " + inputId + " invalid");
        }
        return Integer.parseInt(inputId);
    }
    public static Timestamp getTimestamp(long timestampInString) {
            Date date = new Date(timestampInString);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formatted = format.format(date);
            Timestamp timeStamp = Timestamp.valueOf(formatted);
            return timeStamp;
    }
}