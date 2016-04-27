package com.givmed.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


public class Email extends HelperActivity {
    private EditText mEditText1, mEditText2;
    private TextInputLayout subLayout, textLayout;

    private int REQUEST_CODE = 1923;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.email, R.string.email, true);

        mEditText1 = (EditText) findViewById(R.id.subject_text);
        mEditText2 = (EditText) findViewById(R.id.email_text);

        subLayout = (TextInputLayout) findViewById(R.id.subject_layout);
        textLayout = (TextInputLayout) findViewById(R.id.email_layout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();

            Intent showItemIntent = new Intent(getApplicationContext(), Elleipseis.class);
            //If set, and the activity being launched is already running in the current task, then instead of
            // launching a new instance of that activity, all of the other activities on top of it will be closed and
            // this Intent will be delivered to the (now on top) old activity as a new Intent.
            showItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //If set, the activity will not be launched if it is already running at the top of the history stack.
            showItemIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(showItemIntent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tick) {
            if (submitForm()) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ore_gia@hotmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, mEditText1.getText().toString());
                i.putExtra(Intent.EXTRA_TEXT, mEditText2.getText().toString());

                try {
                    startActivityForResult(Intent.createChooser(i, "Send mail..."), REQUEST_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean submitForm() {
        if (!validateInput(mEditText1, subLayout, R.string.err_msg_subject))
            return false;

        if (!validateInput(mEditText2, textLayout, R.string.err_msg_text))
            return false;

        return true;
    }

    private boolean validateInput(EditText edi, TextInputLayout lay, int msg) {
        if (edi.getText().toString().trim().isEmpty()) {
            lay.setError(getString(msg));
            requestFocus(edi);
            return false;
        } else {
            lay.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}