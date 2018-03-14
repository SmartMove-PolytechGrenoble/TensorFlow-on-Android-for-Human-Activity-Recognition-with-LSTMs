package io.github.introml.activityrecognition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

/**
 * Created by anthony on 13/03/18.
 */


public class HomeActivity extends Activity {

    private Button goToTrainingMode ;
    private Button goToCreatingMode ;
    private Button goAir ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        goToTrainingMode = (Button) findViewById(R.id.goToTrainingMode);
        goToTrainingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        goToCreatingMode = (Button) findViewById(R.id.goToCreatingMode);
        goToCreatingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateTrainingActivity.class);
                startActivity(intent);
            }
        });

        goAir = (Button) findViewById(R.id.goAir);
        goAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://air.imag.fr/index.php/SmartMove"));
                startActivity(browserIntent);
            }
        });



    }

}