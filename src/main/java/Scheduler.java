/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3
 */

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends TimerTask {

    public static void main (final String args[]) {
        TimerTask timerTask = new Scheduler();
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
        System.out.println("TimerTask started");

        //cancel after sometime
        try { Thread.sleep(120000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        timer.cancel();

        System.out.println("TimerTask cancelled");
        try { Thread.sleep(30000); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void run () {
        System.out.println("Timer task started at:"+new Date());
        completeTask();
        System.out.println("Timer task finished at:"+new Date());
    }

    private void completeTask () {
        try {
            //assuming it takes 20 secs to complete the task
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
