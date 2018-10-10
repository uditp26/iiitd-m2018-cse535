package com.quizup.app;

public interface Communicator {
    public void reactor(int pos);
    public void updator(int pos, Boolean value);
    public void exporter();
    public Boolean checker(int pos);
}
