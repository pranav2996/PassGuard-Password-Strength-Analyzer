import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Shared colors and reusable glass-style Swing components. */
final class GlassUI {
    static final Color WHITE = new Color(244, 247, 255);
    static final Color MUTED = new Color(190, 202, 230);
    static final Color PURPLE = new Color(166, 98, 255);
    static final Color PURPLE_LIGHT = new Color(199, 135, 255);
    static final Color CYAN = new Color(54, 211, 255);
    static final Color GREEN = new Color(45, 235, 155);
    static final Color ORANGE = new Color(255, 185, 85);
    static final Color RED = new Color(255, 101, 126);
    static final Color CARD = new Color(18, 30, 64, 194);
    static final Color BORDER = new Color(164, 186, 255, 74);
    static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    private GlassUI() { }

    static JPanel transparent(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    static JLabel title(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static JLabel muted(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static GlassCard card(String heading, String subtitle) {
        GlassCard card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));
        if (heading != null && !heading.isBlank()) {
            card.add(title(heading, 18));
            if (subtitle != null && !subtitle.isBlank()) {
                card.add(Box.createVerticalStrut(5));
                card.add(muted(subtitle, 12));
            }
            card.add(Box.createVerticalStrut(18));
        }
        return card;
    }

    static void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(WHITE);
        field.setCaretColor(WHITE);
        field.setBackground(new Color(255, 255, 255, 24));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(147, 183, 255, 118), 1),
                new EmptyBorder(12, 14, 12, 14)));
        field.setOpaque(true);
    }

    static void styleCheckBox(JCheckBox checkBox) {
        checkBox.setForeground(WHITE);
        checkBox.setBackground(new Color(0, 0, 0, 0));
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkBox.setFocusPainted(false);
        checkBox.setOpaque(false);
    }

    static JLabel iconHeading(String icon, String text) {
        JLabel label = new JLabel(text, new GlyphIcon(icon, 22, PURPLE_LIGHT), SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(WHITE);
        label.setIconTextGap(12);
        return label;
    }
}

final class GlassCard extends JPanel {
    GlassCard() { setOpaque(false); }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        Shape shape = new RoundRectangle2D.Float(1, 2, w - 3, h - 5, 22, 22);
        g.setColor(new Color(1, 7, 28, 72));
        g.fill(new RoundRectangle2D.Float(0, 5, w, h - 5, 24, 24));
        g.setPaint(new GradientPaint(0, 0, new Color(47, 70, 125, 198),
                w, h, new Color(19, 26, 61, 205)));
        g.fill(shape);
        g.setColor(GlassUI.BORDER);
        g.setStroke(new BasicStroke(1.15f));
        g.draw(shape);
        g.setPaint(new GradientPaint(8, 4, new Color(198, 157, 255, 60),
                Math.max(9, w - 8), 8, new Color(72, 211, 255, 24)));
        g.setStroke(new BasicStroke(1.4f));
        g.draw(new RoundRectangle2D.Float(2, 2, w - 5, Math.max(10, h - 6), 21, 21));
        g.dispose();
        super.paintComponent(graphics);
    }
}

final class GlassButton extends JButton {
    private final boolean primary;

    GlassButton(String text, boolean primary) {
        super(text);
        this.primary = primary;
        setForeground(GlassUI.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(12, 19, 12, 19));
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int inset = 1;
        Color top = primary ? new Color(203, 126, 255) : new Color(75, 94, 151, 220);
        Color bottom = primary ? new Color(128, 66, 246) : new Color(44, 57, 105, 230);
        if (!isEnabled()) {
            top = new Color(94, 101, 128);
            bottom = new Color(61, 68, 94);
        } else if (getModel().isPressed()) {
            top = top.darker();
            bottom = bottom.darker();
        } else if (getModel().isRollover()) {
            top = top.brighter();
            bottom = bottom.brighter();
        }
        g.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g.fillRoundRect(inset, inset, getWidth() - 2 * inset, getHeight() - 2 * inset, 16, 16);
        if (primary && isEnabled()) {
            g.setColor(new Color(184, 99, 255, 58));
            g.setStroke(new BasicStroke(7f));
            g.drawRoundRect(3, 4, getWidth() - 7, getHeight() - 8, 16, 16);
        }
        g.setColor(new Color(224, 222, 255, 86));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
        g.dispose();
        super.paintComponent(graphics);
    }
}

final class GlyphIcon implements Icon {
    private final String kind;
    private final int size;
    private final Color color;

    GlyphIcon(String kind, int size, Color color) {
        this.kind = kind;
        this.size = size;
        this.color = color;
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }

    @Override public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.scale(size / 24.0, size / 24.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (kind) {
            case "analyse":
                g.drawOval(3, 3, 13, 13); g.drawLine(14, 14, 21, 21);
                g.drawLine(8, 9, 11, 9); g.drawLine(10, 8, 10, 11); break;
            case "insights":
                g.drawLine(4, 20, 21, 20); g.drawRoundRect(6, 12, 3, 8, 1, 1);
                g.drawRoundRect(11, 7, 3, 13, 1, 1); g.drawRoundRect(16, 3, 3, 17, 1, 1); break;
            case "generate":
                g.drawLine(12, 2, 12, 9); g.drawLine(9, 6, 15, 6);
                g.drawLine(19, 13, 19, 21); g.drawLine(15, 17, 23, 17);
                g.drawLine(5, 14, 5, 20); g.drawLine(2, 17, 8, 17); break;
            case "shield": {
                Path2D p = new Path2D.Float(); p.moveTo(12, 2); p.lineTo(21, 5); p.lineTo(20, 13);
                p.curveTo(19, 18, 15, 21, 12, 22); p.curveTo(9, 21, 5, 18, 4, 13); p.lineTo(3, 5); p.closePath();
                g.draw(p); g.drawRoundRect(9, 10, 6, 5, 1, 1); g.drawArc(10, 7, 4, 6, 0, 180); break;
            }
            case "checklist":
                g.drawRoundRect(5, 3, 15, 19, 2, 2); g.drawLine(9, 8, 11, 10); g.drawLine(11, 10, 15, 6);
                g.drawLine(9, 15, 11, 17); g.drawLine(11, 17, 15, 13); break;
            case "key":
                g.drawOval(3, 6, 11, 11); g.drawOval(7, 10, 3, 3); g.drawLine(13, 14, 21, 21);
                g.drawLine(17, 17, 19, 15); g.drawLine(19, 19, 21, 17); break;
            case "spark":
                g.drawLine(12, 2, 12, 22); g.drawLine(2, 12, 22, 12);
                g.drawLine(5, 5, 19, 19); g.drawLine(19, 5, 5, 19); break;
            default:
                g.drawOval(5, 5, 14, 14);
        }
        g.dispose();
    }
}
