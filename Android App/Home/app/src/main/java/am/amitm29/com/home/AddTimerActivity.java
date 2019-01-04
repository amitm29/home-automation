package am.amitm29.com.home;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import am.amitm29.com.home.Database.DbHelper;
import am.amitm29.com.home.Database.Timer;
import am.amitm29.com.home.Utils.BTConnectionUtils;



public class AddTimerActivity extends AppCompatActivity{
    Window window;
    private boolean mHasChanged;
    private EditText titleEt, noteEt, durationEt, rIdEt;
    private TextInputLayout rIdTIL, durationTIL;
    private Spinner actionSpnr;
    private int mAction;
    private ImageView deleteIV;
    private char mR_ID = 'c';
    private String saveStr = "";
    public static String EXTRA_RID = "timer_rid";
    private String LOG_TAG = "AddTimerActivity";

    Timer mCurrentTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        flag = false;
        setContentView(R.layout.activity_add_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(AddTimerActivity.this,R.color.colorPrimaryDark));

        }
        findViewById(R.id.LL1).setBackgroundColor(ContextCompat.getColor(AddTimerActivity.this ,R.color.colorPrimary));

        findViewById(R.id.fabTimer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimer();
            }
        });

        titleEt = findViewById(R.id.title_et);
        durationEt = findViewById(R.id.duration_et);
        rIdEt = findViewById(R.id.r_id_et);
        actionSpnr = findViewById(R.id.action_spnr);
        setUpSpinnerListener();

        rIdTIL = findViewById(R.id.r_id_layout);
        durationTIL = findViewById(R.id.duration_layout);

        deleteIV = findViewById(R.id.deleteIV);

        findViewById(R.id.saveIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimer();
            }
        });

        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mHasChanged) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, close the current activity.
                                    finish();
                                }
                            };

                    // Show dialog that there are unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
                else
                    finish();
            }
        });

        Intent intent = getIntent();
        mR_ID = intent.getCharExtra(EXTRA_RID, 'c');

        if(mR_ID=='c'){
            deleteIV.setVisibility(View.GONE);
        }
        else{
            deleteIV.setVisibility(View.VISIBLE);
            loadTimer();
        }

        setUpOnTouchAndFocusListeners();

    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHasChanged = true;
            return false;
        }
    };



    public void setUpSpinnerListener() {
        actionSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    switch (selection) {
                        case "ON": mAction = Timer.AC_ON;
                            break;
                        case "OFF": mAction = Timer.AC_OFF;
                            break;
                        default: mAction = Timer.AC_OFF;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mAction = Timer.AC_ON;
            }
        });
    }

    private void saveTimer() {
        final String r_id = rIdEt.getText().toString().trim();
        final String durationStr = durationEt.getText().toString().trim();
        final String title = titleEt.getText().toString().trim();


        if(TextUtils.isEmpty(r_id)) {
            Toast.makeText(this, "Please enter the switch number", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(durationStr)){
            Toast.makeText(this, "Please enter the timer duration", Toast.LENGTH_SHORT).show();
            return;
        }
        final long duration = Long.parseLong(durationStr);
        final long finalTime = System.currentTimeMillis() + duration*1000;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mR_ID=='c'){
                    DbHelper.getDbInstance().timerDao().addTimer(new Timer(r_id.charAt(0),title, mAction, duration, finalTime));
                }

                else{
                    DbHelper.getDbInstance().timerDao().updateTimer(
                            new Timer(r_id.charAt(0),title, mAction, duration, finalTime));
                }
            }
        }).start();

        saveStr = "T" + duration + "R" + r_id.charAt(0) + "/" + mAction + "#";
        BTConnectionUtils.writeBT(saveStr);

        Log.d(LOG_TAG, saveStr);

        if(mR_ID=='c')
            Toast.makeText(this, "Timer saved!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Timer updated!", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void loadTimer() {
        final LiveData<Timer> timers = DbHelper.getDbInstance().timerDao().loadTimerById(mR_ID);
        timers.observe(this, new Observer<Timer>() {
            @Override
            public void onChanged(@Nullable Timer timer) {
                timers.removeObserver(this);
                if(timer!=null) {
                    Log.d(LOG_TAG, "UI populated");
                    populateUI(timer);
                    mCurrentTimer = timer;
                }
                else
                    Log.d(LOG_TAG, "timer equals null");
            }
        });
    }

    private void populateUI(Timer timer) {
        titleEt.setText(timer.getTitl());
        rIdEt.setText(timer.getR_ID()+"");
        durationEt.setText(timer.getDurationInSec()+"");
        actionSpnr.setSelection(timer.getAction());
    }

    private void deleteTimer() {
        String delStr = "TR" + mCurrentTimer.getR_ID();
        BTConnectionUtils.writeBT(delStr);
        Log.d(LOG_TAG, "Sent string : " + delStr);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCurrentTimer != null) {
                    DbHelper.getDbInstance().timerDao().deleteTimer(mCurrentTimer);
                    Log.d(LOG_TAG, "Timer deleted!");
                }else
                    Log.d(LOG_TAG, "mCurrentTimer is null, timer cannot be deleted");
            }
        }).start();
        Toast.makeText(this, "Timer deleted!", Toast.LENGTH_SHORT).show();
        finish();
    }





    @Override
    public void onBackPressed() {
        // If the entry hasn't changed, continue with handling back button press
        if (!mHasChanged) {
            super.onBackPressed();
        }

        else {
            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTimer();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the Cancel button, so dismiss the dialog
                // and continue editing the entry
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void validateRIdET(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            rIdTIL.setError(null);
        }
        else{
            rIdTIL.setError("Enter the switch number!");
        }
    }


    private void validateDurationET(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            durationTIL.setError(null);
        }
        else{
            durationTIL.setError("Enter the duration of the timer!");
        }
    }

    private void setUpOnTouchAndFocusListeners() {
        rIdEt.setOnTouchListener(mTouchListener);
        durationEt.setOnTouchListener(mTouchListener);


        rIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateRIdET(s);
            }
        });

        rIdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateRIdET(((EditText) v).getText());
                }
            }
        });



        durationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateDurationET(s);
            }
        });

        durationEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateDurationET(((EditText) v).getText());
                }
            }
        });
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        flag=true;
//    }
}
