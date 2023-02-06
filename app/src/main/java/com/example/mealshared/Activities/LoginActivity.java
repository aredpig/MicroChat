package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.lifecycle.LifecycleObserver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mealshared.AVLTree;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;

public class LoginActivity extends AppCompatActivity implements LifecycleObserver {
    // Modified by Jiawei, change user into a static variable.
    // This is used to represent current user (user logged in).
    public static User user;
    // End here.
    private Button LoginButton;
    private EditText Email;
    private EditText Password;
    final DatabaseHelper DB = DatabaseHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Password = findViewById(R.id.editTextTextPassword);
        Email = findViewById(R.id.editTextTextEmail);
        LoginButton = findViewById(R.id.buttonLogin);
        LoginButton.setOnClickListener(ButtonListener);

        BuildTree();
    }

    /**
     * DO Not Delete this function!!!!
     */
    private void BuildTree(){
        DB.DeleteTree();
        DB.getUsers(DBUsers -> {
            AVLTree buildTree = new AVLTree(DBUsers.get(0));
            for(int i=1;i<DBUsers.size();i++){
                buildTree = buildTree.insertNode(DBUsers.get(i),buildTree);
            }
            DB.AddAVL(buildTree);
        });
    }

    private View.OnClickListener ButtonListener = v -> {
        if (Email.getText().toString().length()==0 || Password.getText().toString().length()==0) {
            Toast.makeText(LoginActivity.this,"Please complete missing blanks",Toast.LENGTH_SHORT).show();
            return;
        }
        DB.getUser(Email.getText().toString(), new Consumer<User>() {
            @Override
            public void accept(User TempUser) {
                user = TempUser;
                if (user!=null){
                    String ConvertedPassword = String.valueOf(Password.getText().toString().hashCode());
                    if(ConvertedPassword.equals(user.getPassword())) {
                        Toast.makeText(LoginActivity.this, "Valid User!", Toast.LENGTH_SHORT).show();
                        Intent mainPage = new Intent(LoginActivity.this, NavigatePageActivity.class);
                        mainPage.putExtra("Email",user.getEmail());
                        startActivity(mainPage);
                        finish();
                    }
                    else Toast.makeText(LoginActivity.this,"Wrong Password!",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(LoginActivity.this,"User is not find in database",Toast.LENGTH_SHORT).show();
            }
        });
    };

    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
        finish();
    }
}