package main.java.scanning;

import main.java.util.LogInfo;
import main.java.util.Parser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileScanner {

    HashMap<Integer, LogInfo> entries;

    ArrayList<Long> timers;
    HashMap<String, Integer> methodsIndex; // метод и его индекс в timers


    public void scan (Scanner input) {

        LogInfo loginfo;
        entries = new HashMap<>();
        timers = new ArrayList<>();
        methodsIndex = new HashMap<>();

        Integer index = 0;
        Integer thisIndex;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            loginfo = Parser.parse(line);
            if (line == null) {
                break;
            }

            if (loginfo.isEntry == true && !entries.containsKey(loginfo.id)) {
                entries.put(loginfo.id, loginfo);
            } else {

                if (!methodsIndex.containsKey(loginfo.method)) {
                    methodsIndex.put(loginfo.method, index);
                    timers.add(0L);
                    index++;
                }

                LocalDateTime from = entries.get(loginfo.id).time;
                LocalDateTime to = loginfo.time;
                Long millis = from.until(to, ChronoUnit.MILLIS);
                thisIndex = methodsIndex.get(loginfo.method);
                timers.set(thisIndex, timers.get(thisIndex) + millis);
                entries.remove(loginfo.id);
            }
        }

        input.close();



        for (String method: methodsIndex.keySet()){
            thisIndex = methodsIndex.get(method);
            if (timers.get(thisIndex) == 0L){
                continue;
            }
            String key = method.toString();
            Long value = timers.get(thisIndex);
            System.out.println(key + ": " + value/1e3 + " seconds");
        }

    }




}

