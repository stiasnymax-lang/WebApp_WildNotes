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

        mapImage = (ImageView) findViewById(R.id.mapImage);
        mapImage.setImageResource(R.drawable.map_raw);
        mapImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        setupPolygonHotspots();

        // Warten bis das Layout fertig ist, damit die Matrix korrekt berechnet ist
        mapImage.post(new Runnable() {
            @Override
            public void run() {
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

                        // Koordinaten außerhalb des Bildes ignorieren
                        int imgW = mapImage.getDrawable().getIntrinsicWidth();
                        int imgH = mapImage.getDrawable().getIntrinsicHeight();

                        if (imgPoint[0] < 0 || imgPoint[1] < 0
                                || imgPoint[0] > imgW || imgPoint[1] > imgH) {
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
        });
    }

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

        hotspots.add(new PolygonHotspot(
                5,
                Arrays.asList(
                        new PointF(1390, 1822),
                        new PointF(1482, 1819),
                        new PointF(1458, 1995),
                        new PointF(1379, 1995)
                )
        ));

        hotspots.add(new PolygonHotspot(
                6,
                Arrays.asList(
                        new PointF(1479, 1827),
                        new PointF(1566, 1811),
                        new PointF(1645, 1824),
                        new PointF(1718, 1871),
                        new PointF(1787, 1934),
                        new PointF(1579, 2179),
                        new PointF(1503, 2174),
                        new PointF(1442, 2119),
                        new PointF(1442, 2034),
                        new PointF(1458, 1979)
                )
        ));

        hotspots.add(new PolygonHotspot(
                7,
                Arrays.asList(
                        new PointF(1821, 2263),
                        new PointF(1621, 2463),
                        new PointF(1797, 2740),
                        new PointF(2017, 2519)
                )
        ));

        hotspots.add(new PolygonHotspot(
                8,
                Arrays.asList(
                        new PointF(1952, 2581),
                        new PointF(2092, 2716),
                        new PointF(2106, 2824),
                        new PointF(2022, 2840),
                        new PointF(1874, 2911),
                        new PointF(1797, 2742)
                )
        ));

        hotspots.add(new PolygonHotspot(
                9,
                Arrays.asList(
                        new PointF(1803, 2756),
                        new PointF(1629, 2811),
                        new PointF(1737, 3019),
                        new PointF(1889, 2948)
                )
        ));

        hotspots.add(new PolygonHotspot(
                10,
                Arrays.asList(
                        new PointF(1905, 3011),
                        new PointF(1765, 3063),
                        new PointF(1827, 3190),
                        new PointF(1974, 3142)
                )
        ));

        hotspots.add(new PolygonHotspot(
                11,
                Arrays.asList(
                        new PointF(2227, 2958),
                        new PointF(2081, 2995),
                        new PointF(2200, 3314),
                        new PointF(2327, 3257)
                )
        ));

        hotspots.add(new PolygonHotspot(
                12,
                Arrays.asList(
                        new PointF(2013, 3211),
                        new PointF(1650, 3335),
                        new PointF(1718, 3527),
                        new PointF(1895, 3479),
                        new PointF(1984, 3657),
                        new PointF(2060, 3611),
                        new PointF(1997, 3451),
                        new PointF(2097, 3411)
                )
        ));

        hotspots.add(new PolygonHotspot(
                13,
                Arrays.asList(
                        new PointF(2097, 3411),
                        new PointF(2161, 3559),
                        new PointF(2074, 3606),
                        new PointF(1997, 3451)
                )
        ));

        hotspots.add(new PolygonHotspot(
                14,
                Arrays.asList(
                        new PointF(1984, 3659),
                        new PointF(2097, 3843),
                        new PointF(2290, 3738),
                        new PointF(2161, 3559)
                )
        ));

        hotspots.add(new PolygonHotspot(
                15,
                Arrays.asList(
                        new PointF(2582, 3164),
                        new PointF(2413, 3335),
                        new PointF(2529, 3442),
                        new PointF(2685, 3259)
                )
        ));

        hotspots.add(new PolygonHotspot(
                16,
                Arrays.asList(
                        new PointF(2345, 3343),
                        new PointF(2227, 3455),
                        new PointF(2540, 3795),
                        new PointF(2666, 3669)
                )
        ));

        hotspots.add(new PolygonHotspot(
                17,
                Arrays.asList(
                        new PointF(2097, 3843),
                        new PointF(2129, 4504),
                        new PointF(2674, 4338),
                        new PointF(2298, 3738)
                )
        ));

        hotspots.add(new PolygonHotspot(
                18,
                Arrays.asList(
                        new PointF(2053, 4322),
                        new PointF(1958, 4346),
                        new PointF(2003, 4504),
                        new PointF(2111, 4497)
                )
        ));

        hotspots.add(new PolygonHotspot(
                19,
                Arrays.asList(
                        new PointF(1958, 4359),
                        new PointF(1853, 4383),
                        new PointF(1890, 4524),
                        new PointF(1997, 4504)
                )
        ));

        hotspots.add(new PolygonHotspot(
                20,
                Arrays.asList(
                        new PointF(1850, 4396),
                        new PointF(1750, 4420),
                        new PointF(1803, 4561),
                        new PointF(1890, 4524)
                )
        ));

        hotspots.add(new PolygonHotspot(
                21,
                Arrays.asList(
                        new PointF(1682, 4422),
                        new PointF(1587, 4448),
                        new PointF(1619, 4582),
                        new PointF(1734, 4561)
                )
        ));

        hotspots.add(new PolygonHotspot(
                22,
                Arrays.asList(
                        new PointF(1582, 4451),
                        new PointF(1482, 4475),
                        new PointF(1498, 4585),
                        new PointF(1619, 4582)
                )
        ));

        hotspots.add(new PolygonHotspot(
                23,
                Arrays.asList(
                        new PointF(1250, 3779),
                        new PointF(939, 3832),
                        new PointF(1116, 4658),
                        new PointF(1498, 4590),
                        new PointF(1419, 4180),
                        new PointF(1321, 4111)
                )
        ));

        hotspots.add(new PolygonHotspot(
                24,
                Arrays.asList(
                        new PointF(879, 3543),
                        new PointF(1206, 3480),
                        new PointF(1250, 3706),
                        new PointF(1247, 3779),
                        new PointF(942, 3840)
                )
        ));

        hotspots.add(new PolygonHotspot(
                25,
                Arrays.asList(
                        new PointF(766, 2898),
                        new PointF(1074, 2858),
                        new PointF(1156, 3195),
                        new PointF(795, 3248)
                )
        ));

        hotspots.add(new PolygonHotspot(
                26,
                Arrays.asList(
                        new PointF(708, 2648),
                        new PointF(911, 2598),
                        new PointF(940, 2735),
                        new PointF(1056, 2679),
                        new PointF(1090, 2806),
                        new PointF(979, 2795),
                        new PointF(979, 2866),
                        new PointF(766, 2903)
                )
        ));

        hotspots.add(new PolygonHotspot(
                27,
                Arrays.asList(
                        new PointF(1358, 3786),
                        new PointF(1379, 3879),
                        new PointF(1342, 3879),
                        new PointF(1379, 4035),
                        new PointF(1434, 4134),
                        new PointF(1513, 4066),
                        new PointF(1606, 4066),
                        new PointF(1695, 4087),
                        new PointF(1737, 4137),
                        new PointF(1797, 4038),
                        new PointF(1705, 3967),
                        new PointF(1606, 3924),
                        new PointF(1595, 3811),
                        new PointF(1563, 3811),
                        new PointF(1563, 3751),
                        new PointF(1529, 3748)
                )
        ));
    }

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

    // Touch-Koordinaten (View) in tatsächliche Bild-Koordinaten umrechnen
    // Funktioniert geräteunabhängig durch inverse Matrix-Transformation
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

    // Helper-Klasse zur Beschreibung eines Polygon-Geheges
    private static class PolygonHotspot {
        final int enclosureNr;
        final List<PointF> points;

        PolygonHotspot(int enclosureNr, List<PointF> points) {
            this.enclosureNr = enclosureNr;
            this.points = points;
        }
    }
}