package main.java.scanning;

import main.java.util.LogInfo;
import main.java.util.Parser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileScanner {
// выводим в консоль сводку по каждому методу, который попал в лог:
// имя метода,
// минимальное время,
// максимальное время,
// среднее время,
// количество вызовов,
// ID самого долгого вызова

    HashMap<Integer, LogInfo> entries;
    ArrayList<Long> sumTime, maximums, minimums;
    ArrayList<Integer> callsAmount, longestCallsID;
    HashMap<String, Integer> methodsIndex; // метод и его индекс в sumTime

    public void scan (Scanner input) {

        LogInfo loginfo;

        entries = new HashMap<>();

        sumTime = new ArrayList<>();
        minimums = new ArrayList<>();
        maximums = new ArrayList<>();
        callsAmount = new ArrayList<>();
        longestCallsID = new ArrayList<>();

        methodsIndex = new HashMap<>();

        Integer index = 0;
        Integer thisIndex;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            loginfo = Parser.parse(line);

            if (loginfo.isEntry == true && !entries.containsKey(loginfo.id)) {
                entries.put(loginfo.id, loginfo);
            } else {

                if (!methodsIndex.containsKey(loginfo.method)) {
                    methodsIndex.put(loginfo.method, index);
                    sumTime.add(0L);
                    minimums.add(Long.MAX_VALUE);
                    maximums.add(0L);
                    callsAmount.add(0);
                    longestCallsID.add(-1);
                    index++;
                }

                LocalDateTime from = entries.get(loginfo.id).time;
                LocalDateTime to = loginfo.time;
                Long millis = from.until(to, ChronoUnit.MILLIS);
                thisIndex = methodsIndex.get(loginfo.method);
                sumTime.set(thisIndex, sumTime.get(thisIndex) + millis);

                if (millis < minimums.get(thisIndex)) {
                    minimums.set(thisIndex, millis);
                }

                if (millis > maximums.get(thisIndex)) {
                    maximums.set(thisIndex, millis);
                    longestCallsID.set(thisIndex, loginfo.id);
                }

                callsAmount.set(thisIndex, callsAmount.get(thisIndex) + 1);

                entries.remove(loginfo.id);
            }
        }

        input.close();

        for (String method: methodsIndex.keySet()){

            thisIndex = methodsIndex.get(method);

            String toPrint = String.format("method \"%s\":\n", method);
            toPrint += String.format("min: %.3f seconds\n",
                                     (1.*minimums.get(thisIndex))/1e3);
            toPrint += String.format("max: %.3f seconds\n",
                                     (1.*maximums.get(thisIndex))/1e3);
            toPrint += String.format("mean: %.6f seconds\n",
                                     (1.* sumTime.get(thisIndex)/callsAmount.get(thisIndex))/1e3);
            toPrint += String.format("total calls: %d\n",
                                     callsAmount.get(thisIndex));
            toPrint += String.format("longest call ID: %d\n",
                                     longestCallsID.get(thisIndex));
            toPrint += "\n";

            System.out.print(toPrint);

        }

    }

}

