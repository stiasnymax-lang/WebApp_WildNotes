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
                        new PointF(528, 692),
                        new PointF(563, 691),
                        new PointF(554, 758),
                        new PointF(524, 758)
                )
        ));

        hotspots.add(new PolygonHotspot(
                6,
                Arrays.asList(
                        new PointF(562, 694),
                        new PointF(595, 688),
                        new PointF(625, 693),
                        new PointF(653, 711),
                        new PointF(679, 735),
                        new PointF(600, 828),
                        new PointF(571, 826),
                        new PointF(548, 805),
                        new PointF(548, 773),
                        new PointF(554, 752)
                )
        ));

        hotspots.add(new PolygonHotspot(
                7,
                Arrays.asList(
                        new PointF(692, 860),
                        new PointF(616, 936),
                        new PointF(683, 1041),
                        new PointF(767, 957)
                )
        ));

        hotspots.add(new PolygonHotspot(
                8,
                Arrays.asList(
                        new PointF(742, 981),
                        new PointF(795, 1032),
                        new PointF(800, 1073),
                        new PointF(768, 1079),
                        new PointF(712, 1106),
                        new PointF(683, 1042)
                )
        ));

        hotspots.add(new PolygonHotspot(
                9,
                Arrays.asList(
                        new PointF(685, 1047),
                        new PointF(619, 1068),
                        new PointF(660, 1147),
                        new PointF(717, 1120)
                )
        ));

        hotspots.add(new PolygonHotspot(
                10,
                Arrays.asList(
                        new PointF(724, 1144),
                        new PointF(671, 1164),
                        new PointF(694, 1212),
                        new PointF(750, 1194)
                )
        ));

        hotspots.add(new PolygonHotspot(
                11,
                Arrays.asList(
                        new PointF(846, 1124),
                        new PointF(791, 1138),
                        new PointF(836, 1259),
                        new PointF(884, 1237)
                )
        ));

        hotspots.add(new PolygonHotspot(
                12,
                Arrays.asList(
                        new PointF(765, 1220),
                        new PointF(627, 1267),
                        new PointF(653, 1340),
                        new PointF(720, 1322),
                        new PointF(754, 1389),
                        new PointF(783, 1372),
                        new PointF(759, 1311),
                        new PointF(797, 1296)
                )
        ));

        hotspots.add(new PolygonHotspot(
                13,
                Arrays.asList(
                        new PointF(797, 1296),
                        new PointF(821, 1352),
                        new PointF(788, 1370),
                        new PointF(759, 1311)
                )
        ));

        hotspots.add(new PolygonHotspot(
                14,
                Arrays.asList(
                        new PointF(754, 1390),
                        new PointF(797, 1460),
                        new PointF(870, 1420),
                        new PointF(821, 1352)
                )
        ));

        hotspots.add(new PolygonHotspot(
                15,
                Arrays.asList(
                        new PointF(981, 1202),
                        new PointF(917, 1267),
                        new PointF(961, 1308),
                        new PointF(1020, 1238)
                )
        ));

        hotspots.add(new PolygonHotspot(
                16,
                Arrays.asList(
                        new PointF(891, 1270),
                        new PointF(846, 1313),
                        new PointF(965, 1442),
                        new PointF(1013, 1393)
                )
        ));

        hotspots.add(new PolygonHotspot(
                17,
                Arrays.asList(
                        new PointF(797, 1461),
                        new PointF(809, 1712),
                        new PointF(1016, 1648),
                        new PointF(873, 1420)
                )
        ));

        hotspots.add(new PolygonHotspot(
                18,
                Arrays.asList(
                        new PointF(780, 1642),
                        new PointF(744, 1651),
                        new PointF(761, 1712),
                        new PointF(802, 1709)
                )
        ));

        hotspots.add(new PolygonHotspot(
                19,
                Arrays.asList(
                        new PointF(744, 1656),
                        new PointF(704, 1665),
                        new PointF(718, 1718),
                        new PointF(759, 1712)
                )
        ));

        hotspots.add(new PolygonHotspot(
                20,
                Arrays.asList(
                        new PointF(703, 1670),
                        new PointF(665, 1679),
                        new PointF(685, 1733),
                        new PointF(718, 1718)
                )
        ));

        hotspots.add(new PolygonHotspot(
                21,
                Arrays.asList(
                        new PointF(639, 1680),
                        new PointF(603, 1691),
                        new PointF(615, 1741),
                        new PointF(659, 1733)
                )
        ));

        hotspots.add(new PolygonHotspot(
                22,
                Arrays.asList(
                        new PointF(601, 1691),
                        new PointF(563, 1701),
                        new PointF(569, 1744),
                        new PointF(615, 1741)
                )
        ));

        hotspots.add(new PolygonHotspot(
                23,
                Arrays.asList(
                        new PointF(475, 1436),
                        new PointF(357, 1457),
                        new PointF(424, 1770),
                        new PointF(569, 1746),
                        new PointF(539, 1588),
                        new PointF(502, 1562)
                )
        ));

        hotspots.add(new PolygonHotspot(
                24,
                Arrays.asList(
                        new PointF(334, 1346),
                        new PointF(458, 1323),
                        new PointF(475, 1408),
                        new PointF(474, 1437),
                        new PointF(358, 1460)
                )
        ));

        hotspots.add(new PolygonHotspot(
                25,
                Arrays.asList(
                        new PointF(291, 1101),
                        new PointF(408, 1086),
                        new PointF(439, 1214),
                        new PointF(302, 1234)
                )
        ));

        hotspots.add(new PolygonHotspot(
                26,
                Arrays.asList(
                        new PointF(269, 1006),
                        new PointF(346, 987),
                        new PointF(357, 1039),
                        new PointF(401, 1018),
                        new PointF(414, 1066),
                        new PointF(372, 1062),
                        new PointF(372, 1089),
                        new PointF(291, 1103)
                )
        ));

        hotspots.add(new PolygonHotspot(
                27,
                Arrays.asList(
                        new PointF(516, 1439),
                        new PointF(524, 1474),
                        new PointF(510, 1474),
                        new PointF(524, 1533),
                        new PointF(545, 1571),
                        new PointF(575, 1545),
                        new PointF(610, 1545),
                        new PointF(644, 1553),
                        new PointF(660, 1572),
                        new PointF(683, 1536),
                        new PointF(648, 1507),
                        new PointF(610, 1492),
                        new PointF(606, 1449),
                        new PointF(594, 1449),
                        new PointF(594, 1426),
                        new PointF(581, 1425)
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