package src;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple driver to run the FCFS, SJF, Priority, and RR simulations
 * using the sample from the project input document.
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("MAIN SOURCE ACTIVE");
        System.out.println(">>> USING RANDOM GENERATOR <<<");

        //List<Process> processes = new ArrayList<>();
        // Example from "Input_for_project#1.pdf":
        //processes.add(new Process("P1", 10, 0, 1, 2, 3));
        //processes.add(new Process("P2",  6, 2, 3, 1, 2));
        //processes.add(new Process("P3",  8, 4, 2, 3, 1));

        // === AUTO-GENERATE ≥20 RANDOM PROCESSES ===
        List<Process> processes = ProcessGenerator.generateProcesses(20);

        System.out.println("=== GENERATED PROCESSES ===");
        for (Process p : processes) {
        System.out.printf("%s  arrival=%d  burst=%d  priority=%d  ioStart=%d  ioDur=%d%n",
        p.pid, p.arrivalTime, p.burstTime, p.priority, p.ioStartTime, p.ioDuration);
        }
        System.out.println();
       

        // --- FCFS ---
        System.out.println("=== FCFS Simulation ===");
        List<Process> fcfsResult = CPUSchedulingSimulator.simulateFCFS(processes);
        SimulationUtils.printSummary("FCFS", fcfsResult);

        // --- SJF ---
        System.out.println("=== SJF Simulation ===");
        List<Process> sjfResult = CPUSchedulingSimulator.simulateSJF(processes);
        SimulationUtils.printSummary("SJF", sjfResult);

        // --- Priority ---
        System.out.println("=== Priority Simulation ===");
        List<Process> prioResult = CPUSchedulingSimulator.simulatePriority(processes);
        SimulationUtils.printSummary("Priority", prioResult);

        // --- Round Robin ---
        System.out.println("=== Round Robin Simulation (Q = 2) ===");
        List<Process> rrResult = CPUSchedulingSimulator.simulateRoundRobin(processes, 2);
        SimulationUtils.printSummary("RR (Q=2)", rrResult);
    }
}
