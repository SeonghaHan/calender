package org.techtown.calencalen;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.techtown.calencalen.db.DbOpenHelper;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;


public class FirstFragment extends Fragment {
    private OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private int yy,mm,dd;
    private MaterialCalendarView cal_v;
    private DbOpenHelper mDbOpenHelper;
    private List<String> DataList;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cal_v = getActivity().findViewById(R.id.calendarView);

        View view = inflater.inflate(R.layout.fragment_first,container,false);
        cal_v = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        cal_v.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        cal_v.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);
        cal_v.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                yy = date.getYear();
                mm = date.getMonth()+1;
                dd = date.getDay();

                Intent intent = new Intent(getActivity(),Register.class);
                intent.putExtra("year",yy);
                intent.putExtra("month",mm);
                intent.putExtra("day",dd);

                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DataList = new ArrayList<>();
        search();
        new ApiSimulator(DataList).executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
            List<String> Time_Result;

        ApiSimulator(List<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            for(int i = 0 ; i < Time_Result.size() ; i ++){
                String[] time = Time_Result.get(i).split("-");
                int pYear = Integer.parseInt(time[0]);
                int pMonth = Integer.parseInt(time[1]);
                int pDay = Integer.parseInt(time[2]);

                calendar.set(pYear, pMonth - 1, pDay);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            cal_v.addDecorator(new EventDecorator(Color.RED, calendarDays, getActivity()));
        }
    }
    public void search(){
        mDbOpenHelper = new DbOpenHelper(getActivity());
        mDbOpenHelper.open();

        Cursor iCursor = mDbOpenHelper.selectColumns();

        if(iCursor != null) {
            while (iCursor.moveToNext()) {
                String d = iCursor.getString(iCursor.getColumnIndex("year"))
                        +"-"+iCursor.getString(iCursor.getColumnIndex("month"))
                        +"-"+iCursor.getString((iCursor.getColumnIndex("day")));
                Log.d("date : ", d);
                DataList.add(d);
            }
        }
    }





}
