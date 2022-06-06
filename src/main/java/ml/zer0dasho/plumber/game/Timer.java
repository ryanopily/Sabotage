package ml.zer0dasho.plumber.game;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import ml.zer0dasho.plumber.utils.Trycat;

/**
 * Timer for use in BukkitRunnable, makes use of ticks.
 * Every call to the tick() method represents one tick.
 * 
 * @author 0-o#9646
 */
public class Timer {
	
	public Runnable onFinish;
	public int totalTicks, elapsedTicks;
	
	private boolean finished;
	private DateTimeFormatter format;
	
	public final Supplier<String> time;
	
	public Timer(String pattern, int hours, int minutes, int seconds) {
		this(pattern, (hours * 3600) + (minutes * 60) + seconds);
	}
	
	public Timer(String pattern, int ticks) {
		this.totalTicks = ticks;
    	this.format = Trycat.Get(() -> DateTimeFormatter.ofPattern(pattern), TIMER_FORMAT);
    	
    	time = () -> {
    		int ticksLeft = totalTicks - elapsedTicks;
		    return format.format(LocalTime.of(ticksLeft/3600, ticksLeft/60, ticksLeft%60));
    	};
	}
	
	/**
	 * Resets the timer to it's default state.
	 */
	public void reset() {
		this.finished = false;
		this.elapsedTicks = 0;
	}
	
	/**
	 * Ticks the timer once. Returns true only if the timer is done ticking.
	 * @return boolean
	 */
	public boolean tick() {
		boolean finished = (totalTicks <= ++elapsedTicks);
		
		if(!this.finished && finished && onFinish != null)
			onFinish.run();
		
		return (this.finished = finished);
	}
	
	/* Getters */
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public DateTimeFormatter getFormat() {
		return this.format;
	}
	
	public void setFormat(DateTimeFormatter format) {
		if(format != null)
			this.format = format;
	}
	
	public static final DateTimeFormatter TIMER_FORMAT = DateTimeFormatter.ofPattern("m:ss");
}