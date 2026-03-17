package hsa.de.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hsa.de.core.HomeActivity;
import hsa.de.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    Button buttonLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuth = FirebaseAuth.getInstance();
    editTextEmail = findViewById(R.id.email);
    editTextPassword = findViewById(R.id.password);
    buttonLog = findViewById(R.id.btn_login);


    buttonLog.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email, password;
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null && user.isEmailVerified()) {
                                    // E-Mail bestätigt → Login erlauben
                                    Toast.makeText(getApplicationContext(), "Login erfolgreich", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // E-Mail noch nicht bestätigt → abmelden & Hinweis
                                    mAuth.signOut();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Bitte bestätige zuerst deine E-Mail-Adresse.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Login fehlgeschlagen.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    });
}
}