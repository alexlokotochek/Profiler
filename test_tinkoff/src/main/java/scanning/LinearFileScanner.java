package main.java.scanning;

import main.java.util.LogInfo;
import main.java.util.Parser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LinearFileScanner {

    static public ArrayList<String> scan (String path) {

        Scanner input = FileOpener.open(path);
        if (input == null) {
            return null;
        }

        Integer startIndex = 0;
        LogInfo loginfo = new LogInfo();
        String line;

        while (input.hasNextLine()) {
            line = input.nextLine();
            loginfo = Parser.fullParse(line);

            if (loginfo.isEntry) {
                startIndex = loginfo.id;
                break;
            }
        }

        // будем хранить время входа в метод,
        // id которого равен индексу в этом листе + startIndex
        ArrayList<LocalDateTime> entriesTime = new ArrayList<>();

        ArrayList<Long> sumTime = new ArrayList<>();
        ArrayList<Long> minimums = new ArrayList<>();
        ArrayList<Long> maximums = new ArrayList<>();
        ArrayList<Integer> callsAmount = new ArrayList<>();
        ArrayList<Integer> longestCallsID = new ArrayList<>();

        HashMap<String, Integer> methodsIndex = new HashMap<>();

        Integer methodIndex = 0, thisIndex;

        do {

            // уже считали до первого entry, поэтому do..while,
            // но может быть лог косячный и там один-единственный entry
            if (!input.hasNextLine()) {
                break;
            }

            if (loginfo.isEntry && entriesTime.size() == loginfo.id - startIndex) {
                // добавили вход в метод
                // с индексом loginfo.id - startIndex
                entriesTime.add(loginfo.time);

            } else {

                if (!methodsIndex.containsKey(loginfo.method)) {
                    methodsIndex.put(loginfo.method, methodIndex);
                    sumTime.add(0L);
                    minimums.add(Long.MAX_VALUE);
                    maximums.add(0L);
                    callsAmount.add(0);
                    longestCallsID.add(null);
                    methodIndex++;
                }

                if (entriesTime.get(loginfo.id - startIndex) != null) {
                    LocalDateTime from = entriesTime.get(loginfo.id - startIndex);
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
                    entriesTime.set(loginfo.id - startIndex, null);

                }
            }

            if (loginfo.id == 97991) {
                // если id вышли за пределы AtomicLong,
                // то начнутся с нуля, значит к ним
                // будет прибавляться последний не вышедший
                // за пределы индекс
                startIndex = -(loginfo.id - startIndex + 1);
            }

            line = input.nextLine();
            loginfo = Parser.fullParse(line);

        } while (input.hasNextLine());

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
