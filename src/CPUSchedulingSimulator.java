package src;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Contains simulation methods for different CPU scheduling algorithms.
 * For now: FCFS and Round Robin (with I/O simulation).
 */
public class CPUSchedulingSimulator {

    /**
     * Simulate First-Come First-Served (FCFS) scheduling with I/O.
     * Returns a list of processes with all metrics filled in.
     */
    public static List<Process> simulateFCFS(List<Process> originalProcesses) {
        List<Process> processes = SimulationUtils.deepCopyProcesses(originalProcesses);

        Queue<Process> readyQueue = new ArrayDeque<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process currentCPU = null;
        Process currentIO = null;

        int completed = 0;
        int time = 0;
        int totalProcesses = processes.size();

        while (completed < totalProcesses) {
            // 1) Admit newly arrived processes at this time
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    readyQueue.add(p);
                    p.admittedToSystem = true;
                }
            }

            // 2) Start I/O if device is idle and there is a waiting process
            if (currentIO == null && !ioQueue.isEmpty()) {
                currentIO = ioQueue.remove();
                currentIO.inIO = true;
            }

            // 3) Increment waiting times
            for (Process p : readyQueue) {
                p.readyQueueWaitingTime++;
            }
            for (Process p : ioQueue) {
                p.ioWaitingTime++;
            }

            // 4) CPU scheduling decision (FCFS: simple FIFO)
            if (currentCPU == null && !readyQueue.isEmpty()) {
                currentCPU = readyQueue.remove();
            }

            boolean movedToIOThisTick = false;

            // 5) Execute CPU for one time unit
            if (currentCPU != null) {
                if (currentCPU.firstStartTime == -1) {
                    currentCPU.firstStartTime = time;
                }

                currentCPU.remainingTime--;
                currentCPU.cpuTimeExecuted++;

                // Check if this process needs to go to I/O now
                if (!currentCPU.hasDoneIO
                        && currentCPU.ioStartTime >= 0
                        && currentCPU.cpuTimeExecuted == currentCPU.ioStartTime) {

                    currentCPU.ioRemainingTime = currentCPU.ioDuration;
                    currentCPU.inIO = true;
                    ioQueue.add(currentCPU);
                    currentCPU = null;
                    movedToIOThisTick = true;
                }
            }

            // 6) Execute I/O for one time unit (single device)
            if (currentIO != null) {
                currentIO.ioRemainingTime--;
                if (currentIO.ioRemainingTime <= 0) {
                    currentIO.inIO = false;
                    currentIO.hasDoneIO = true;
                    // Once I/O finishes, process goes back to ready queue
                    readyQueue.add(currentIO);
                    currentIO = null;
                }
            }

            // 7) Check for CPU completion (if not moved to I/O)
            if (!movedToIOThisTick && currentCPU != null && currentCPU.remainingTime <= 0) {
                currentCPU.completionTime = time + 1; // finishing at end of this tick
                currentCPU.turnaroundTime = currentCPU.completionTime - currentCPU.arrivalTime;
                completed++;
                currentCPU = null;
            }

            // Optional debug print (commented out)
            // debugState(time, "FCFS", currentCPU, currentIO, readyQueue, ioQueue);

            time++;
        }

        return processes;
    }

    /**
     * Simulate Round Robin scheduling with a given time quantum, including I/O.
     */
    public static List<Process> simulateRoundRobin(List<Process> originalProcesses, int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be > 0");
        }

        List<Process> processes = SimulationUtils.deepCopyProcesses(originalProcesses);

        Queue<Process> readyQueue = new ArrayDeque<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process currentCPU = null;
        Process currentIO = null;

        int completed = 0;
        int time = 0;
        int totalProcesses = processes.size();
        int quantumRemaining = 0;

        while (completed < totalProcesses) {
            // 1) Admit newly arrived processes
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    readyQueue.add(p);
                    p.admittedToSystem = true;
                }
            }

            // 2) Start I/O if device idle
            if (currentIO == null && !ioQueue.isEmpty()) {
                currentIO = ioQueue.remove();
                currentIO.inIO = true;
            }

            // 3) Increment waiting times
            for (Process p : readyQueue) {
                p.readyQueueWaitingTime++;
            }
            for (Process p : ioQueue) {
                p.ioWaitingTime++;
            }

            // 4) CPU scheduling decision (RR)
            if (currentCPU == null && !readyQueue.isEmpty()) {
                currentCPU = readyQueue.remove();
                quantumRemaining = quantum;
            }

            boolean movedToIOThisTick = false;

            // 5) Execute CPU for one time unit
            if (currentCPU != null) {
                if (currentCPU.firstStartTime == -1) {
                    currentCPU.firstStartTime = time;
                }

                currentCPU.remainingTime--;
                currentCPU.cpuTimeExecuted++;
                quantumRemaining--;

                // Check for I/O request
                if (!currentCPU.hasDoneIO
                        && currentCPU.ioStartTime >= 0
                        && currentCPU.cpuTimeExecuted == currentCPU.ioStartTime) {

                    currentCPU.ioRemainingTime = currentCPU.ioDuration;
                    currentCPU.inIO = true;
                    ioQueue.add(currentCPU);
                    currentCPU = null;
                    movedToIOThisTick = true;
                }
            }

            // 6) Execute I/O for one time unit
            if (currentIO != null) {
                currentIO.ioRemainingTime--;
                if (currentIO.ioRemainingTime <= 0) {
                    currentIO.inIO = false;
                    currentIO.hasDoneIO = true;
                    readyQueue.add(currentIO);
                    currentIO = null;
                }
            }

            // 7) Handle completion or time-slice expiration (if not moved to I/O)
            if (!movedToIOThisTick && currentCPU != null) {
                if (currentCPU.remainingTime <= 0) {
                    currentCPU.completionTime = time + 1;
                    currentCPU.turnaroundTime = currentCPU.completionTime - currentCPU.arrivalTime;
                    completed++;
                    currentCPU = null;
                } else if (quantumRemaining <= 0) {
                    // Time slice expired; preempt and re-queue
                    readyQueue.add(currentCPU);
                    currentCPU = null;
                }
            }

            // Optional debug print (commented)
            // debugState(time, "RR", currentCPU, currentIO, readyQueue, ioQueue);

            time++;
        }

        return processes;
    }

    // You can enable this for step-by-step debugging if you want.
    @SuppressWarnings("unused")
    private static void debugState(int time,
                                   String algo,
                                   Process currentCPU,
                                   Process currentIO,
                                   Queue<Process> ready,
                                   Queue<Process> ioQ) {
        System.out.printf("t=%d [%s] CPU=%s IO=%s Ready=%s IOqueue=%s%n",
                time,
                algo,
                (currentCPU == null ? "-" : currentCPU.pid),
                (currentIO == null ? "-" : currentIO.pid),
                queueToString(ready),
                queueToString(ioQ));
    }

    private static String queueToString(Queue<Process> q) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Process p : q) {
            if (!first) sb.append(", ");
            sb.append(p.pid);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
