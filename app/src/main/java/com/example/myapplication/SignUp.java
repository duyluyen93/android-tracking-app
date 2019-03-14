package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUp extends AppCompatActivity {

  private TextInputEditText email_signup, pass_signup, repass_signup;
  private FirebaseAuth mAuth;
  private AlertDialog.Builder alert;
  private Button user_signup, driver_signup;
  private Toolbar toolbar_signup;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    allIDs();
    FirebaseApp.initializeApp(this);
    alert = new AlertDialog.Builder(SignUp.this);
    setSupportActionBar(toolbar_signup);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar_signup.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    user_signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signUp("Users");
      }
    });

    driver_signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signUp("Drivers");
      }
    });
  }

  private void signUp(final String typeOfUser) {
    final String email = email_signup.getText().toString();
    final String password = pass_signup.getText().toString();
    String repass = repass_signup.getText().toString();
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
    if (password != repass) {
      alert.setTitle("Thông báo").setMessage("Nhập lại mật khẩu chưa đúng")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    }
    mAuth = FirebaseAuth.getInstance();
    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()) {
            alert.setTitle("Đăng ký thành công!")
              .setMessage("Chào mừng bạn " + email + ". Hãy đăng nhập bằng email này.")
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  // Toast.makeText(SignUp.this, "OK", Toast.LENGTH_SHORT).show();
                  finish();
                }
              });
            alert.show();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child(typeOfUser)
              .child(mAuth.getCurrentUser().getUid());
            myRef.child("Tài xế").setValue("Đã đăng ký");
            Log.d("Register", "=======" + mAuth.getCurrentUser());
          } else {
            alert.setTitle("Lỗi").setMessage("Không thể đăng ký")
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              });
          }
        }
      });

  }

  private void allIDs() {
    toolbar_signup = findViewById(R.id.toolbar_signup);
    email_signup = findViewById(R.id.email_signup);
    pass_signup = findViewById(R.id.pass_signup);
    repass_signup = findViewById(R.id.repass_signup);
    user_signup = findViewById(R.id.user_signup);
    driver_signup = findViewById(R.id.driver_signup);
    pass_signup.setTransformationMethod(new PasswordTransformationMethod());
    repass_signup.setTransformationMethod(new PasswordTransformationMethod());
  }
}
