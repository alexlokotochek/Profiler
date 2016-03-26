package main.java.scanning;
import main.java.util.LogInfo;
import main.java.util.Parser;
import main.java.util.Parser.LogException;
import main.java.util.Statistics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileScanner {

    static public ArrayList<String> scan (Scanner input) {

        // информация о строчке в логе
        LogInfo loginfo = new LogInfo();

        // в HashMap храним события: (id открытия метода, лог)
        HashMap<Integer, LogInfo> entries = new HashMap<>();

        // доступ по индексу метода. обработает всё за линию,
        // хеш-функция не подведёт благодаря последовательным id
        Statistics stats = new Statistics();

        while (input.hasNextLine()) {

            String line = input.nextLine();

            try {
                loginfo = Parser.fullParse(line);
            } catch (LogException le) {
                le.printStackTrace();
            }

            if (loginfo.isEntry) {
                entries.put(loginfo.id, loginfo);
            } else {
                // добавим метод, если он не встречался
                stats.tryAddMethod(loginfo.method);
                stats.addLog(loginfo, entries.get(loginfo.id).time);
                // был выход из метода, значит забудем,
                // что входили в него
                entries.remove(loginfo.id);
            }
        }

        input.close();

        return stats.getResults();

    }

}