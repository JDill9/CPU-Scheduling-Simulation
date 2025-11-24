package src;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for the CPU scheduling simulation.
 */
public class SimulationUtils {

    /** Deep copy list of processes */
    public static List<Process> deepCopyProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copyForSimulation());
        }
        return copy;
    }

    /** Print summary table */
    public static void printSummary(String algorithmName, List<Process> processes) {
        System.out.println("=== Results for " + algorithmName + " ===");

        String headerFormat =
                "%-5s %-7s %-7s %-8s %-9s %-10s %-12s %-12s %-12s %-12s%n";
        String rowFormat =
                "%-5s %-7d %-7d %-8d %-9d %-10d %-12d %-12d %-12d %-12d%n";

        System.out.printf(headerFormat,
                "PID", "Arrive", "Burst", "Prio",
                "IOstart", "IOdur",
                "Complete", "Turnaround",
                "Wait(ready)", "Wait(IO)");

        double totalTurnaround = 0;
        double totalWaiting = 0;

        for (Process p : processes) {
            totalTurnaround += p.turnaroundTime;
            totalWaiting += p.readyQueueWaitingTime;

            System.out.printf(rowFormat,
                    p.pid,
                    p.arrivalTime,
                    p.burstTime,
                    p.priority,
                    p.ioStartTime,
                    p.ioDuration,
                    p.completionTime,
                    p.turnaroundTime,
                    p.readyQueueWaitingTime,
                    p.ioWaitingTime);
        }

        int n = processes.size();
        if (n > 0) {
            System.out.printf("%nAverage Turnaround Time: %.2f%n", totalTurnaround / n);
            System.out.printf("Average Waiting Time (ready queue): %.2f%n", totalWaiting / n);
        }

        System.out.println();
    }
}
