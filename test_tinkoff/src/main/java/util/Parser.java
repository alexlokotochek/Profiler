package main.java.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Parser {

    // строка имеет вид:
    // 2015-10-26T16:10:05,005 TRACE [OperationsImpl] entry with (checkAuth:17907)
    // мы хотим получить из неё название метода ("checkAuth")
    // id вызова (17907) и время (2015-10-26T16:10:05,005)

    public static LogInfo parse(String str){
        LogInfo loginfo = new LogInfo();

        String[] strs = str.split(" ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss','SSS");
        loginfo.time = LocalDateTime.parse(strs[0], formatter);

        loginfo.isEntry = (strs[3].equals("entry"));

        int index = strs[5].indexOf(':');
        loginfo.method = strs[5].substring(1, index);
        loginfo.id = Integer.parseInt(strs[5].substring(index + 1, strs[5].length() - 1));

        return loginfo;
    }
}

