package dev;

public class Timer {
    public static boolean enabled = false;

    private final double startTime;
    private final String name;
    public Timer(final String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }
    @SuppressWarnings("preview")
    public void end() {
        if(!enabled) return;
        System.out.println(STR."\{name} took \{(System.nanoTime() - startTime) / 1000000}ms.");
    }
}
