package com.wallellen.android.reminders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wallellen.android.reminders.db.RemindersDbAdapter;
import com.wallellen.android.reminders.model.Reminder;

import java.util.Date;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.mipmap.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mListView = (ListView) findViewById(R.id.reminders_list_view);
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.reminders_row,
//                R.id.row_text, new String[]{"first record", "second record", "third record"});

        mListView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        initData(savedInstanceState);

        Cursor cursor = mDbAdapter.fetchAllReminders();
        String[] from = new String[]{RemindersDbAdapter.COL_CONTENT};
        int[] to = new int[]{R.id.row_text};
        mCursorAdapter = new ReminderSimpleCursorAdapter(RemindersActivity.this, R.layout.reminders_row, cursor, from, to, 0);

        mListView.setAdapter(mCursorAdapter);

//        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                MenuInflater inflater = mode.getMenuInflater();
//                inflater.inflate(R.menu.cam_menu, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_item_delete_reminder:
//                        for (int c = mCursorAdapter.getCount() - 1; c >= 0; c--) {
//                            if (mListView.isItemChecked(c)) {
//                                mDbAdapter.deleteReminderById((int) mCursorAdapter.getItemId(c));
//                            }
//                        }
//
//                        mode.finish();
//                        mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
//                        return true;
//                }
//
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//            }
//        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
//                Toast.makeText(RemindersActivity.this, "Clicked " + position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String[] modes = new String[]{"Edit Reminder", "Delete Reminder", "Schedule Reminder"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RemindersActivity.this, R.layout.reminders_row, R.id.row_text, modes);
                modeListView.setAdapter(arrayAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();

                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        if (position == 0) {
//                            Toast.makeText(parent.getContext(), "edit " + masterListPosition, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(parent.getContext(), "delete " + masterListPosition, Toast.LENGTH_SHORT).show();
//                        }

                        if (position == 0) {
                            int nId = getIdFromPosition(masterListPosition);
                            Reminder reminder = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);
                        } else if(position == 1){
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        } else {
                            Date date = new Date();
                            new TimePickerDialog(RemindersActivity.this, null, date.getHours(), date.getMinutes(), true).show();
                        }

                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        mDbAdapter.deleteAllReminders();
        //Add some data
        mDbAdapter.createReminder("Buy Learn Android Studio", true);
        mDbAdapter.createReminder("Send Dad birthday gift", false);
        mDbAdapter.createReminder("Dinner at the Gage on Friday", false);
        mDbAdapter.createReminder("String squash racket", false);
        mDbAdapter.createReminder("Shovel and salt walkways", false);
        mDbAdapter.createReminder("Prepare Advanced Android syllabus", true);
        mDbAdapter.createReminder("Buy new office chair", false);
        mDbAdapter.createReminder("Call Auto-body shop for quote", false);
        mDbAdapter.createReminder("Renew membership to club", false);
        mDbAdapter.createReminder("Buy new Galaxy Android phone", true);
        mDbAdapter.createReminder("Sell old Android phone - auction", false);
        mDbAdapter.createReminder("Buy new paddles for kayaks", false);
        mDbAdapter.createReminder("Call accountant about tax returns", false);
        mDbAdapter.createReminder("Buy 300,000 shares of Google", false);
        mDbAdapter.createReminder("Call the Dalai Lama back", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_new || id == R.id.action_exit) {
//            return true;
//        }

        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "Create new Reminder");
                fireCustomDialog(null);
                return true;
            case R.id.action_exit:
                finish();
                Log.d(getLocalClassName(), "Exit");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fireCustomDialog(final Reminder reminder) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView textView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editText = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButtom = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOption = reminder != null;

        if (isEditOption) {
            textView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getmImportant() == 1);
            editText.setText(reminder.getmContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }

        commitButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                boolean important = checkBox.isChecked();

                if (isEditOption) {
                    Reminder reminder1 = new Reminder(reminder.getmId(), text, important ? 1 : 0);
                    mDbAdapter.updateReminder(reminder1);
                } else {
                    mDbAdapter.createReminder(text, important);
                }

                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.custom_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            int nId = getIdFromPosition(position);
            Reminder reminder = mDbAdapter.fetchReminderById(nId);
            fireCustomDialog(reminder);
        } else {
            mDbAdapter.deleteReminderById(getIdFromPosition(position));
            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
        }


    }

    private int getIdFromPosition(int position) {
        return (int) mCursorAdapter.getItemId(position);
    }
}
