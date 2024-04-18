package dev;

public class Timer {
    private final double startTime;
    private final String name;
    public Timer(final String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }
    @SuppressWarnings("preview")
    public void end() {
        System.out.println(STR."\{name} took \{(System.nanoTime() - startTime) / 1000000}ms.");
    }
}
