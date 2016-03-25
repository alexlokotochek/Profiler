package main.java.scanning;

import main.java.util.LogInfo;
import main.java.util.Parser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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

        // в HashMap храним события: (id открытия метода, лог)
        HashMap<Integer, LogInfo> entries = new HashMap<>();

        // статистики, доступ по индексу метода
        // всё, что связано со временем, хранится в милисекундах
        ArrayList<Long> sumTime = new ArrayList<>();
        ArrayList<Long> minimums = new ArrayList<>();
        ArrayList<Long> maximums = new ArrayList<>();
        ArrayList<Integer> callsAmount = new ArrayList<>();
        ArrayList<Integer> longestCallsID = new ArrayList<>();

        // индекс метода назначается при появлении ранее неизвестных
        HashMap<String, Integer> methodsIndex = new HashMap<>();
        // текущий счётчик количества разных методов
        Integer index = 0;

        while (input.hasNextLine()) {

            String line = input.nextLine();
            loginfo = Parser.fullParse(line);

            if (loginfo.isEntry && !entries.containsKey(loginfo.id)) {

                // произошел entry в какой-то метод
                // и он не косячный (не два входа подряд)
                entries.put(loginfo.id, loginfo);

            } else {

                // встретили выход из метода

                if (!methodsIndex.containsKey(loginfo.method)) {
                    // добавим новый метод и инициализируем
                    // статистики для него
                    methodsIndex.put(loginfo.method, index);
                    sumTime.add(0L);
                    minimums.add(Long.MAX_VALUE);
                    maximums.add(0L);
                    callsAmount.add(0);
                    longestCallsID.add(-1);
                    index++;
                }

                // узнаем, когда входили в этот метод
                // и посчитаем статистики

                LocalDateTime from = entries.get(loginfo.id).time;
                LocalDateTime to = loginfo.time;
                Long millis = from.until(to, ChronoUnit.MILLIS);
                Integer thisIndex = methodsIndex.get(loginfo.method);
                sumTime.set(thisIndex, sumTime.get(thisIndex) + millis);

                if (millis < minimums.get(thisIndex)) {
                    minimums.set(thisIndex, millis);
                }

                if (millis > maximums.get(thisIndex)) {
                    maximums.set(thisIndex, millis);
                    longestCallsID.set(thisIndex, loginfo.id);
                }

                callsAmount.set(thisIndex, callsAmount.get(thisIndex) + 1);

                // был выход из метода, значит забудем,
                // что входили в него
                entries.remove(loginfo.id);

            }
        }

        input.close();

        // аккумулируем результаты всех методов
        ArrayList<String> result = new ArrayList<>();

        for (String method: methodsIndex.keySet()){

            Integer thisIndex = methodsIndex.get(method);
            // Формат вывода:
            // OperationsImpl:getData min 123, max 846, avg 315, max id 22, count 333

            // будем выводить тысячные доли секунды, так как часто min < 1.
            // время хранится в Long: milliseconds, так что делим их на 1000 для секунд
            // Locale.US в format, чтобы дробная часть была через точку, а не запятую

            String toPrint = String.format("OperationsImpl:%s ", method);

            toPrint += String.format(Locale.US, "min %.3f, ",
                                     (1.* minimums.get(thisIndex))/1e3);
            toPrint += String.format(Locale.US, "max %.3f, ",
                                     (1.* maximums.get(thisIndex))/1e3);
            toPrint += String.format(Locale.US, "avg %.3f, ",
                                     (1.* sumTime.get(thisIndex)/ callsAmount.get(thisIndex))/1e3);
            toPrint += String.format(Locale.US, "max id %d, ",
                                     longestCallsID.get(thisIndex));
            toPrint += String.format(Locale.US, "count %d",
                                     callsAmount.get(thisIndex));

            result.add(toPrint);

        }

        return result;

    }

}

