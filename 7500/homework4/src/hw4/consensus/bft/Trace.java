package hw4.consensus.bft;

import hw4.net.Id;

import java.util.*;

public class Trace {
    public static final Trace EMPTY = new Trace(Collections.EMPTY_LIST);
    private List<Id> trace;

    public Trace(List<Id> trace) {
        this.trace = new ArrayList(trace);
        if (new HashSet(trace).size() != trace.size())
            throw new RuntimeException("Invalid trace: " + this.trace);
    }

    public static Trace append(Trace trace, Id id) {
        List<Id> ids = new ArrayList(trace.getTrace());
        ids.add(id);
        return new Trace(ids);
    }

    public List<Id> getTrace() {
        return Collections.unmodifiableList(trace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trace trace1 = (Trace) o;
        return Objects.equals(getTrace(), trace1.getTrace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTrace());
    }

    @Override
    public String toString() {
        return "Trace{" + trace + "}";
    }
}
