package src;
import java.util.Objects;

/**
 * Represents a single process in the CPU scheduling simulation.
 * Includes both static attributes (arrival, burst, I/O info)
 * and dynamic state used during simulation.
 */
public class Process {

    // ---- Static attributes (input) ----
    public final String pid;
    public final int arrivalTime;
    public final int burstTime;
    public final int priority;
    /**
     * When (in CPU time units) the process requests I/O
     * relative to the start of its CPU execution.
     * Use -1 if the process has no I/O.
     */
    public final int ioStartTime;
    /**
     * How long the I/O takes once it starts.
     */
    public final int ioDuration;

    // ---- Dynamic state (per simulation run) ----
    public int remainingTime;
    public int cpuTimeExecuted;       // total CPU time executed so far
    public boolean hasDoneIO;         // true once I/O has completed
    public boolean inIO;              // currently being serviced by I/O
    public int ioRemainingTime;       // remaining I/O service time
    public int completionTime = -1;   // wall-clock time when finished
    public int turnaroundTime;        // completionTime - arrivalTime
    public int readyQueueWaitingTime; // time spent in ready queue
    public int ioWaitingTime;         // time spent waiting for I/O device
    public int firstStartTime = -1;   // time when first got CPU
    public boolean admittedToSystem;  // has arrived and been enqueued

    public Process(String pid,
                   int burstTime,
                   int arrivalTime,
                   int priority,
                   int ioStartTime,
                   int ioDuration) {
        this.pid = Objects.requireNonNull(pid);
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.ioStartTime = ioStartTime;
        this.ioDuration = ioDuration;

        // initialize dynamic state for first run
        resetDynamicState();
    }

    /**
     * Reset all dynamic fields so the same process object
     * can be reused in another simulation (FCFS, RR, etc.).
     */
    public void resetDynamicState() {
        this.remainingTime = this.burstTime;
        this.cpuTimeExecuted = 0;
        this.hasDoneIO = false;
        this.inIO = false;
        this.ioRemainingTime = 0;
        this.completionTime = -1;
        this.turnaroundTime = 0;
        this.readyQueueWaitingTime = 0;
        this.ioWaitingTime = 0;
        this.firstStartTime = -1;
        this.admittedToSystem = false;
    }

    /**
     * Create a fresh copy with the same static attributes
     * but dynamic state reset, so each algorithm can run
     * with clean process state.
     */
    public Process copyForSimulation() {
        return new Process(this.pid,
                           this.burstTime,
                           this.arrivalTime,
                           this.priority,
                           this.ioStartTime,
                           this.ioDuration);
    }

    @Override
    public String toString() {
        return String.format("Process[%s, arrival=%d, burst=%d, prio=%d, ioStart=%d, ioDur=%d]",
                pid, arrivalTime, burstTime, priority, ioStartTime, ioDuration);
    }
}
