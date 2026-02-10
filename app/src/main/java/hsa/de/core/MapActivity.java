package hsa.de.core;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hsa.de.R;

public class MapActivity extends AppCompatActivity {

    private ImageView mapImage;


    private final List<PolygonHotspot> hotspots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapImage = findViewById(R.id.mapImage);

        // Bild setzen
        mapImage.setImageResource(R.drawable.map_raw);
        mapImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        setupPolygonHotspots();

        // Touch-Listener
        mapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return true;
                }

                v.performClick();

                float[] imgPoint = viewPointToImagePoint(
                        mapImage,
                        event.getX(),
                        event.getY()
                );

                if (imgPoint == null) {
                    return true;
                }

                int enclosureNr = findEnclosureByPoint(imgPoint[0], imgPoint[1]);

                if (enclosureNr != -1) {
                    Intent intent = new Intent(MapActivity.this, EnclosureActivity.class);
                    intent.putExtra("enclosureNr", enclosureNr);
                    startActivity(intent);
                } else {
                    Toast.makeText(MapActivity.this,
                            "Kein Gehege ausgewählt",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    // Polygon-Gehege definieren
    private void setupPolygonHotspots() {
        hotspots.clear();

        hotspots.add(new PolygonHotspot(
                1,
                Arrays.asList(
                        new PointF(1112, 1666),
                        new PointF(787, 2068),
                        new PointF(884, 2144),
                        new PointF(1065, 1921),
                        new PointF(1115, 1942),
                        new PointF(1254, 1782)
                )
        ));

        hotspots.add(new PolygonHotspot(
                2,
                Arrays.asList(
                        new PointF(1569, 1600),
                        new PointF(1417, 1766),
                        new PointF(1485, 1816),
                        new PointF(1627, 1816),
                        new PointF(1724, 1866),
                        new PointF(1787, 1934),
                        new PointF(1900, 1824)
                )
        ));

        hotspots.add(new PolygonHotspot(
                3,
                Arrays.asList(
                        new PointF(2005, 1824),
                        new PointF(1900, 1934),
                        new PointF(2031, 2047),
                        new PointF(2131, 1908)
                )
        ));
        hotspots.add(new PolygonHotspot(
                4,
                Arrays.asList(
                        new PointF(2367, 1622),
                        new PointF(2136, 1913),
                        new PointF(2031, 2047),
                        new PointF(1885, 2207),
                        new PointF(2031, 2388),
                        new PointF(2504, 1845)
                )
        ));
    }

    // Prüft, welches Gehege angeklickt wurde
    private int findEnclosureByPoint(float x, float y) {
        for (PolygonHotspot h : hotspots) {
            if (pointInPolygon(x, y, h.points)) {
                return h.enclosureNr;
            }
        }
        return -1;
    }

    // Point-in-Polygon (Ray Casting)
    private boolean pointInPolygon(float x, float y, List<PointF> poly) {
        boolean inside = false;
        int n = poly.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            float xi = poly.get(i).x;
            float yi = poly.get(i).y;
            float xj = poly.get(j).x;
            float yj = poly.get(j).y;

            boolean intersect =
                    ((yi > y) != (yj > y)) &&
                            (x < (xj - xi) * (y - yi) / (yj - yi) + xi);

            if (intersect) {
                inside = !inside;
            }
        }
        return inside;
    }

    // View-Koordinaten → Bild-Koordinaten
    private float[] viewPointToImagePoint(ImageView imageView, float vx, float vy) {
        if (imageView.getDrawable() == null) {
            return null;
        }

        Matrix inverse = new Matrix();
        imageView.getImageMatrix().invert(inverse);

        float[] pts = new float[]{vx, vy};
        inverse.mapPoints(pts);
        return pts;
    }

    // Helper-Klasse für Polygon-Gehege
    private static class PolygonHotspot {
        final int enclosureNr;
        final List<PointF> points;

        PolygonHotspot(int enclosureNr, List<PointF> points) {
            this.enclosureNr = enclosureNr;
            this.points = points;
        }
    }
}
