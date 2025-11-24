package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProcessGenerator {

    public static List<Process> generateProcesses(int n) {
        Random rand = new Random();
        List<Process> processes = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            String pid = "P" + i;

            int arrival = rand.nextInt(10);     
            int burst = rand.nextInt(8) + 3;    
            int priority = rand.nextInt(5) + 1; 

            boolean hasIO = rand.nextBoolean();

            int ioStart = hasIO ? rand.nextInt(Math.max(1, burst - 1)) : -1;
            int ioDuration = hasIO ? (rand.nextInt(3) + 1) : 0;

            processes.add(new Process(pid, burst, arrival, priority, ioStart, ioDuration));
        }

        return processes;
    }
}
