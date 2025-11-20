package src;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for the CPU scheduling simulation:
 *  - deep-copy processes for each algorithm
 *  - print summary tables and averages
 */
public class SimulationUtils {

    /**
     * Create a deep copy list of processes with reset dynamic state.
     */
    public static List<Process> deepCopyProcesses(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copyForSimulation());
        }
        return copy;
    }

    /**
     * Print a summary table for the given algorithm and its processes.
     * Waiting time is taken as total time in the ready queue.
     */
    public static void printSummary(String algorithmName, List<Process> processes) {
        System.out.println("=== Results for " + algorithmName + " ===");

        String headerFormat =
                "%-5s %-7s %-7s %-8s %-9s %-10s %-12s %-12s %-12s %-12s%n";
        String rowFormat =
                "%-5s %-7d %-7d %-8d %-9d %-10d %-12d %-12d %-12d %-12d%n";

        System.out.printf(headerFormat,
                "PID",
                "Arrive",
                "Burst",
                "Prio",
                "IOstart",
                "IOdur",
                "Complete",
                "Turnaround",
                "Wait(ready)",
                "Wait(IO)");

        double totalTurnaround = 0.0;
        double totalWaiting = 0.0;

        for (Process p : processes) {
            int waitingTime = p.readyQueueWaitingTime;
            int turnaround = p.turnaroundTime;

            totalTurnaround += turnaround;
            totalWaiting += waitingTime;

            System.out.printf(rowFormat,
                    p.pid,
                    p.arrivalTime,
                    p.burstTime,
                    p.priority,
                    p.ioStartTime,
                    p.ioDuration,
                    p.completionTime,
                    turnaround,
                    waitingTime,
                    p.ioWaitingTime);
        }

        int n = processes.size();
        if (n > 0) {
            double avgT = totalTurnaround / n;
            double avgW = totalWaiting / n;
            System.out.printf("%nAverage Turnaround Time: %.2f%n", avgT);
            System.out.printf("Average Waiting Time (ready queue): %.2f%n", avgW);
        }

        System.out.println();
    }
}
