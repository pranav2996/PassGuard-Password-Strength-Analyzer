import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Random;

/** Lightweight Java2D ambient waves. The Swing timer runs only while visible. */
final class AnimatedBackground extends JPanel {
    private static final int PARTICLE_COUNT = 34;
    private final float[] particleX = new float[PARTICLE_COUNT];
    private final float[] particleY = new float[PARTICLE_COUNT];
    private final float[] particleSize = new float[PARTICLE_COUNT];
    private final Timer timer;
    private double phase;

    AnimatedBackground() {
        setOpaque(true);
        setLayout(new BorderLayout());
        Random random = new Random(8291L);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particleX[i] = random.nextFloat();
            particleY[i] = random.nextFloat();
            particleSize[i] = 1.2f + random.nextFloat() * 2.2f;
        }
        timer = new Timer(38, event -> {
            phase += 0.018;
            if (isShowing()) repaint();
        });
        timer.setCoalesce(true);
        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) timer.start(); else timer.stop();
            }
        });
    }

    @Override public void addNotify() {
        super.addNotify();
        if (isShowing()) timer.start();
    }

    @Override public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int width = getWidth(), height = getHeight();
        if (width <= 0 || height <= 0) return;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, new Color(5, 13, 39), width, height,
                new Color(13, 13, 43)));
        g.fillRect(0, 0, width, height);

        paintGlow(g, width, height, width * .16f, height * .22f, width * .50f,
                new Color(0, 177, 255, 52));
        paintGlow(g, width, height, width * .86f, height * .25f, width * .43f,
                new Color(139, 55, 255, 50));
        paintGlow(g, width, height, width * .58f, height * .96f, width * .52f,
                new Color(26, 83, 241, 34));

        drawWave(g, width, height, height * .33f, height * .11f, .0072, .18,
                new Color(29, 205, 255, 122));
        drawWave(g, width, height, height * .39f, height * .085f, .0085, 2.35,
                new Color(19, 157, 255, 92));
        drawWave(g, width, height, height * .36f, height * .105f, .0065, 3.7,
                new Color(194, 88, 255, 122));
        drawWave(g, width, height, height * .77f, height * .075f, .0075, 2.2,
                new Color(57, 112, 255, 86));
        drawWave(g, width, height, height * .84f, height * .095f, .0061, 4.4,
                new Color(180, 72, 255, 128));

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float drift = (float) Math.sin(phase * .7 + i * 1.6) * 4f;
            float twinkle = .45f + .55f * (float) Math.abs(Math.sin(phase + i));
            int alpha = Math.max(12, Math.min(92, (int) (72 * twinkle)));
            g.setColor(new Color(i % 3 == 0 ? 108 : 94, i % 3 == 0 ? 218 : 139,
                    255, alpha));
            float diameter = particleSize[i] * (0.8f + 0.4f * twinkle);
            g.fillOval((int) (particleX[i] * width + drift),
                    (int) (particleY[i] * height + drift * .4f),
                    Math.round(diameter), Math.round(diameter));
        }
        g.dispose();
    }

    private void paintGlow(Graphics2D g, int width, int height, float x, float y,
                           float radius, Color color) {
        if (radius <= 0) return;
        RadialGradientPaint glow = new RadialGradientPaint(new Point2D.Float(x, y), radius,
                new float[]{0f, 1f}, new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)});
        g.setPaint(glow);
        g.fillRect(0, 0, width, height);
    }

    private void drawWave(Graphics2D g, int width, int height, double baseline,
                          double amplitude, double frequency, double offset, Color color) {
        Path2D.Float line = new Path2D.Float();
        line.moveTo(0, yAt(0, baseline, amplitude, frequency, offset));
        for (int x = 8; x <= width; x += 8) {
            line.lineTo(x, yAt(x, baseline, amplitude, frequency, offset));
        }
        Path2D.Float fill = (Path2D.Float) line.clone();
        fill.lineTo(width, height);
        fill.lineTo(0, height);
        fill.closePath();
        g.setPaint(new GradientPaint(0, (float) baseline - (float) amplitude,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 0),
                0, height, new Color(color.getRed(), color.getGreen(), color.getBlue(), 17)));
        g.fill(fill);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        g.setStroke(new BasicStroke(13f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(line);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 82));
        g.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(line);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(line);
    }

    private float yAt(double x, double baseline, double amplitude, double frequency, double offset) {
        return (float) (baseline + Math.sin(x * frequency + phase + offset) * amplitude
                + Math.sin(x * frequency * 0.47 + phase * 0.68 + offset * 1.8) * amplitude * .28);
    }
}
