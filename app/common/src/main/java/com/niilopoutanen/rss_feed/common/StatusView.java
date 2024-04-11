package com.niilopoutanen.rss_feed.common;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.ArrayList;
import java.util.List;


public class StatusView extends LinearLayoutCompat {
    private final List<Status> statusQueue = new ArrayList<>();
    private final List<Runnable> eventQueue = new ArrayList<>();
    private final List<Runnable> finalEventQueue = new ArrayList<>();
    private final Handler queueHandler = new Handler();
    private final int ADD_STATUS_DELAY = 1000;
    public StatusView(@NonNull Context context) {
        super(context);
        init();
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        setOrientation(VERTICAL);
        setLayoutTransition(new LayoutTransition());
        queueHandler.postDelayed(this::processQueue, ADD_STATUS_DELAY);
    }

    public void clearMessages(){
        removeAllViews();
    }
    public void addStatus(String msg){
        Status status = new Status(msg, Status.Type.PROCESSING);
        statusQueue.add(status);
    }
    public void addStatus(String msg, Status.Type type){
        Status status = new Status(msg, type);
        statusQueue.add(status);
    }
    public void addStatus(Status status){
        statusQueue.add(status);
    }

    public void addQueueEvent(Runnable event){
        eventQueue.add(event);
    }
    public void addFinalEvent(Runnable event){
        finalEventQueue.add(event);
    }
    private void processQueue() {
        if (!statusQueue.isEmpty()) {
            Status status = statusQueue.get(0);
            addToLayout(status);
            statusQueue.remove(0);
        }
        if(!eventQueue.isEmpty()){
            eventQueue.get(0).run();
            eventQueue.remove(0);
        }
        if(statusQueue.isEmpty()){
            //Run remaining base events first;
            for(Runnable event: eventQueue){
                event.run();
            }
            eventQueue.clear();

            //Run all final events when done;
            for(Runnable event: finalEventQueue){
                event.run();
            }
            finalEventQueue.clear();
        }

        queueHandler.postDelayed(this::processQueue, ADD_STATUS_DELAY);
    }
    private void addToLayout(Status status){
        TextView statusText = new TextView(getContext());

        String msg = status.msg;
        if(status.type == Status.Type.PROCESSING){
            msg += "...";
        }
        else if(status.type == Status.Type.SUCCESS){
            msg += " ✅";
        }
        else if(status.type == Status.Type.FAILURE){
            msg += " ❌";
        }
        statusText.setText(msg);

        statusText.setTextColor(getContext().getColor(R.color.textSecondary));
        statusText.setGravity(Gravity.CENTER_HORIZONTAL);
        statusText.setTextSize(17);
        statusText.setTypeface(getContext().getResources().getFont(R.font.inter_medium));
        super.addView(statusText);
    }

    public static class Status{
        public enum Type{
            SUCCESS, FAILURE, PROCESSING
        }
        public final String msg;
        public final Type type;
        public Status(String msg, Type type){
            this.msg = msg;
            this.type = type;
        }
    }
}
