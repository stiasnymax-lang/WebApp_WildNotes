package hsa.de;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Diese ZWEI ZEILEN NICHT ÄNDERN!

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AB HIER EIGENER CODE

        final Button LoginButton = findViewById(R.id.login_button);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                setContentView(R.layout.activity_login);
            }
        });

        final Button RegisterButton = findViewById(R.id.register_button);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view){
                setContentView(R.layout.activity_register);
            }
        });
    }
}