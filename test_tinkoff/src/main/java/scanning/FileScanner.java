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

    static public ArrayList<String> scan (String path) {

        Scanner input = FileOpener.open(path);
        if (input == null) {
            return null;
        }

        LogInfo loginfo;

        HashMap<Integer, LogInfo> entries = new HashMap<>();

        ArrayList<Long> sumTime = new ArrayList<>();
        ArrayList<Long> minimums = new ArrayList<>();
        ArrayList<Long> maximums = new ArrayList<>();
        ArrayList<Integer> callsAmount = new ArrayList<>();
        ArrayList<Integer> longestCallsID = new ArrayList<>();

        HashMap<String, Integer> methodsIndex = new HashMap<>();

        Integer index = 0;
        Integer thisIndex;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            loginfo = Parser.fullParse(line);

            if (loginfo.isEntry && !entries.containsKey(loginfo.id)) {
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

        ArrayList<String> result = new ArrayList<>();

        for (String method: methodsIndex.keySet()){
            thisIndex = methodsIndex.get(method);
            // Формат вывода:
            // OperationsImpl:getData min 123, max 846, avg 315, max id 22, count 333
            // будем выводить тысячные доли секунды, так как часто min < 1
            // в качестве разделителя возьмём ';', потому что дробная часть после ','
            // время хранится в Long: milliseconds, так что делим их на 1000 для секунд
            String toPrint = String.format("OperationsImpl:%s ", method);
            toPrint += String.format("min %.3f; ",
                                     (1.* minimums.get(thisIndex))/1e3);
            toPrint += String.format("max %.3f; ",
                                     (1.* maximums.get(thisIndex))/1e3);
            toPrint += String.format("avg %.3f; ",
                                     (1.* sumTime.get(thisIndex)/ callsAmount.get(thisIndex))/1e3);
            toPrint += String.format("max id %d; ",
                                     longestCallsID.get(thisIndex));
            toPrint += String.format("count %d",
                                     callsAmount.get(thisIndex));
            result.add(toPrint);
        }
        return result;
    }

}

