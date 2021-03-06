package main.java.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {

    // строка имеет вид:
    // 2015-10-26T16:10:05,005 TRACE [OperationsImpl] entry with (checkAuth:17907)
    // мы хотим получить из неё название метода ("checkAuth")
    // id вызова (17907) и время (2015-10-26T16:10:05,005)

    // построим автомат для регулярного выражения один раз
    private static final String regex =
            "^\\d\\d\\d\\d-\\d\\d-\\d\\d" +
            "T\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d\\s" +
            "TRACE\\s\\[OperationsImpl\\]\\s" +
            "(entry|exit)\\s" +
            "with\\s\\(\\w+:\\d+\\)$";
    private static final Pattern pattern = Pattern.compile(regex);


    public static LogInfo fullParse(String log) throws LogException {

        Matcher matcher = pattern.matcher(log);
        if (!matcher.matches()) {
            throw new LogException(log);
        }

        LogInfo loginfo = new LogInfo();

        String[] splittedStr = log.split(" ");
        String datePattern = "yyyy-MM-dd'T'HH:mm:ss','SSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        loginfo.time = LocalDateTime.parse(splittedStr[0], formatter);

        loginfo.isEntry = (splittedStr[3].equals("entry"));

        Integer begin = splittedStr[5].indexOf(':');
        Integer end = splittedStr[5].length() - 1;
        loginfo.method = splittedStr[5].substring(1, begin);
        String IdStr = splittedStr[5].substring(begin + 1, end);
        loginfo.id = Integer.parseInt(IdStr);

        return loginfo;

    }

    public static class LogException extends Exception {

        public LogException(String log) {
            super("Wrong log entry:" + log);
        }

    }


}

