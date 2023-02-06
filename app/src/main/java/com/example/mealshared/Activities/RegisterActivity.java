package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText Email;
    private EditText Username;
    private EditText Password;
    private AVLTree tree;
    int AvailableUid;
    final DatabaseHelper DB = DatabaseHelper.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GetTree();
        Email = findViewById(R.id.editTextTextEmailAddress);
        Username = findViewById(R.id.editTextTextUsername);
        Password = findViewById(R.id.editTextTextPassword);
        registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(ButtonListener);
    }



    private void GetTree() {
        DB.getTree(Temptree -> {
            tree = Temptree;
        });
    }

    public boolean validFormat(String string){
        String datePattern = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    private View.OnClickListener ButtonListener = v -> {
        if (Email.getText().toString().length() == 0 || Password.getText().toString().length() == 0|| Username.getText().toString().length() == 0){
            Toast.makeText(RegisterActivity.this,"Please complete missing blanks",Toast.LENGTH_SHORT).show();
            return;
        }

        // check if email input is valid
        if (!validFormat(Email.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Wrong email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        DB.userExist(Email.getText().toString(),new Consumer<Boolean>() {
            public void accept(Boolean isExist){
                if (!isExist){
                    if (tree==null) {
                        Toast.makeText(RegisterActivity.this,"Check your Internet!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AvailableUid = tree.findMax().getUid()+1;
                    User user = new User(AvailableUid,Email.getText().toString(), Username.getText().toString(),Password.getText().toString());
                    DB.AddUser(user);
                    tree = tree.insertNode(user,tree);
                    DB.UpdateTree(tree);
                    Toast.makeText(RegisterActivity.this,"Congratulations! Successfully Registered!",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,"User is already exist！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    };
}