package com.apps.orfanak.beachvolleyscoreboard;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Environment;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.w3c.dom.Text;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.ArrayList;

        import com.google.android.gms.ads.AdListener;
        import com.google.android.gms.ads.AdRequest;
        import com.google.android.gms.ads.InterstitialAd;


public class FirstSet extends ActionBarActivity {

    //class variables
    private TextView homeTeamSet1;
    private TextView visitorTeamSet1;
    private int numOfSets;
    private int pointsPerSet;
    private String tieBreakPoints;
    private TextView homeSets;
    private TextView  visitorSets;
    private boolean setEnd = false;
    private Button nextSet;
    private Button homeScore;
    private Button visitorScore;
    private int homScore;
    private int visScore;
    private ImageView homeBall;
    private ImageView visitorBall;
    private ImageButton cancel;
    private ImageButton share;
    private boolean homeGainedPoint = false;
    private boolean visitorGainedPoint = false;
    private boolean homeWins = false;
    private boolean visitorWins = false;
    private int pointsPerSetReset=0;
    private String homeTeam="";
    private String visitorTeam="";
    private TextView finalScorePerSet;
    private String[] finalScoreSet = new String[3];
    private ImageButton resetButton;
    private AlertDialog.Builder dialog;
    private File imageFile;
    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_set);

        //Receiving data from main activity
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        homeTeam = bundle.getString("homeTeam");
        visitorTeam = bundle.getString("visitorTeam");
        numOfSets = Integer.valueOf(bundle.getString("numberOfSets"));
        pointsPerSet = Integer.valueOf(bundle.getString("numberOfPoints"));
        tieBreakPoints = bundle.getString("tieBreakPoints");
        pointsPerSetReset=pointsPerSet;

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3283484166219147/5791512936");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                    shareImage();
            }
        });

        requestNewInterstitial();



        // initializing textViews of first set xml
        homeTeamSet1 = (TextView) findViewById(R.id.textViewHomeTeam);
        homeTeamSet1.setText(homeTeam);

        visitorTeamSet1 = (TextView) findViewById(R.id.textViewVisitorTeam);
        visitorTeamSet1.setText(visitorTeam);

        homeSets = (TextView) findViewById(R.id.homeSets);
        visitorSets = (TextView) findViewById(R.id.visitorSets);

        //initializing final score textview
        finalScorePerSet = (TextView) findViewById(R.id.finalScorePerSet);
        finalScorePerSet.setVisibility(View.INVISIBLE);


        //initializing score buttons
        homeScore = (Button) findViewById(R.id.homeScoreId);
        visitorScore = (Button) findViewById(R.id.visitorScoreId);

        //initializing ball icon imageviews
        //these are set visible when team serves
        homeBall = (ImageView) findViewById(R.id.imageViewHomeId);
        visitorBall = (ImageView) findViewById(R.id.imageViewVisitorId);

        //initializing cancel button that cancels the most recent added point
        cancel = (ImageButton) findViewById(R.id.buttonCancel);

        //initializing reset button
        resetButton = (ImageButton) findViewById(R.id.resetButtonId);

        //handling reset button

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new AlertDialog.Builder(FirstSet.this);


                //set Title
                dialog.setTitle(getResources().getString(R.string.dialog_title));

                //set message
                dialog.setMessage(getResources().getString(R.string.dialog_message));

                //set cancelable
                dialog.setCancelable(false);

                // set an icon
                dialog.setIcon(android.R.drawable.ic_dialog_alert);

                //set Positive button
                dialog.setPositiveButton(getResources().getString(R.string.positive_button),

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Exit our activity
                                //FirstSet.this.newSet();
                                homeScore.setEnabled(true);
                                visitorScore.setEnabled(true);

                                homScore = 0;
                                homeScore.setText(String.valueOf(homScore));

                                visScore=0;
                                visitorScore.setText(String.valueOf(visScore));

                                homeBall.setVisibility(View.INVISIBLE);
                                visitorBall.setVisibility(View.INVISIBLE);

                                //reseting points per set to the value that the user entered in the beggining
                                //pointsPerSet = pointsPerSetReset;


                                if(setEnd && homeWins){

                                    //correcting num of home sets
                                    int i = Integer.valueOf(homeSets.getText().toString());


                                    i = i-1;
                                    if (i<0){
                                        i=0;
                                    }
                                    homeSets.setText(String.valueOf(i));
                                    finalScorePerSet.setVisibility(View.INVISIBLE);

                                    setEnd = false;
                                }

                                if(setEnd && visitorWins){


                                    //correcting num of visitor sets
                                    int i = Integer.valueOf(visitorSets.getText().toString());

                                    i = i-1;

                                    if(i<0){
                                        i=0;
                                    }
                                    visitorSets.setText(String.valueOf(i));
                                    finalScorePerSet.setVisibility(View.INVISIBLE);

                                    setEnd = false;
                                }
                                //handling ties and number of sets to find out the game winner
                                int numHomeSets = Integer.valueOf(homeSets.getText().toString());
                                int numVisitorSets = Integer.valueOf(visitorSets.getText().toString());
                                if (numHomeSets == 1 && numVisitorSets == 1){
                                    //we have a tiebreak
                                    pointsPerSet = Integer.valueOf(tieBreakPoints);
                                    Toast.makeText(getApplicationContext(),"Tie Break!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                // set negative button
                dialog.setNegativeButton(getResources().getString(R.string.negative_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.cancel();
                            }
                        });


                // create dialog
                AlertDialog alertD = dialog.create();



                //show dialog
                alertD.show();

            }
        });

        //initializing share button
        share = (ImageButton) findViewById(R.id.shareButtonId);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // share();

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {
                    shareImage();
                }

            }
        });



        homeScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homScore = Integer.valueOf(homeScore.getText().toString());
                visScore = Integer.valueOf(visitorScore.getText().toString());
                homScore++;
                homeScore.setText(String.valueOf(homScore));
                homeBall.setVisibility(View.VISIBLE);
                visitorBall.setVisibility(View.INVISIBLE);
                homeGainedPoint=true;
                visitorGainedPoint=false;


                if ((homScore == pointsPerSet) && (visScore < (pointsPerSet - 1))) {
                    setEnd = true;
                    homeWins =true;
                    homeScore.setEnabled(false);
                    visitorScore.setEnabled(false);

                    //num of home sets
                    int i = Integer.valueOf(homeSets.getText().toString());
                    //num of visitor sets
                    int j = Integer.valueOf(visitorSets.getText().toString());

                    i = i+1;
                    homeSets.setText(String.valueOf(i));

                    if((i+j)== 1) {
                        finalScoreSet[0] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) );
                        finalScorePerSet.setText(finalScoreSet[0]);
                    }
                    else if((i+j) == 2){
                        finalScoreSet[1] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) + "\n");
                        finalScorePerSet.setText(finalScoreSet[0] +  "\n" + finalScoreSet[1] );
                    }
                    else if((i+j) == 3){
                        finalScoreSet[2] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) + "\n");
                        finalScorePerSet.setText(finalScoreSet[0] +  "\n" + finalScoreSet[1]+  "\n" + finalScoreSet[2]  );
                    }
                    finalScorePerSet.setVisibility(View.VISIBLE);



                    Toast.makeText(getApplicationContext(),"Set goes to: " + homeTeam,Toast.LENGTH_LONG).show();
                    newSet();

                } else if (homScore == (pointsPerSet - 1) && (visScore == (pointsPerSet - 1))) {
                    setEnd = false;
                    pointsPerSet++;
                }




            }


        });


        visitorScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homScore = Integer.valueOf(homeScore.getText().toString());
                visScore = Integer.valueOf(visitorScore.getText().toString());
                visScore++;
                visitorScore.setText(String.valueOf(visScore));
                homeBall.setVisibility(View.INVISIBLE);
                visitorBall.setVisibility(View.VISIBLE);
                homeGainedPoint=false;
                visitorGainedPoint=true;



                if ((visScore == pointsPerSet) && (homScore < (pointsPerSet - 1))) {

                    setEnd = true;
                    visitorWins =true;
                    homeScore.setEnabled(false);
                    visitorScore.setEnabled(false);
                    finalScorePerSet.setText(String.valueOf(homScore) + " - " + String.valueOf(visScore));
                    finalScorePerSet.setVisibility(View.VISIBLE);

                    //num of home sets
                    int i = Integer.valueOf(homeSets.getText().toString());
                    //num of visitor sets
                    int j = Integer.valueOf(visitorSets.getText().toString());
                    j = j+1;
                    visitorSets.setText(String.valueOf(j));

                    if((i+j)== 1) {
                        finalScoreSet[0] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) );
                        finalScorePerSet.setText(finalScoreSet[0]);
                    }
                    else if((i+j) == 2){
                        finalScoreSet[1] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) + "\n");
                        finalScorePerSet.setText(finalScoreSet[0] +  "\n" + finalScoreSet[1] );
                    }
                    else if((i+j) == 3){
                        finalScoreSet[2] = (String.valueOf(homScore) + " - " + String.valueOf(visScore) + "\n");
                        finalScorePerSet.setText(finalScoreSet[0] +  "\n" + finalScoreSet[1]+  "\n" + finalScoreSet[2]  );
                    }
                    finalScorePerSet.setVisibility(View.VISIBLE);


                    Toast.makeText(getApplicationContext(),"Set goes to: " + visitorTeam,Toast.LENGTH_LONG).show();
                    newSet();

                } else if (homScore == (pointsPerSet - 1) && (visScore == (pointsPerSet - 1))) {
                    setEnd = false;
                    pointsPerSet++;

                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(homeGainedPoint && !visitorGainedPoint){
                    homScore--;
                    homeScore.setText(String.valueOf(homScore));
                    homeBall.setVisibility(View.INVISIBLE);
                }
                else if (!homeGainedPoint && visitorGainedPoint){
                    visScore--;
                    visitorScore.setText(String.valueOf(visScore));
                    visitorBall.setVisibility(View.INVISIBLE);

                }

                if(homScore<0) {
                    homScore = 0;
                    homeScore.setText(String.valueOf(homScore));
                }

                if (visScore<0){
                    visScore=0;
                    visitorScore.setText(String.valueOf(visScore));
                }
                if(setEnd && homeWins){
                    int i = Integer.valueOf(homeSets.getText().toString());
                    i = i-1;
                    if(i<0){i=0;}
                    homeSets.setText(String.valueOf(i));

                    setEnd = false;
                    homeWins = false;
                    homeScore.setEnabled(true);
                    visitorScore.setEnabled(true);
                    nextSet.setVisibility(View.INVISIBLE);

                }
                if(setEnd && visitorWins){
                    int i = Integer.valueOf(visitorSets.getText().toString());
                    i = i-1;
                    if(i<0){i=0;}
                        visitorSets.setText(String.valueOf(i));

                    setEnd = false;
                    homeWins = false;
                    homeScore.setEnabled(true);
                    visitorScore.setEnabled(true);
                    nextSet.setVisibility(View.INVISIBLE);

                }

            }
        });





    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        homeTeam = savedInstanceState.getString("home");
        visitorTeam = savedInstanceState.getString("visitor");
        homeSets.setText(savedInstanceState.getString("homeSets"));
        visitorSets.setText(savedInstanceState.getString("visitorSets"));
        homeScore.setText(savedInstanceState.getString("homeScore"));
        visitorScore.setText(savedInstanceState.getString("visitorScore"));
        finalScorePerSet.setText(savedInstanceState.getString("finalSetScore"));

        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        outState.putString("home", homeTeamSet1.getText().toString());
        outState.putString("visitor", visitorTeamSet1.getText().toString());
        outState.putString("homeSets", homeSets.getText().toString());
        outState.putString("visitorSets", visitorSets.getText().toString());
        outState.putString("homeScore", homeScore.getText().toString());
        outState.putString("visitorScore", visitorScore.getText().toString());
        outState.putString("finalSetScore", finalScorePerSet.getText().toString());


        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }



    private void share(){

        ArrayList<String> data = new ArrayList<String>();
        data.add("http://www.beachvolley.gr");
        data.add("Sets: " + homeSets.getText().toString() + " - " +visitorSets.getText().toString());
        data.add(homeTeam + " vs " + visitorTeam);
        data.add("Score: " + homScore + " - " + visScore);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("*/*");
        sendIntent.putExtra(Intent.EXTRA_TEXT, data.get(0) + "\n " + data.get(1) + "\n" + data.get(2)+ "\n" + data.get(3));
       // sendIntent.setPackage("com.facebook.orca");
        //sendIntent.setPackage("com.facebook.katana");
        super.onPause();
        startActivity(sendIntent);



    }



    //share screenshot image
    public void shareImage(){

        // image naming and path  to include sd card  appending name you choose for file
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "screenShot.jpeg";

// create bitmap screen capture
        Bitmap bitmap;
       // View v1= findViewById(R.id.image);
         View v1 = getWindow().getDecorView().getRootView();

        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        OutputStream fout = null;
        imageFile = new File(mPath);

        try {
            fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //sharing
        Uri uri = Uri.fromFile(new File(mPath));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_set, menu);
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

    public void newSet(){

        //initializing next set button
        nextSet = (Button) findViewById(R.id.nextSetButton);
        nextSet.setVisibility(View.VISIBLE);




        nextSet.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           setEnd=false;
                                           visitorWins=false;
                                           homeWins=false;

                                           homeScore.setEnabled(true);
                                           visitorScore.setEnabled(true);

                                           homScore = 0;
                                           homeScore.setText(String.valueOf(homScore));

                                           visScore=0;
                                           visitorScore.setText(String.valueOf(visScore));

                                           homeBall.setVisibility(View.INVISIBLE);
                                           visitorBall.setVisibility(View.INVISIBLE);

                                          //reseting points per set to the value that the user entered in the beggining
                                           pointsPerSet = pointsPerSetReset;

                                           //handling ties and number of sets to find out the game winner
                                           int numHomeSets = Integer.valueOf(homeSets.getText().toString());
                                           int numVisitorSets = Integer.valueOf(visitorSets.getText().toString());

                                           if(numHomeSets == numOfSets && numVisitorSets == 0){
                                               //home is the winner
                                               Toast.makeText(getApplicationContext(),"WINNER: " + homeTeam,Toast.LENGTH_LONG).show();
                                               homeScore.setEnabled(false);
                                               visitorScore.setEnabled(false);

                                           }
                                           else if (numVisitorSets == numOfSets && numHomeSets == 0){
                                               //visitor is the winner
                                               Toast.makeText(getApplicationContext(),"WINNER: " + visitorTeam,Toast.LENGTH_LONG).show();
                                               homeScore.setEnabled(false);
                                               visitorScore.setEnabled(false);
                                           }
                                           if (numHomeSets == 1 && numVisitorSets == 1){
                                               //we have a tiebreak
                                               pointsPerSet = Integer.valueOf(tieBreakPoints);
                                               Toast.makeText(getApplicationContext(),"Tie Break!",Toast.LENGTH_LONG).show();
                                           }
                                           if(numHomeSets == 2 && numVisitorSets == 1){
                                               Toast.makeText(getApplicationContext(),"WINNER: " + homeTeam,Toast.LENGTH_LONG).show();
                                               homeScore.setEnabled(false);
                                               visitorScore.setEnabled(false);

                                           }
                                           if(numHomeSets == 1 && numVisitorSets == 2){
                                               Toast.makeText(getApplicationContext(),"WINNER: " + visitorTeam,Toast.LENGTH_LONG).show();
                                               homeScore.setEnabled(false);
                                               visitorScore.setEnabled(false);

                                           }


                                           nextSet.setVisibility(View.INVISIBLE);

                                       }
                                   }
        );


    }


}
