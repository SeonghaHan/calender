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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.techtown.calencalen.db.DbOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {
    ArrayList<HashMap<String, String>> personList;
    ListView list;
    ListAdapter adapter;
    private OneDayDecorator oneDayDecorator = new OneDayDecorator();
    int mYear,mMonth,mDay;
    int yy,mm,dd;
    private MaterialCalendarView cal_v;
    private ListView _dynamic;
    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;
    private List<String> DataList;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.fragment_second,container, false);
        cal_v = getActivity().findViewById(R.id.calendarView);
        _dynamic = vv.findViewById(R.id.dddd);
        list = getActivity().findViewById(R.id.dddd);
        personList = new ArrayList<HashMap<String, String>>();



        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        View view = inflater.inflate(R.layout.fragment_second,container,false);
        cal_v = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        cal_v.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        cal_v.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);
        cal_v.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String d = date.toString();
                String dStr = d.substring(12,d.length() - 1);

                String[] time = dStr.split("-");
                mYear = Integer.parseInt(time[0]);
                mMonth = Integer.parseInt(time[1]);
                mDay = Integer.parseInt(time[2]);

            }
        });
        return view;
    }

    @Override

    public void onResume() {
        super.onResume();

        DataList = new ArrayList<>();
        search();
        new SecondFragment.ApiSimulator(DataList).executeOnExecutor(Executors.newSingleThreadExecutor());
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

    public void showDatabase(){

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
