package src;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Contains simulation methods for all CPU scheduling algorithms,
 * including FCFS, SJF, Priority, and Round Robin — all with I/O support.
 */
public class CPUSchedulingSimulator {

    /* ============================
       FCFS (already implemented)
       ============================ */
    public static List<Process> simulateFCFS(List<Process> originalProcesses) {
        List<Process> processes = SimulationUtils.deepCopyProcesses(originalProcesses);

        Queue<Process> readyQueue = new ArrayDeque<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process currentCPU = null;
        Process currentIO = null;

        int completed = 0;
        int time = 0;
        int total = processes.size();

        while (completed < total) {

            // Arrivals
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    p.admittedToSystem = true;
                    readyQueue.add(p);
                }
            }

            // IO start
            if (currentIO == null && !ioQueue.isEmpty()) {
                currentIO = ioQueue.poll();
                currentIO.inIO = true;
            }

            // Waiting time increment
            for (Process p : readyQueue) p.readyQueueWaitingTime++;
            for (Process p : ioQueue) p.ioWaitingTime++;

            // CPU pick — FIFO
            if (currentCPU == null && !readyQueue.isEmpty()) {
                currentCPU = readyQueue.poll();
            }

            boolean movedToIO = false;

            // CPU work
            if (currentCPU != null) {
                if (currentCPU.firstStartTime == -1)
                    currentCPU.firstStartTime = time;

                currentCPU.remainingTime--;
                currentCPU.cpuTimeExecuted++;

                if (!currentCPU.hasDoneIO &&
                    currentCPU.ioStartTime >= 0 &&
                    currentCPU.cpuTimeExecuted == currentCPU.ioStartTime) {

                    currentCPU.ioRemainingTime = currentCPU.ioDuration;
                    currentCPU.inIO = true;
                    ioQueue.add(currentCPU);
                    currentCPU = null;
                    movedToIO = true;
                }
            }

            // IO work
            if (currentIO != null) {
                currentIO.ioRemainingTime--;
                if (currentIO.ioRemainingTime <= 0) {
                    currentIO.inIO = false;
                    currentIO.hasDoneIO = true;
                    readyQueue.add(currentIO);
                    currentIO = null;
                }
            }

            // Completion
            if (!movedToIO && currentCPU != null && currentCPU.remainingTime <= 0) {
                currentCPU.completionTime = time + 1;
                currentCPU.turnaroundTime = currentCPU.completionTime - currentCPU.arrivalTime;
                completed++;
                currentCPU = null;
            }

            time++;
        }

        return processes;
    }

    /* ============================
       SJF (Shortest Job First)
       ============================ */
    public static List<Process> simulateSJF(List<Process> original) {
        List<Process> processes = SimulationUtils.deepCopyProcesses(original);

        List<Process> ready = new ArrayList<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process cpu = null, io = null;
        int completed = 0;
        int time = 0;
        int total = processes.size();

        while (completed < total) {

            // Arrivals
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    p.admittedToSystem = true;
                    ready.add(p);
                }
            }

            // IO start
            if (io == null && !ioQueue.isEmpty()) {
                io = ioQueue.remove();
                io.inIO = true;
            }

            // Waiting time
            for (Process p : ready) p.readyQueueWaitingTime++;
            for (Process p : ioQueue) p.ioWaitingTime++;

            // CPU pick — SHORTEST remaining time
            if (cpu == null && !ready.isEmpty()) {
                cpu = ready.stream()
                        .min(Comparator.comparingInt(p -> p.remainingTime))
                        .get();
                ready.remove(cpu);
            }

            boolean movedToIO = false;

            // CPU work
            if (cpu != null) {
                if (cpu.firstStartTime == -1)
                    cpu.firstStartTime = time;

                cpu.remainingTime--;
                cpu.cpuTimeExecuted++;

                if (!cpu.hasDoneIO &&
                    cpu.ioStartTime >= 0 &&
                    cpu.cpuTimeExecuted == cpu.ioStartTime) {

                    cpu.ioRemainingTime = cpu.ioDuration;
                    cpu.inIO = true;
                    ioQueue.add(cpu);
                    cpu = null;
                    movedToIO = true;
                }
            }

            // IO work
            if (io != null) {
                io.ioRemainingTime--;
                if (io.ioRemainingTime <= 0) {
                    io.inIO = false;
                    io.hasDoneIO = true;
                    ready.add(io);
                    io = null;
                }
            }

            // Completion
            if (!movedToIO && cpu != null && cpu.remainingTime <= 0) {
                cpu.completionTime = time + 1;
                cpu.turnaroundTime = cpu.completionTime - cpu.arrivalTime;
                completed++;
                cpu = null;
            }

            time++;
        }

        return processes;
    }

    /* ============================
       Priority Scheduling
       Lowest priority number = highest priority
       ============================ */
    public static List<Process> simulatePriority(List<Process> original) {
        List<Process> processes = SimulationUtils.deepCopyProcesses(original);

        List<Process> ready = new ArrayList<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process cpu = null, io = null;
        int completed = 0;
        int time = 0;
        int total = processes.size();

        while (completed < total) {

            // Arrivals
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    p.admittedToSystem = true;
                    ready.add(p);
                }
            }

            // IO start
            if (io == null && !ioQueue.isEmpty()) {
                io = ioQueue.remove();
                io.inIO = true;
            }

            // Waiting
            for (Process p : ready) p.readyQueueWaitingTime++;
            for (Process p : ioQueue) p.ioWaitingTime++;

            // CPU pick — Lowest priority number = highest priority
            if (cpu == null && !ready.isEmpty()) {
                cpu = ready.stream()
                        .min(Comparator.comparingInt(p -> p.priority))
                        .get();
                ready.remove(cpu);
            }

            boolean movedToIO = false;

            // CPU exec
            if (cpu != null) {
                if (cpu.firstStartTime == -1)
                    cpu.firstStartTime = time;

                cpu.remainingTime--;
                cpu.cpuTimeExecuted++;

                if (!cpu.hasDoneIO &&
                    cpu.ioStartTime >= 0 &&
                    cpu.cpuTimeExecuted == cpu.ioStartTime) {

                    cpu.ioRemainingTime = cpu.ioDuration;
                    cpu.inIO = true;
                    ioQueue.add(cpu);
                    cpu = null;
                    movedToIO = true;
                }
            }

            // IO exec
            if (io != null) {
                io.ioRemainingTime--;
                if (io.ioRemainingTime <= 0) {
                    io.inIO = false;
                    io.hasDoneIO = true;
                    ready.add(io);
                    io = null;
                }
            }

            // Completion
            if (!movedToIO && cpu != null && cpu.remainingTime <= 0) {
                cpu.completionTime = time + 1;
                cpu.turnaroundTime = cpu.completionTime - cpu.arrivalTime;
                completed++;
                cpu = null;
            }

            time++;
        }

        return processes;
    }

    /* ============================
       Round Robin
       ============================ */
    public static List<Process> simulateRoundRobin(List<Process> original, int quantum) {
        if (quantum <= 0) throw new IllegalArgumentException("Quantum must be > 0");

        List<Process> processes = SimulationUtils.deepCopyProcesses(original);

        Queue<Process> ready = new ArrayDeque<>();
        Queue<Process> ioQueue = new ArrayDeque<>();

        Process cpu = null, io = null;
        int time = 0, completed = 0;
        int total = processes.size();
        int quantumRemaining = 0;

        while (completed < total) {

            // Arrivals
            for (Process p : processes) {
                if (!p.admittedToSystem && p.arrivalTime == time) {
                    p.admittedToSystem = true;
                    ready.add(p);
                }
            }

            // IO start
            if (io == null && !ioQueue.isEmpty()) {
                io = ioQueue.remove();
                io.inIO = true;
            }

            // Waiting
            for (Process p : ready) p.readyQueueWaitingTime++;
            for (Process p : ioQueue) p.ioWaitingTime++;

            // CPU pick
            if (cpu == null && !ready.isEmpty()) {
                cpu = ready.remove();
                quantumRemaining = quantum;
            }

            boolean movedToIO = false;

            // CPU exec
            if (cpu != null) {
                if (cpu.firstStartTime == -1)
                    cpu.firstStartTime = time;

                cpu.remainingTime--;
                cpu.cpuTimeExecuted++;
                quantumRemaining--;

                if (!cpu.hasDoneIO &&
                    cpu.ioStartTime >= 0 &&
                    cpu.cpuTimeExecuted == cpu.ioStartTime) {

                    cpu.ioRemainingTime = cpu.ioDuration;
                    cpu.inIO = true;
                    ioQueue.add(cpu);
                    cpu = null;
                    movedToIO = true;
                }
            }

            // IO exec
            if (io != null) {
                io.ioRemainingTime--;
                if (io.ioRemainingTime <= 0) {
                    io.inIO = false;
                    io.hasDoneIO = true;
                    ready.add(io);
                    io = null;
                }
            }

            // Completion or preempt
            if (!movedToIO && cpu != null) {
                if (cpu.remainingTime <= 0) {
                    cpu.completionTime = time + 1;
                    cpu.turnaroundTime = cpu.completionTime - cpu.arrivalTime;
                    completed++;
                    cpu = null;
                } else if (quantumRemaining <= 0) {
                    ready.add(cpu);
                    cpu = null;
                }
            }

            time++;
        }

        return processes;
    }
}
