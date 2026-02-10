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

/**
 * Activity zur Anzeige der Zookarte
 * Über Polygon-Hotspots können einzelne Gehege angeklickt werden
 * Bei Auswahl eines Geheges wird die EnclosureActivity geöffnet
 */
public class MapActivity extends AppCompatActivity {

    // ImageView für die Zookarte
    private ImageView mapImage;

    // Liste aller definierten Polygon-Hotspots (Gehege)
    private final List<PolygonHotspot> hotspots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapImage = findViewById(R.id.mapImage);

        // Zookarten-Bild setzen und skalieren
        mapImage.setImageResource(R.drawable.map_raw);
        mapImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Polygon-Gehege initialisieren
        setupPolygonHotspots();

        /**
         * Touch-Listener für die Karte
         * Ermittelt die Klickposition und prüft
         * ob ein Gehege (Polygon) getroffen wurde
         */
        mapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Nur bei Loslassen des Fingers reagieren
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return true;
                }

                // Für Accessibility (Pflicht bei TouchListener)
                v.performClick();

                // Touch-Koordinaten (View) in Bild-Koordinaten umrechnen
                float[] imgPoint = viewPointToImagePoint(
                        mapImage,
                        event.getX(),
                        event.getY()
                );

                if (imgPoint == null) {
                    return true;
                }

                // Prüfen, ob ein Gehege getroffen wurde
                int enclosureNr = findEnclosureByPoint(imgPoint[0], imgPoint[1]);

                if (enclosureNr != -1) {
                    // Gehege gefunden → EnclosureActivity öffnen
                    Intent intent = new Intent(MapActivity.this, EnclosureActivity.class);
                    intent.putExtra("enclosureNr", enclosureNr);
                    startActivity(intent);
                } else {
                    // Kein Gehege angetippt
                    Toast.makeText(MapActivity.this,
                            "Kein Gehege ausgewählt",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    /**
     * Definiert alle Polygon-Hotspots für die Gehege
     * Jedes Polygon besteht aus einer Gehege-Nummer
     * und einer Liste von Eckpunkten
     */
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

    /**
     * Prüft, welches Gehege an der angegebenen Position liegt
     *
     * @param x x-Koordinate im Bild
     * @param y y-Koordinate im Bild
     * @return Gehege-Nummer oder -1, falls kein Gehege getroffen wurde
     */
    private int findEnclosureByPoint(float x, float y) {
        for (PolygonHotspot h : hotspots) {
            if (pointInPolygon(x, y, h.points)) {
                return h.enclosureNr;
            }
        }
        return -1;
    }

    /**
     * Point-in-Polygon-Test mittels Ray-Casting-Algorithmus
     * Prüft, ob ein Punkt innerhalb eines Polygons liegt
     *
     * Quelle: https://www.youtube.com/watch?v=RSXM9bgqxJM
     */
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


    // Rechnet Touch-Koordinaten der View in tatsächliche Bild-Koordinaten um
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


    //Helper-Klasse zur Beschreibung eines Polygon-Geheges Enthält die Gehege-Nummer und die Polygonpunkte
    private static class PolygonHotspot {
        final int enclosureNr;
        final List<PointF> points;

        PolygonHotspot(int enclosureNr, List<PointF> points) {
            this.enclosureNr = enclosureNr;
            this.points = points;
        }
    }
}
