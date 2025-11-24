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
    public int cpuTimeExecuted;
    public boolean hasDoneIO;
    public boolean inIO;
    public int ioRemainingTime;
    public int completionTime = -1;
    public int turnaroundTime;
    public int readyQueueWaitingTime;
    public int ioWaitingTime;
    public int firstStartTime = -1;
    public boolean admittedToSystem;

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

        resetDynamicState();
    }

    /**
     * Reset all dynamic fields so the same process can be reused
     * for multiple simulations.
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
     * Create a deep simulation copy of this process.
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
