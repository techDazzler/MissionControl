package manavkang.missioncontrol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.io.Console;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SeekBar.OnSeekBarChangeListener{

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView Heading;

    //GPS location
    TextView textViewLatitude;
    TextView textViewLongitude;

    LocationManager myLocationManager;
    String PROVIDER = LocationManager.GPS_PROVIDER;

    //Seekbar
    SeekBar distance;
    TextView distance_value;

    Location location;

    float degree;

    FirebaseDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
       image = (ImageView) findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is he heading
        Heading = (TextView) findViewById(R.id.Heading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //GPS location
        textViewLatitude = (TextView) findViewById(R.id.latitude);
        textViewLongitude = (TextView) findViewById(R.id.longitude);
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = myLocationManager.getLastKnownLocation(PROVIDER);
        showMyLocation(location);

        OnSeekbarSlide();
        distance_value.setText("0");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        degree = Math.round(event.values[0]);

        Heading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

         //Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


    private LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showMyLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocationManager.removeUpdates(myLocationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocationManager.requestLocationUpdates(
                PROVIDER,     //provider
                0,       //minTime
                0,       //minDistance
                myLocationListener);  // LocationListener
    }

    private void showMyLocation(Location l){
        if(l!=null) {
            System.out.print(l.getLatitude());
            System.out.print(l.getLongitude());
            textViewLatitude.setText("Latitude: " + l.getLatitude());
            textViewLongitude.setText("Longitude: " + l.getLongitude());

        }
    }

    public void OnSeekbarSlide(){

        distance = (SeekBar)findViewById(R.id.seekBar);
        distance_value = (TextView)findViewById(R.id.distanceValue);
        distance.setOnSeekBarChangeListener(this);
        distance.incrementProgressBy(10);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        progress = progress / 10;
        progress = progress * 10;
        distance_value.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public String getDistantLocation(){
        double dist = Double.parseDouble((String) distance_value.getText());
        double brng = Math.toRadians(degree);
        double lat1 = Math.toRadians(location.getLatitude());
        double lon1 = Math.toRadians(location.getLongitude());

        double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
        double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
        System.out.println("a = " +  a);
        double lon2 = lon1 + a;

        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;

        return "Latitude = "+Math.toDegrees(lat2)+","+ "Longitude = "+Math.toDegrees(lon2);
    }

    public void buttonClicked(View v){
        String result = getDistantLocation();

        db = FirebaseDatabase.getInstance();

        // saves data in json
        DatabaseReference myRef = db.getReference(randomStringUUID());
        myRef.setValue(result);

        showToast();
    }

    // generates random uuid
    public String randomStringUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void showToast(){
        CharSequence text = "Target Location Saved Successfully";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}
