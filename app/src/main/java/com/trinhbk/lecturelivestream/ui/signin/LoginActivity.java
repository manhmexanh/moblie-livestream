package com.trinhbk.lecturelivestream.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.home.HomeActivity;
import com.trinhbk.lecturelivestream.ui.utils.Constants;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button btnStudent;

    private Button btnTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        btnStudent = findViewById(R.id.btnStudent);
        btnTeacher = findViewById(R.id.btnTeacher);
        btnStudent.setOnClickListener(this);
        btnTeacher.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTeacher:
                loginWithTeacher();
                break;
            case R.id.btnStudent:
                loginWithStudent();
                break;
        }
    }

    private void loginWithStudent() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra(Constants.IntentKey.KEY_INTENT_USER_PERMISSION, false);
        startActivity(intent);
        finish();
    }

    private void loginWithTeacher() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra(Constants.IntentKey.KEY_INTENT_USER_PERMISSION, true);
        startActivity(intent);
        finish();
    }

}
