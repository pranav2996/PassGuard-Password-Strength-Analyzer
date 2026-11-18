import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/** The dedicated password-entry and strength-result page. */
final class AnalyzerPage extends JPanel {
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox showPassword = new JCheckBox("Show password");
    private final JLabel strengthLabel = GlassUI.title("Not analysed", 28);
    private final JLabel scoreLabel = GlassUI.muted("SCORE  ·  0 / 7", 12);
    private final JLabel scoreIcon = new JLabel(new GlyphIcon("shield", 56, new Color(159, 183, 255, 90)));
    private final ScoreBar progress = new ScoreBar();
    private final JLabel[] checklist = new JLabel[5];
    private final Consumer<PasswordSecurity.Analysis> analysisListener;

    AnalyzerPage(Consumer<PasswordSecurity.Analysis> analysisListener) {
        this.analysisListener = analysisListener;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createInputCard());
        add(Box.createVerticalStrut(17));
        AdaptivePair results = new AdaptivePair(17, 790, createStrengthCard(), createChecklistCard());
        results.setAlignmentX(Component.LEFT_ALIGNMENT);
        results.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        add(results);
        showEmptyResult();
    }

    void reset() {
        passwordField.setText("");
        showPassword.setSelected(false);
        passwordField.setEchoChar('•');
        showEmptyResult();
    }

    private GlassCard createInputCard() {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("analyse", "Analyse your password"));
        card.add(Box.createVerticalStrut(5));
        card.add(GlassUI.muted("Enter a password below to check its strength locally.", 12));
        card.add(Box.createVerticalStrut(18));
        JLabel fieldLabel = GlassUI.title("PASSWORD", 11);
        fieldLabel.setForeground(new Color(220, 228, 248));
        card.add(fieldLabel);
        card.add(Box.createVerticalStrut(8));
        GlassUI.styleField(passwordField);
        passwordField.setEchoChar('•');
        passwordField.setPreferredSize(new Dimension(300, 48));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(event -> analyse());
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        GlassUI.styleCheckBox(showPassword);
        showPassword.addActionListener(event -> passwordField.setEchoChar(
                showPassword.isSelected() ? (char) 0 : '•'));
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(showPassword);
        card.add(Box.createVerticalStrut(14));
        JPanel buttons = GlassUI.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        GlassButton analyse = new GlassButton("Analyse Password", true);
        analyse.setIcon(new GlyphIcon("analyse", 17, GlassUI.WHITE));
        GlassButton clear = new GlassButton("Clear", false);
        clear.setIcon(new GlyphIcon("spark", 17, GlassUI.WHITE));
        analyse.addActionListener(event -> analyse());
        clear.addActionListener(event -> {
            reset();
            analysisListener.accept(null);
        });
        buttons.add(analyse);
        buttons.add(clear);
        card.add(buttons);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 258));
        return card;
    }

    private GlassCard createStrengthCard() {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("insights", "Strength Overview"));
        card.add(Box.createVerticalStrut(4));
        card.add(GlassUI.muted("Your local password estimate at a glance.", 12));
        card.add(Box.createVerticalStrut(20));
        JPanel rating = GlassUI.transparent(new BorderLayout(8, 0));
        rating.add(strengthLabel, BorderLayout.CENTER);
        rating.add(scoreIcon, BorderLayout.EAST);
        rating.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(rating);
        card.add(Box.createVerticalStrut(3));
        card.add(scoreLabel);
        card.add(Box.createVerticalStrut(14));
        progress.setAlignmentX(Component.LEFT_ALIGNMENT);
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 13));
        card.add(progress);
        card.add(Box.createVerticalStrut(9));
        card.add(GlassUI.muted("An educational estimate, not a security guarantee.", 11));
        return card;
    }

    private GlassCard createChecklistCard() {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("checklist", "Security Checklist"));
        card.add(Box.createVerticalStrut(4));
        card.add(GlassUI.muted("Five useful password characteristics.", 12));
        card.add(Box.createVerticalStrut(18));
        String[] labels = {"At least 8 characters", "Uppercase letter", "Lowercase letter",
                "Number", "Special character"};
        for (int i = 0; i < labels.length; i++) {
            checklist[i] = new JLabel("○   " + labels[i]);
            checklist[i].setFont(GlassUI.BODY);
            checklist[i].setForeground(GlassUI.MUTED);
            checklist[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(checklist[i]);
            if (i < labels.length - 1) card.add(Box.createVerticalStrut(9));
        }
        return card;
    }

    private void analyse() {
        char[] entered = passwordField.getPassword();
        if (entered.length == 0) {
            showEmptyResult();
            analysisListener.accept(null);
            return;
        }
        PasswordSecurity.Analysis analysis;
        try {
            analysis = PasswordSecurity.analyze(new String(entered));
        } finally {
            Arrays.fill(entered, '\0');
        }
        updateResult(analysis);
        analysisListener.accept(analysis);
    }

    private void updateResult(PasswordSecurity.Analysis analysis) {
        strengthLabel.setText(analysis.strength);
        Color strengthColor = analysis.score <= 2 ? GlassUI.RED
                : analysis.score <= 4 ? GlassUI.ORANGE : GlassUI.GREEN;
        strengthLabel.setForeground(strengthColor);
        scoreLabel.setText("SCORE  ·  " + analysis.score + " / 7");
        scoreIcon.setIcon(new GlyphIcon("shield", 56, new Color(analysis.score <= 2 ? GlassUI.RED.getRGB()
                : GlassUI.GREEN.getRGB(), true)));
        progress.setScore(analysis.score, strengthColor);
        boolean[] checks = {analysis.length8, analysis.upper, analysis.lower,
                analysis.digit, analysis.special};
        for (int i = 0; i < checks.length; i++) {
            String old = checklist[i].getText();
            String text = old.length() > 4 ? old.substring(4) : old;
            checklist[i].setText((checks[i] ? "✓" : "○") + "   " + text);
            checklist[i].setForeground(checks[i] ? GlassUI.GREEN : GlassUI.MUTED);
        }
    }

    private void showEmptyResult() {
        strengthLabel.setText("Not analysed");
        strengthLabel.setForeground(GlassUI.ORANGE);
        scoreLabel.setText("SCORE  ·  0 / 7");
        scoreIcon.setIcon(new GlyphIcon("shield", 56, new Color(159, 183, 255, 90)));
        progress.setScore(0, GlassUI.CYAN);
        String[] labels = {"At least 8 characters", "Uppercase letter", "Lowercase letter",
                "Number", "Special character"};
        for (int i = 0; i < checklist.length; i++) {
            checklist[i].setText("○   " + labels[i]);
            checklist[i].setForeground(GlassUI.MUTED);
        }
    }
}
