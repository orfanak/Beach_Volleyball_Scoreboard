package com.apps.orfanak.beachvolleyscoreboard;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.view.View.*;


public class MainActivity extends ActionBarActivity {

    private Button startButton;
    private EditText homeTeam;
    private EditText visitorTeam;
    private EditText numberOfSets;
    private EditText numberOfPoints;
    private EditText tieBreakPoints;
    private int myNumOfSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find views
        startButton = (Button) findViewById(R.id.button);
        homeTeam = (EditText) findViewById(R.id.editTextHomeTeam);
        visitorTeam = (EditText) findViewById(R.id.editTextVisitorTeam);

        numberOfSets = (EditText) findViewById(R.id.editTextNumOfSets);
        // setting the minimum and maximum number of sets
        numberOfSets.setFilters(new InputFilter[]{new InputFilterMinMax("1", "2")});

        numberOfPoints = (EditText) findViewById(R.id.editTextPointsPerSet);
        tieBreakPoints = (EditText) findViewById(R.id.editTextTieBreakPoints);
        tieBreakPoints.setFilters(new InputFilter[]{new InputFilterMinMax("1","15")});

        char[] home = new char[] {'H','o','m','e'};

        char[] visitor = new char[] {'V','i','s','i','t','o','r'};

        final char[] numOfSets = new char[] {'2'};

        char[] numOfPoints = new char[] {'2','1'};

        char[] tieBreakP = new char[] {'1','5'};


        homeTeam.setText(home,0, 4);
        visitorTeam.setText(visitor, 0, 7);
        numberOfSets.setText(numOfSets,0,1);
        numberOfPoints.setText(numOfPoints,0,2);
        tieBreakPoints.setText(tieBreakP,0,2);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // create an intent to start first set activity
                Intent i = new Intent(MainActivity.this, FirstSet.class);

                String stringHome = String.valueOf(homeTeam.getText());
                String stringVisitor = String.valueOf(visitorTeam.getText());
                String stringNumberOfSets = String.valueOf(numberOfSets.getText());
                String stringNumberOfPoints = String.valueOf(numberOfPoints.getText());
                String stringTieBreakPoints = String.valueOf(tieBreakPoints.getText());

                //handling number of sets <=3

                try {
                    myNumOfSets = Integer.parseInt(stringNumberOfSets);
                } catch (NumberFormatException nfe) {
                    System.out.println("Value must integer 1 or 2 or 3 not: " + nfe);
                }

                if ((myNumOfSets < 1) || (myNumOfSets > 2)){
                    Toast.makeText(getApplicationContext(),"Number of sets can only be 1 or 2",Toast.LENGTH_LONG).show();
                    stringNumberOfSets = "2";
                }

                //add data to a bundle
                Bundle extras = new Bundle();
                extras.putString("homeTeam", stringHome);
                extras.putString("visitorTeam", stringVisitor);
                extras.putString("numberOfSets", stringNumberOfSets);
                extras.putString("numberOfPoints", stringNumberOfPoints);
                extras.putString("tieBreakPoints", stringTieBreakPoints);

                i.putExtras(extras);
                startActivity(i);

            }
        });


    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}



