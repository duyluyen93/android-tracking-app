package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

  private TextInputEditText name_login, pass_login;
  private Button login;
  private Toolbar toolbar;
  private TextView navigate_to_signin;
  private FirebaseAuth mAuth;
  private AlertDialog.Builder alert;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    FirebaseApp.initializeApp(this);

    allIDs();
    alert = new AlertDialog.Builder(LogIn.this);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    navigate_to_signin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        navigate_to_signin.setTextColor(Color.GREEN);
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            navigate_to_signin.setTextColor(Color.parseColor("#64B5F6"));
            Log.d("TAG", "Changed");
          }
        }, 500);
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);
      }
    });

    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email = name_login.getText().toString();
        final String password = pass_login.getText().toString();
        if (email.isEmpty()) {
          alert.setTitle("Thông báo").setMessage("Nhập email")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
          alert.show();
          return;
        }
        if (password.isEmpty()) {
          alert.setTitle("Thông báo").setMessage("Nhập mật khẩu")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });
          alert.show();
          return;
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                alert.setTitle("Welcome").setMessage("Thành công")
                  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                      Intent intent = new Intent(LogIn.this, MainActivity.class);
                      intent.putExtra("Reload", true);
                      startActivity(intent);
                      finish();
//                      Log.d("Kết quả:", "" + mAuth.getCurrentUser());
                    }
                  });
                alert.show();
                Log.d("User", mAuth.getCurrentUser().toString());
              } else {
                alert.setTitle("Đăng nhập không thành công")
                  .setMessage("Kiểm tra lại email và mật khẩu")
                  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                  });
                alert.show();
              }
            }
          });
      }
    });
  }

  private void allIDs() {
    name_login = findViewById(R.id.name_login);
    pass_login = findViewById(R.id.pass_login);
    login = findViewById(R.id.login);
    toolbar = findViewById(R.id.toolbar_login);
    navigate_to_signin = findViewById(R.id.navigate_to_signin);
    pass_login.setTransformationMethod(new PasswordTransformationMethod());
  }
}
