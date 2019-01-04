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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import am.amitm29.com.home.Database.AppDatabase;
import am.amitm29.com.home.Database.CustomControls;

import static am.amitm29.com.home.Adapters.CustomControlsAdapter.EXTRA_ID;

public class AddControlActivity extends AppCompatActivity {

    Window window;
    private EditText titleET, noteET, outputET, inputET;
    private boolean mHasChanged = false;
    private long mId;
    private ImageView deleteIV;
    private TextInputLayout titleTIL, outputTIL, inputTIL;
    private AppDatabase mDb;
    private CustomControls mCurrentControl;

    @Override
    protected void onResume() {
        super.onResume();
        mDb = AppDatabase.getsInstance(this);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fabControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveControl();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(AddControlActivity.this,R.color.colorPrimaryDark));

        }
        findViewById(R.id.LL1).setBackgroundColor(ContextCompat.getColor(AddControlActivity.this ,R.color.colorPrimary));

        titleET = findViewById(R.id.title_et);
        noteET = findViewById(R.id.note_et);
        outputET = findViewById(R.id.output_et);
        inputET = findViewById(R.id.input_et);
        titleTIL = findViewById(R.id.title_layout);
        outputTIL = findViewById(R.id.output_layout);
        inputTIL = findViewById(R.id.input_layout);

        deleteIV = findViewById(R.id.deleteIV);

        findViewById(R.id.saveIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveControl();
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
        mId = intent.getLongExtra(EXTRA_ID, 0);

        if(mId ==0){
//            setTitle("Add an entry");
            deleteIV.setVisibility(View.GONE);
        }
        else{
//            setTitle("Edit the entry");
            deleteIV.setVisibility(View.VISIBLE);
            loadControl();
        }

        setUpOnTouchAndFocusListeners();


    }

    private void saveControl() {
        final String title = titleET.getText().toString().trim();
        final String note = noteET.getText().toString().trim();
        final String output = outputET.getText().toString().trim();
        final String input = inputET.getText().toString().trim();
        final long time = System.currentTimeMillis();

        if(TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter the title", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(output)){
            Toast.makeText(this, "Please enter the output message", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(input)){
            Toast.makeText(this, "Please enter the expected input", Toast.LENGTH_SHORT).show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mId ==0){
                        mDb.controlsDao().insertControl(new CustomControls(title, note, output, input, false, time));
                    }

                    else{
                        mDb.controlsDao().updateControl(new CustomControls(mId, title, note, output, input, false, time));
                    }
                }
            }).start();
            if(mId==0)
                Toast.makeText(this, "Custom control saved!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Custom control updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void loadControl()
    {
        mDb = AppDatabase.getsInstance(this);
        final LiveData<CustomControls> customControls = mDb.controlsDao().loadControlById(mId);
        customControls.observe(this, new Observer<CustomControls>() {
            @Override
            public void onChanged(@Nullable CustomControls customControl) {
                customControls.removeObserver(this);
                populateUI(customControl);
                mCurrentControl = customControl;
            }
        });

    }

    private void populateUI(CustomControls customControl) {
        titleET.setText(customControl.getTitl());
        noteET.setText(customControl.getNote());
        outputET.setText(customControl.getOutput());
        inputET.setText(customControl.getInput());
    }

    public void deleteControl(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDb.controlsDao().deleteControl(mCurrentControl);
            }
        }).start();
        Toast.makeText(this, "Custom control deleted!", Toast.LENGTH_SHORT).show();
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
                deleteControl();
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


    private void validateInputET(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            inputTIL.setError(null);
        }
        else{
            inputTIL.setError("Enter the expected input!");
        }
    }

    private void validateTitleET(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            titleTIL.setError(null);
        }
        else{
            titleTIL.setError("Enter the title!");
        }
    }

    private void validateOutputET(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            outputTIL.setError(null);
        }
        else{
            outputTIL.setError("Enter the output message!");
        }
    }

    private void setUpOnTouchAndFocusListeners() {
        titleET.setOnTouchListener(mTouchListener);
        noteET.setOnTouchListener(mTouchListener);
        outputET.setOnTouchListener(mTouchListener);
        inputET.setOnTouchListener(mTouchListener);

        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateTitleET(s);
            }
        });

        titleET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateTitleET(((EditText) v).getText());
                }
            }
        });

        outputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateOutputET(s);
            }
        });

        outputET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateOutputET(((EditText) v).getText());
                }
            }
        });

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputET(s);
            }
        });

        inputET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateInputET(((EditText) v).getText());
                }
            }
        });
    }


}
