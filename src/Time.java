class Time {
    private int time, milisecondCounter, interval;

    Time(int timelimit, int interval) {
        time = timelimit;
        this.interval = interval;
        milisecondCounter = 0;
    }

    public void progressTime() {
        if (milisecondCounter >= 1000) {
            milisecondCounter %= 1000;
            time--;
        } else {
            milisecondCounter += interval;
        }
    } 

    public int getTime(){
        return time;
    }
}