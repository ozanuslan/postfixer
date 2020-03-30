class Time {
    private int time, milisecondCounter, interval;

    Time(int timelimit, int interval) {
        time = timelimit;
        this.interval = interval;
        milisecondCounter = 0;
    }

    void progressTime() {
        if (milisecondCounter >= 1000) {
            milisecondCounter %= 1000;
            time--;
        } else {
            milisecondCounter += interval;
        }
    } 

    int getTime(){
        return time;
    }
}