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

import hsa.de.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("E-Mail eingeben");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Ungültige E-Mail-Adresse");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Passwort eingeben");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Passwort muss mindestens 6 Zeichen haben");
            editTextPassword.requestFocus();
            return;
        }

        buttonReg.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                        } else {
                            buttonReg.setEnabled(true);

                            String fehler = "Registrierung fehlgeschlagen";
                            if (task.getException() != null && task.getException().getMessage() != null) {
                                fehler = task.getException().getMessage();
                            }

                            Toast.makeText(
                                    RegisterActivity.this,
                                    fehler,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> emailTask) {

                            if (emailTask.isSuccessful()) {
                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Konto erstellt! Bitte E-Mail bestätigen.",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                String fehler = "Konto erstellt, aber Bestätigungsmail fehlgeschlagen.";
                                if (emailTask.getException() != null && emailTask.getException().getMessage() != null) {
                                    fehler = fehler + "\n" + emailTask.getException().getMessage();
                                }

                                Toast.makeText(
                                        RegisterActivity.this,
                                        fehler,
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            mAuth.signOut();
                            goToLogin();
                        }
                    });
        } else {
            Toast.makeText(
                    RegisterActivity.this,
                    "Benutzer konnte nach Registrierung nicht geladen werden.",
                    Toast.LENGTH_LONG
            ).show();

            buttonReg.setEnabled(true);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}