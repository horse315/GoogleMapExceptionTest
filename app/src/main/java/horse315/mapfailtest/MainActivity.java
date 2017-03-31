package horse315.mapfailtest;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.io.CharStreams;
import com.solidfire.gson.Gson;
import com.solidfire.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCompatButton button2;
    private GoogleMap googleMap;

    private int lineColor;
    private int lineWidth;
    private List<LatLng> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineColor = ResourcesCompat.getColor(getResources(), R.color.lineColor, null);
        lineWidth = (int)getResources().getDimension(R.dimen.route_width);

        button2 = (AppCompatButton) findViewById(R.id.button2);
        button2.setEnabled(false);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .width(lineWidth)
                        // TODO: round breaks the application
                        .jointType(JointType.ROUND)
                        .color(lineColor);

                googleMap.addPolyline(polylineOptions);
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final String path;
        try {
            path = CharStreams.toString(new InputStreamReader(getResources().openRawResource(R.raw.path)));
            Log.d(TAG, "Stream reading done");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        points = new Gson().fromJson(path, new TypeToken<ArrayList<LatLng>>() {}.getType());
        Log.d(TAG, String.format("Loaded %d points", points.size()));

        this.googleMap = googleMap;
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 16), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                button2.setEnabled(true);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
