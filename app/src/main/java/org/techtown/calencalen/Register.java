package org.techtown.calencalen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.calencalen.db.DbOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Main";
    DatePickerDialog datePickerDialog;
    Button btn_Update;
    Button btn_Insert;
    Button btn_Select;
    Button button;
    EditText edit_Title;
    EditText edit_Year;
    EditText edit_Month;
    EditText edit_Day;
    EditText edit_Memo;
    TextView text_Title;
    TextView text_Year;
    TextView text_Month;
    TextView text_Day;
    TextView text_Memo;

    int mYear, mMonth, mDay;
    TextView mTxtDate;

    long nowIndex;
    String title;
    String year;
    String month;
    String day;
    String memo;
    String sort = "userid";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mTxtDate = (TextView)findViewById(R.id.button);

        //현재 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        UpdateNow();//화면에 텍스트뷰에 업데이트 해줌.

        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);

        btn_Select = (Button) findViewById(R.id.btn_select);
        btn_Select.setOnClickListener(this);
        edit_Title = (EditText) findViewById(R.id.edit_title);
        edit_Memo = (EditText) findViewById(R.id.edit_memo);
        //edit_Year = (EditText) findViewById(R.id.edit_year);
        //edit_Month = (EditText) findViewById(R.id.edit_month);
        //edit_Day = (EditText) findViewById(R.id.edit_day);
        text_Title = (TextView) findViewById(R.id.text_title);
        text_Memo = (TextView) findViewById(R.id.text_memo);
        //text_Year = (TextView) findViewById(R.id.text_year);
        //text_Month = (TextView) findViewById(R.id.text_month);
        //text_Day= (TextView) findViewById(R.id.text_day);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

       showDatabase(sort);

        btn_Insert.setEnabled(true);
        //btn_Update.setEnabled(false);

    }

    public void setInsertMode(){
        edit_Title.setText("");
        //edit_Year.setText("");
        //edit_Month.setText("");
        //edit_Day.setText("");
        edit_Memo.setText("");
        btn_Insert.setEnabled(true);
        //btn_Update.setEnabled(false);
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("On Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            Log.e("On Click", "nowIndex = " + nowIndex);
            Log.e("On Click", "Data: " + arrayData.get(position));
            String[] tempData = arrayData.get(position).split("\\s+");
            Log.e("On Click", "Split Result = " + tempData);
            edit_Title.setText(tempData[0].trim());
            edit_Year.setText(tempData[1].trim());
            edit_Month.setText(tempData[2].trim());
            edit_Day.setText(tempData[3].trim());
            edit_Memo.setText(tempData[4].trim());
            btn_Insert.setEnabled(false);
           // btn_Update.setEnabled(true);
        }
    };
    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Long Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            String[] nowData = arrayData.get(position).split("\\s+");
            String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2] + ", " + nowData[3];
            AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Register.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumn(nowIndex);
                            showDatabase(sort);
                            setInsertMode();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Register.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            setInsertMode();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("ID"));
            String tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
            tempTitle = setTextLength(tempTitle,10);
            String tempYear = iCursor.getString(iCursor.getColumnIndex("year"));
            tempYear = setTextLength(tempYear,10);
            String tempMonth = iCursor.getString(iCursor.getColumnIndex("month"));
            tempMonth = setTextLength(tempMonth,10);
            String tempDay = iCursor.getString(iCursor.getColumnIndex("day"));
            tempDay = setTextLength(tempDay,10);
            String tempMemo = iCursor.getString(iCursor.getColumnIndex("memo"));
            tempMemo = setTextLength(tempMemo,10);

            String Result = tempTitle + tempYear + tempMonth + tempDay+tempMemo;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }


    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // TODO Auto-generated method stub
                    //사용자가 입력한 값을 가져온뒤

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    UpdateNow();
                }

            };


    void UpdateNow(){

        mTxtDate.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));

    }
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.button:
                new DatePickerDialog(Register.this, mDateSetListener, mYear, mMonth, mDay).show();
                break;

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:
                title = edit_Title.getText().toString();
                year = String.valueOf(mYear);
                month = String.valueOf(mMonth+1);
                day = String.valueOf(mDay);
                memo = edit_Memo.getText().toString();
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn(title, year, month, day, memo);
                showDatabase(sort);
                setInsertMode();
                edit_Title.requestFocus();
                edit_Title.setCursorVisible(true);

                break;



            case R.id.btn_select:
                showDatabase(sort);
                break;
        }
    }

}
