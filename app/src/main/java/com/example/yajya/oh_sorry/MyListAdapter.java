package com.example.yajya.oh_sorry;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

/**
 * Created by yajya on 2017-05-31.
 */

public class MyListAdapter extends ArrayAdapter {
    Context context;
    int LayoutRes;
    ArrayList<Place> places;
    int time_s;
    int time_e;

    public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Place> objects) {
        super(context, resource, objects);
        this.context = context;
        this.LayoutRes = resource;
        this.places = objects;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(LayoutRes, null);
        }

        final Place item = places.get(position);

        if ( item != null ){
            TextView textView = (TextView)v.findViewById(R.id.title);
            textView.setText(item.getName());
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.checkAllDay);

            final TimePicker startTime = (TimePicker)v.findViewById(R.id.startTime);
            final TimePicker endTime = (TimePicker)v.findViewById(R.id.endTime);

            startTime.setEnabled(false);
            endTime.setEnabled(false);
            checkBox.setChecked(true);

            if(item.getStart() == 0){

            } else {
                int dateStart = item.getStart();
                int dateEnd = item.getEnd();

                if(dateStart == -1 && dateEnd == -1){
                    startTime.setEnabled(false);
                    endTime.setEnabled(false);
                    checkBox.setChecked(true);
                } else {
                    startTime.setEnabled(true);
                    endTime.setEnabled(true);
                    checkBox.setChecked(false);

                    int hourStart = dateStart / 100;
                    int minStart = dateStart % 100;
                    int hourEnd = dateEnd / 100;
                    int minEnd = dateEnd % 100;

                    if (android.os.Build.VERSION.SDK_INT >= 23) { // 안드로이드 sdk 버전에 따라 맞는 시간 get 함수 사용
                        startTime.setHour(hourStart);
                        startTime.setMinute(minStart);

                        endTime.setHour(hourEnd);
                        endTime.setMinute(minEnd);
                    } else {
                        startTime.setCurrentHour(hourStart);
                        startTime.setCurrentMinute(minStart);

                        endTime.setCurrentHour(hourEnd);
                        endTime.setCurrentMinute(minEnd);
                    }
                }
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked==true){
                        startTime.setEnabled(false);
                        endTime.setEnabled(false);
                        time_s = -1;
                        time_e = -1;
                    } else {
                        startTime.setEnabled(true);
                        endTime.setEnabled(true);
                        time_s = 0;
                        time_e = 0;
                    }
                }
            });

            Button btn = (Button)v.findViewById(R.id.btnAdd);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int startHour = 0;
                    int startMin = 0;
                    int endHour = 0;
                    int endMin = 0;

                    if(time_s != -1 & time_e != -1) {
                        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            startHour = startTime.getHour();
                            startMin = startTime.getMinute();
                            endHour = endTime.getHour();
                            endMin = endTime.getMinute();
                        } else {
                            startHour = startTime.getCurrentHour();
                            startMin = startTime.getCurrentMinute();
                            endHour = endTime.getCurrentHour();
                            endMin = endTime.getCurrentMinute();
                        }

                        time_s = startHour * 100 + startMin;
                        time_e = endHour * 100 + endMin;
                    }
                    MyDBHandler dbHandler = new MyDBHandler(getContext(), null, null, 4);
                    Place place = new Place(item.getTag(), item.getName(), item.getLat(), item.getLng(), time_s, time_e);
                    dbHandler.updatePlace(place);
                }
            });

        }

        return v;
    }
}