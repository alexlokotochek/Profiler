package main.java.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Statistics {

    // всё, что связано со временем, хранится в милисекундах
    private final ArrayList<Long> sumTime = new ArrayList<>();
    private final ArrayList<Long> minimums = new ArrayList<>();
    private final ArrayList<Long> maximums = new ArrayList<>();
    private final ArrayList<Integer> callsAmount = new ArrayList<>();
    private final ArrayList<Integer> maximumsId = new ArrayList<>();
    private final Map<String, Integer> methodsIndices = new HashMap<>();

    public void tryAddMethod (String name) {
        if (!methodsIndices.containsKey(name)) {
            // добавим новый метод
            addMethod(name);
        }
    }

    private void addMethod(String name) {
        sumTime.add(0L);
        minimums.add(Long.MAX_VALUE);
        maximums.add(0L);
        callsAmount.add(0);
        maximumsId.add(-1);
        methodsIndices.put(name, methodsIndices.size());
    }

    public void addLog(LogInfo loginfo, LocalDateTime entryTime) {
        LocalDateTime to = loginfo.time;
        Long millis = entryTime.until(to, ChronoUnit.MILLIS);
        Integer thisIndex = methodsIndices.get(loginfo.method);
        sumTime.set(thisIndex, sumTime.get(thisIndex) + millis);

        if (millis < minimums.get(thisIndex)) {
            minimums.set(thisIndex, millis);
        }

        if (millis > maximums.get(thisIndex)) {
            maximums.set(thisIndex, millis);
            maximumsId.set(thisIndex, loginfo.id);
        }

        callsAmount.set(thisIndex, callsAmount.get(thisIndex) + 1);
    }

    public ArrayList<String> getResults() {
        ArrayList<String> results = new ArrayList<>();

        for (String method: methodsIndices.keySet()){

            Integer thisIndex = methodsIndices.get(method);

            // будем выводить тысячные доли секунды, так как часто min < 1.
            // время хранится в Long: milliseconds, делим на 1000 для секунд
            // Locale.US в format, чтобы дробная часть была через точку, а не запятую

            String toPrint = String.format("OperationsImpl:%s ", method);
            toPrint += String.format(Locale.US, "min %.3f, ",
                    (1.* minimums.get(thisIndex))/1e3);
            toPrint += String.format(Locale.US, "max %.3f, ",
                    (1.* maximums.get(thisIndex))/1e3);
            toPrint += String.format(Locale.US, "avg %.3f, ",
                    (1.* sumTime.get(thisIndex)/callsAmount.get(thisIndex))/1e3);
            if (maximumsId.get(thisIndex) != -1) {
                toPrint += String.format(Locale.US, "max id %d, ",
                        maximumsId.get(thisIndex));
            }
            toPrint += String.format(Locale.US, "count %d",
                    callsAmount.get(thisIndex));

            results.add(toPrint);
        }

        return results;
    }

}
