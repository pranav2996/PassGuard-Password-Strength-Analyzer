import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/** Floating three-item navigation used by the three independent pages. */
final class NavigationBar extends JPanel {
    static final String ANALYSER = "analyser";
    static final String INSIGHTS = "insights";
    static final String GENERATE = "generate";

    private final NavigationButton analyserButton = new NavigationButton("Analyser", "analyse");
    private final NavigationButton insightsButton = new NavigationButton("Insights", "insights");
    private final NavigationButton generateButton = new NavigationButton("Generate", "generate");
    private final ActionListener listener;

    NavigationBar(ActionListener listener) {
        this.listener = listener;
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        setBorder(new EmptyBorder(3, 5, 3, 5));
        add(analyserButton);
        add(insightsButton);
        add(generateButton);
        analyserButton.addActionListener(e -> select(ANALYSER));
        insightsButton.addActionListener(e -> select(INSIGHTS));
        generateButton.addActionListener(e -> select(GENERATE));
        analyserButton.setActive(true);
    }

    void select(String page) {
        analyserButton.setActive(ANALYSER.equals(page));
        insightsButton.setActive(INSIGHTS.equals(page));
        generateButton.setActive(GENERATE.equals(page));
        listener.actionPerformed(new java.awt.event.ActionEvent(this,
                java.awt.event.ActionEvent.ACTION_PERFORMED, page));
    }

    private static final class NavigationButton extends JButton {
        private boolean active;

        NavigationButton(String label, String icon) {
            super(label, new GlyphIcon(icon, 18, GlassUI.WHITE));
            setIconTextGap(9);
            setForeground(GlassUI.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = 1, y = 1, w = getWidth() - 2, h = getHeight() - 2;
            if (active) {
                g.setPaint(new GradientPaint(0, y, new Color(194, 115, 255),
                        0, y + h, new Color(109, 70, 233)));
                g.fill(new RoundRectangle2D.Float(x, y, w, h, 18, 18));
                g.setColor(new Color(235, 215, 255, 86));
                g.draw(new RoundRectangle2D.Float(x, y, w, h, 18, 18));
            } else if (getModel().isRollover()) {
                g.setColor(new Color(154, 177, 241, 28));
                g.fill(new RoundRectangle2D.Float(x, y, w, h, 18, 18));
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}

/** Adaptive two-column panel that stacks its children on compact windows. */
final class AdaptivePair extends JPanel {
    private final int gap;
    private final int horizontalThreshold;

    AdaptivePair(int gap, int horizontalThreshold, Component first, Component second) {
        this.gap = gap;
        this.horizontalThreshold = horizontalThreshold;
        setOpaque(false);
        setLayout(null);
        add(first);
        add(second);
    }

    @Override public Dimension getPreferredSize() {
        Insets in = getInsets();
        Component a = getComponent(0), b = getComponent(1);
        Dimension da = a.getPreferredSize(), db = b.getPreferredSize();
        return new Dimension(in.left + in.right + da.width + gap + db.width,
                in.top + in.bottom + Math.max(da.height, db.height));
    }

    @Override public Dimension getMinimumSize() {
        Insets in = getInsets();
        return new Dimension(in.left + in.right + 260, in.top + in.bottom + 140);
    }

    @Override public void doLayout() {
        Insets in = getInsets();
        int x = in.left, y = in.top;
        int width = Math.max(0, getWidth() - in.left - in.right);
        int availableHeight = Math.max(0, getHeight() - in.top - in.bottom);
        Component a = getComponent(0), b = getComponent(1);
        if (width >= horizontalThreshold) {
            int columnWidth = (width - gap) / 2;
            a.setBounds(x, y, columnWidth, availableHeight);
            b.setBounds(x + columnWidth + gap, y, width - columnWidth - gap, availableHeight);
        } else {
            int firstHeight = Math.max(a.getPreferredSize().height,
                    Math.max(120, (availableHeight - gap) / 2));
            int secondY = y + firstHeight + gap;
            int secondHeight = Math.max(0, y + availableHeight - secondY);
            a.setBounds(x, y, width, firstHeight);
            b.setBounds(x, secondY, width, secondHeight);
        }
    }
}

/** Animated, accessible score bar with short easing between score values. */
final class ScoreBar extends JComponent {
    private float shown;
    private float target;
    private Color accent = GlassUI.CYAN;
    private final Timer timer;

    ScoreBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 12));
        setToolTipText("Password strength score: not analysed");
        timer = new Timer(16, null);
        timer.addActionListener(event -> {
            float difference = target - shown;
            if (Math.abs(difference) < .025f) {
                shown = target;
                timer.stop();
            } else shown += difference * .22f;
            repaint();
        });
    }

    void setScore(int score, Color color) {
        target = Math.max(0, Math.min(7, score)) / 7f;
        accent = color;
        setToolTipText("Password strength score: " + score + " of 7");
        timer.start();
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g.setColor(new Color(193, 207, 255, 36));
        g.fillRoundRect(0, 0, w, h, h, h);
        int fill = Math.max(0, Math.min(w, Math.round(w * shown)));
        if (fill > 0) {
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 52));
            g.setStroke(new BasicStroke(h + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(3, h / 2, fill, h / 2);
            g.setPaint(new GradientPaint(0, 0, accent.brighter(), w, 0, accent.darker()));
            g.fillRoundRect(0, 0, fill, h, h, h);
        }
        g.dispose();
    }
}

/** CardLayout page host with a brief fade when switching modules. */
final class FadingPageHost extends JPanel {
    private final CardLayout cards = new CardLayout();
    private float alpha = 1f;
    private final Timer fadeTimer;

    FadingPageHost() {
        setLayout(cards);
        setOpaque(false);
        fadeTimer = new Timer(16, null);
        fadeTimer.addActionListener(event -> {
            alpha = Math.min(1f, alpha + .14f);
            repaint();
            if (alpha >= 1f) fadeTimer.stop();
        });
    }

    void addPage(Component page, String name) { add(page, name); }

    void showPage(String name) {
        cards.show(this, name);
        alpha = .18f;
        fadeTimer.restart();
    }

    @Override protected void paintChildren(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setComposite(AlphaComposite.SrcOver.derive(alpha));
        super.paintChildren(g);
        g.dispose();
    }
}
