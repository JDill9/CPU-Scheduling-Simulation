package src;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple driver to run the FCFS and RR simulations
 * using the sample from the project input document.
 */
public class Main {
    public static void main(String[] args) {

        List<Process> processes = new ArrayList<>();

        // Example from "Input_for_project#1.pdf":
        // Process ID, Burst Time, Arrival Time, I/O Operations [start, duration]
        // Priority is set to 1 for all here; you can change later.

        processes.add(new Process("P1", 10, 0, 1, 2, 3));
        processes.add(new Process("P2",  6, 2, 1, 1, 2));
        processes.add(new Process("P3",  8, 4, 1, 3, 1));

        // --- FCFS ---
        System.out.println("=== FCFS Simulation ===");
        List<Process> fcfsResult = CPUSchedulingSimulator.simulateFCFS(processes);
        SimulationUtils.printSummary("FCFS", fcfsResult);

        // --- Round Robin (example quantum = 2) ---
        System.out.println("=== Round Robin Simulation (Q = 2) ===");
        List<Process> rrResult = CPUSchedulingSimulator.simulateRoundRobin(processes, 2);
        SimulationUtils.printSummary("RR (Q=2)", rrResult);
    }
}
