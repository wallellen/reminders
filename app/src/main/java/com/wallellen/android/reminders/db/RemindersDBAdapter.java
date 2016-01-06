/*
 * This file Copyright (c) 2016. Walle.
 * (http://www.wallellen.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the
 * Walle Agreement (WA) and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or WA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Walle Agreement (WA), this file
 * and the accompanying materials are made available under the
 * terms of the WA which accompanies this distribution, and
 * is available at http://www.wallellen.com/agreement.html
 *
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 */

package com.wallellen.android.reminders.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.wallellen.android.reminders.model.Reminder;

/**
 * Created by walle on 1/6/16.
 */
public class RemindersDbAdapter {
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "_content";
    public static final String COL_IMPORTANT = "important";

    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 1;

    private static final String TAG = "RemindersDbAdapter";

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;


    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 1;

    private final Context ctx;

    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "(" +
            COL_ID + " integer primary key autoincrement, " +
            COL_CONTENT + " text, " +
            COL_IMPORTANT + " INTEGER) ";


    public RemindersDbAdapter(Context context) {
        this.ctx = context;
    }

    public void createReminder(String name, boolean important) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important ? 1 : 0);
        db.insert(TABLE_NAME, null, values);
    }

    public void createReminder(Reminder reminder) {
        createReminder(reminder.getmContent(), reminder.getmImportant() == 1);
    }

    public Reminder fetchReminderById(int id) {
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return new Reminder(cursor.getInt(INDEX_ID), cursor.getString(INDEX_CONTENT), cursor.getInt(INDEX_IMPORTANT));
    }

    public Cursor fetchAllReminders() {
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT},
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getmContent());
        values.put(COL_IMPORTANT, reminder.getmImportant());

        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(reminder.getmId())});
    }

    public void deleteReminderById(int id) {
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllReminders() {
        db.delete(TABLE_NAME, null, null);
    }

    public void open() {
        databaseHelper = new DatabaseHelper(this.ctx);
        db = databaseHelper.getWritableDatabase();
    }

    public void close() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
                    ", which will destroy all data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
