package org.techtown.calencalen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String _ID = "ID";
        public static final String Title = "title";
        public static final String Year = "year";
        public static final String Month = "month";
        public static final String Day = "day";
        public static final String Memo = "memo";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +Title+" text not null , "
                +Year+" text not null , "
                +Month+" text not null , "
                +Day+" text not null , "
                +Memo+" text not null );";
    }
}
