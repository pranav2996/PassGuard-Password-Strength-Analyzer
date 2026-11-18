import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

public class PasswordAnalyzer {

    // -------------------- COLORS --------------------

    private static final Color BACKGROUND = new Color(11, 18, 32);
    private static final Color SIDEBAR = new Color(15, 25, 43);
    private static final Color CARD = new Color(22, 34, 54);
    private static final Color CARD_LIGHT = new Color(29, 44, 67);
    private static final Color BORDER = new Color(44, 62, 88);
    private static final Color WHITE = new Color(241, 245, 249);
    private static final Color MUTED = new Color(148, 163, 184);
    private static final Color BLUE = new Color(59, 130, 246);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color ORANGE = new Color(245, 158, 11);
    private static final Color RED = new Color(239, 68, 68);

    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Compatibility entry point for the original fixed-length generator.

    public static String generatePassword() {
        return PasswordSecurity.generatePassword(16, true, true, true, true);
    }

    public static boolean isCommonPassword(String password) {
        return PasswordSecurity.isCommonPassword(password);
    }

    public static boolean hasRepeatedCharacters(String password) {
        return PasswordSecurity.hasRepeatedCharacters(password);
    }

    public static boolean hasSequentialPattern(String password) {
        return PasswordSecurity.hasSequentialPattern(password);
    }

    // -------------------- UI HELPERS --------------------

    private static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class ModernButton extends JButton {
        private final Color buttonColor;

        ModernButton(String text, Color color) {
            super(text);
            buttonColor = color;
            setForeground(Color.WHITE);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 19, 12, 19));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isPressed()
                    ? buttonColor.darker()
                    : getModel().isRollover() ? buttonColor.brighter() : buttonColor;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel mutedText(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static RoundedPanel createCard(String title, String description) {
        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));
        card.add(heading(title, 18));
        if (description != null && !description.isEmpty()) {
            card.add(Box.createVerticalStrut(5));
            card.add(mutedText(description, 12));
        }
        card.add(Box.createVerticalStrut(19));
        return card;
    }

    private static void addCard(JPanel parent, Component component,
                                int gridX, int gridY, double weightX) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = weightX;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 18, 0);
        parent.add(component, gbc);
    }

    private static JPanel transparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static void styleCheckLabel(JLabel label) {
        label.setFont(FONT);
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // -------------------- APPLICATION --------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PassGuard | Password Strength Analyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1120, 900);
            frame.setMinimumSize(new Dimension(720, 620));
            frame.getContentPane().setBackground(BACKGROUND);

            JPanel page = new JPanel(new BorderLayout(0, 22));
            page.setBackground(BACKGROUND);
            page.setBorder(new EmptyBorder(28, 34, 28, 34));

            // Header
            JPanel header = transparentPanel(new BorderLayout());
            JPanel brand = transparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

            RoundedPanel logo = new RoundedPanel(14);
            logo.setBackground(BLUE);
            logo.setLayout(new GridBagLayout());
            logo.setPreferredSize(new Dimension(48, 48));
            JLabel logoText = new JLabel("PG");
            logoText.setFont(new Font("Segoe UI", Font.BOLD, 17));
            logoText.setForeground(Color.WHITE);
            logo.add(logoText);

            JPanel brandText = transparentPanel(new GridLayout(2, 1, 0, 2));
            JLabel brandName = heading("PASSGUARD", 17);
            JLabel brandSub = mutedText("PASSWORD SECURITY CENTER", 10);
            brandText.add(brandName);
            brandText.add(brandSub);

            brand.add(logo);
            brand.add(Box.createHorizontalStrut(13));
            brand.add(brandText);
            header.add(brand, BorderLayout.WEST);

            RoundedPanel status = new RoundedPanel(18);
            status.setBackground(new Color(18, 63, 57));
            status.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
            JLabel statusDot = new JLabel("●");
            statusDot.setForeground(GREEN);
            JLabel statusText = new JLabel("LOCAL ANALYSIS");
            statusText.setForeground(new Color(167, 243, 208));
            statusText.setFont(new Font("Segoe UI", Font.BOLD, 10));
            status.add(statusDot);
            status.add(statusText);
            header.add(status, BorderLayout.EAST);

            // Hero
            JPanel hero = transparentPanel(new BorderLayout());
            JPanel heroText = transparentPanel(new GridLayout(2, 1, 0, 7));
            heroText.add(heading("Password Strength Analyzer", 27));
            heroText.add(mutedText(
                    "Evaluate password security and create stronger alternatives.", 14));
            hero.add(heroText, BorderLayout.WEST);

            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);

            // Left column: password input
            RoundedPanel inputCard = createCard(
                    "Analyze your password",
                    "Enter a password below to check its security strength.");

            JLabel passwordLabel = heading("PASSWORD", 11);
            passwordLabel.setForeground(new Color(191, 204, 222));
            inputCard.add(passwordLabel);
            inputCard.add(Box.createVerticalStrut(9));

            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            passwordField.setForeground(WHITE);
            passwordField.setCaretColor(WHITE);
            passwordField.setBackground(CARD_LIGHT);
            passwordField.setEchoChar('•');
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(12, 14, 12, 14)));
            passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
            inputCard.add(passwordField);
            inputCard.add(Box.createVerticalStrut(8));

            JCheckBox showPassword = new JCheckBox("Show password");
            showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            showPassword.setForeground(MUTED);
            showPassword.setBackground(CARD);
            showPassword.setFocusPainted(false);
            showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
            showPassword.addActionListener(e -> {
                if (showPassword.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('•');
                }
            });
            inputCard.add(showPassword);
            inputCard.add(Box.createVerticalStrut(16));

            JPanel inputButtons = transparentPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
            inputButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
            ModernButton analyzeButton = new ModernButton("Analyze Password", BLUE);
            ModernButton clearButton = new ModernButton("Clear", new Color(71, 85, 105));
            inputButtons.add(analyzeButton);
            inputButtons.add(clearButton);
            inputCard.add(inputButtons);

            // Result card
            RoundedPanel resultCard = createCard(
                    "Strength overview",
                    "Your password security score at a glance.");

            JLabel resultLabel = heading("Not Analyzed", 25);
            resultLabel.setForeground(ORANGE);
            resultCard.add(resultLabel);
            resultCard.add(Box.createVerticalStrut(8));

            JLabel scoreLabel = mutedText("SCORE  •  0 / 7", 12);
            resultCard.add(scoreLabel);
            resultCard.add(Box.createVerticalStrut(13));

            JProgressBar progressBar = new JProgressBar(0, 7);
            progressBar.setValue(0);
            progressBar.setStringPainted(false);
            progressBar.setForeground(BLUE);
            progressBar.setBackground(CARD_LIGHT);
            progressBar.setBorderPainted(false);
            progressBar.setPreferredSize(new Dimension(100, 10));
            progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
            progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultCard.add(progressBar);
            resultCard.add(Box.createVerticalStrut(8));
            resultCard.add(mutedText("An educational estimate based on length, variety and patterns.", 11));

            // Checklist card
            RoundedPanel checklistCard = createCard(
                    "Security checklist",
                    "Five essential password requirements.");

            JLabel lengthLabel = new JLabel("○   At least 8 characters");
            JLabel upperLabel = new JLabel("○   Uppercase letter");
            JLabel lowerLabel = new JLabel("○   Lowercase letter");
            JLabel digitLabel = new JLabel("○   Number");
            JLabel specialLabel = new JLabel("○   Special character");

            JLabel[] checklist = {
                    lengthLabel, upperLabel, lowerLabel, digitLabel, specialLabel
            };

            for (JLabel label : checklist) {
                styleCheckLabel(label);
                checklistCard.add(label);
                checklistCard.add(Box.createVerticalStrut(12));
            }

            // Right column: warnings and tips
            RoundedPanel tipsCard = createCard(
                    "Security insights",
                    "Warnings and suggestions to improve your password.");

            JLabel warningLabel = new JLabel(" ");
            warningLabel.setFont(FONT_BOLD);
            warningLabel.setForeground(RED);
            warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            tipsCard.add(warningLabel);
            tipsCard.add(Box.createVerticalStrut(10));

            JTextArea tipsArea = new JTextArea(
                    "Analyze a password to see personalized suggestions.");
            tipsArea.setEditable(false);
            tipsArea.setFocusable(false);
            tipsArea.setLineWrap(true);
            tipsArea.setWrapStyleWord(true);
            tipsArea.setRows(6);
            tipsArea.setFont(FONT);
            tipsArea.setForeground(MUTED);
            tipsArea.setBackground(CARD);
            tipsArea.setBorder(null);

            JScrollPane tipsScroll = new JScrollPane(tipsArea);
            tipsScroll.setBorder(null);
            tipsScroll.setBackground(CARD);
            tipsScroll.getViewport().setBackground(CARD);
            tipsScroll.setPreferredSize(new Dimension(350, 135));
            tipsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            tipsCard.add(tipsScroll);

            // Generator card
            RoundedPanel generatorCard = createCard(
                    "Strong password generator",
                    "Choose a length and the character groups to include.");

            JPanel generatorOptions = transparentPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
            generatorOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
            JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 128, 1));
            generatorOptions.add(mutedText("Length", 12));
            generatorOptions.add(lengthSpinner);
            JCheckBox upperOption = new JCheckBox("A–Z", true);
            JCheckBox lowerOption = new JCheckBox("a–z", true);
            JCheckBox digitOption = new JCheckBox("0–9", true);
            JCheckBox specialOption = new JCheckBox("Symbols", true);
            for (JCheckBox option : new JCheckBox[]{upperOption, lowerOption, digitOption, specialOption}) {
                option.setForeground(MUTED);
                option.setBackground(CARD);
                option.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                option.setFocusPainted(false);
                generatorOptions.add(option);
            }
            generatorCard.add(generatorOptions);
            generatorCard.add(Box.createVerticalStrut(12));

            JTextField generatedLabel = new JTextField(
                    "Generate a password to preview it here.");
            generatedLabel.setFont(new Font("Consolas", Font.BOLD, 14));
            generatedLabel.setForeground(GREEN);
            generatedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            generatedLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(12, 12, 12, 12)));
            generatedLabel.setEditable(false);
            generatedLabel.setFocusable(false);
            generatedLabel.setOpaque(true);
            generatedLabel.setBackground(CARD_LIGHT);
            generatedLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            generatorCard.add(generatedLabel);
            generatorCard.add(Box.createVerticalStrut(14));

            JPanel generatorButtons = transparentPanel(
                    new FlowLayout(FlowLayout.LEFT, 9, 0));
            generatorButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
            ModernButton generateButton = new ModernButton(
                    "Generate password", GREEN.darker());
            ModernButton copyButton = new ModernButton("Copy password", BLUE);
            copyButton.setEnabled(false);
            generatorButtons.add(generateButton);
            generatorButtons.add(copyButton);
            generatorCard.add(generatorButtons);
            JLabel clipboardFeedback = mutedText("", 11);
            generatorCard.add(Box.createVerticalStrut(8));
            generatorCard.add(clipboardFeedback);

            final String[] generatedPassword = {""};

            generateButton.addActionListener(e -> {
                try {
                    generatedPassword[0] = PasswordSecurity.generatePassword(
                            (Integer) lengthSpinner.getValue(), upperOption.isSelected(),
                            lowerOption.isSelected(), digitOption.isSelected(), specialOption.isSelected());
                    generatedLabel.setText(generatedPassword[0]);
                    copyButton.setEnabled(true);
                    clipboardFeedback.setText("Generated locally.");
                } catch (IllegalArgumentException ex) {
                    generatedPassword[0] = "";
                    generatedLabel.setText("Select at least one character group.");
                    copyButton.setEnabled(false);
                    clipboardFeedback.setText(ex.getMessage());
                }
            });

            copyButton.addActionListener(e -> {
                if (!generatedPassword[0].isEmpty()) {
                    StringSelection selection =
                            new StringSelection(generatedPassword[0]);
                    try {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                        clipboardFeedback.setText("Copied. The system clipboard may retain it until replaced.");
                    } catch (HeadlessException | IllegalStateException ex) {
                        clipboardFeedback.setText("Clipboard is unavailable or busy.");
                    } catch (SecurityException ex) {
                        clipboardFeedback.setText("Clipboard access was denied.");
                    }
                } else {
                    clipboardFeedback.setText("Generate a password first.");
                }
            });

            // Privacy note
            JPanel privacy = transparentPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
            JLabel privacyIcon = new JLabel("●");
            privacyIcon.setForeground(GREEN);
            JLabel privacyText = mutedText(
                    "Privacy first: passwords are analyzed locally and are not saved or sent anywhere.",
                    11);
            privacy.add(privacyIcon);
            privacy.add(privacyText);

            // Assemble two-column layout
            JPanel leftColumn = transparentPanel(new GridBagLayout());
            addCard(leftColumn, inputCard, 0, 0, 1);
            addCard(leftColumn, resultCard, 0, 1, 1);
            addCard(leftColumn, checklistCard, 0, 2, 1);

            JPanel rightColumn = transparentPanel(new GridBagLayout());
            addCard(rightColumn, tipsCard, 0, 0, 1);
            addCard(rightColumn, generatorCard, 0, 1, 1);

            GridBagConstraints leftGbc = new GridBagConstraints();
            leftGbc.gridx = 0;
            leftGbc.gridy = 0;
            leftGbc.weightx = 0.53;
            leftGbc.weighty = 1;
            leftGbc.fill = GridBagConstraints.HORIZONTAL;
            leftGbc.anchor = GridBagConstraints.NORTH;
            leftGbc.insets = new Insets(0, 0, 0, 12);
            content.add(leftColumn, leftGbc);

            GridBagConstraints rightGbc = new GridBagConstraints();
            rightGbc.gridx = 1;
            rightGbc.gridy = 0;
            rightGbc.weightx = 0.47;
            rightGbc.weighty = 1;
            rightGbc.fill = GridBagConstraints.HORIZONTAL;
            rightGbc.anchor = GridBagConstraints.NORTH;
            rightGbc.insets = new Insets(0, 12, 0, 0);
            content.add(rightColumn, rightGbc);

            JPanel center = transparentPanel(new BorderLayout(0, 20));
            center.add(hero, BorderLayout.NORTH);
            center.add(content, BorderLayout.CENTER);

            page.add(header, BorderLayout.NORTH);
            page.add(center, BorderLayout.CENTER);
            page.add(privacy, BorderLayout.SOUTH);

            JScrollPane scrollPane = new JScrollPane(page);
            scrollPane.setBorder(null);
            scrollPane.setBackground(BACKGROUND);
            scrollPane.getViewport().setBackground(BACKGROUND);
            scrollPane.getVerticalScrollBar().setUnitIncrement(18);
            scrollPane.setHorizontalScrollBarPolicy(
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            frame.setContentPane(scrollPane);
            frame.getRootPane().setBorder(null);
            frame.setLocationRelativeTo(null);

            // -------------------- ANALYZE BUTTON --------------------

            analyzeButton.addActionListener(e -> {
                char[] enteredChars = passwordField.getPassword();

                if (enteredChars.length == 0) {
                    resultLabel.setText("Enter a password");
                    resultLabel.setForeground(ORANGE);
                    scoreLabel.setText("SCORE  •  0 / 7");
                    progressBar.setValue(0);
                    progressBar.setForeground(BLUE);
                    warningLabel.setText(" ");
                    tipsArea.setText("Enter a password to see its local estimate and suggestions.");
                    for (JLabel label : checklist) {
                        label.setText("○   " + label.getText().substring(3));
                        label.setForeground(MUTED);
                    }
                    return;
                }
                PasswordSecurity.Analysis analysis;
                try {
                    analysis = PasswordSecurity.analyze(new String(enteredChars));
                } finally {
                    Arrays.fill(enteredChars, '\0');
                }
                int score = analysis.score;
                String strength = analysis.strength;

                resultLabel.setText(strength);
                scoreLabel.setText("SCORE  •  " + score + " / 7");
                progressBar.setValue(score);

                if (strength.equals("Weak")) {
                    resultLabel.setForeground(RED);
                    progressBar.setForeground(RED);
                } else if (strength.equals("Moderate")) {
                    resultLabel.setForeground(ORANGE);
                    progressBar.setForeground(ORANGE);
                } else {
                    resultLabel.setForeground(GREEN);
                    progressBar.setForeground(GREEN);
                }

                boolean[] checks = {analysis.length8, analysis.upper, analysis.lower, analysis.digit, analysis.special};

                for (int i = 0; i < checklist.length; i++) {
                    String text = checklist[i].getText().substring(3);
                    if (checks[i]) {
                        checklist[i].setText("✓   " + text);
                        checklist[i].setForeground(GREEN);
                    } else {
                        checklist[i].setText("○   " + text);
                        checklist[i].setForeground(MUTED);
                    }
                }

                java.util.List<String> warnings = new java.util.ArrayList<>();
                if (analysis.common) warnings.add("Common password");
                if (analysis.repeated) warnings.add("Repeated characters");
                if (analysis.sequential) warnings.add("Sequential pattern");
                warningLabel.setText(warnings.isEmpty() ? "No listed pattern detected" : "Warning: " + String.join(" · ", warnings));
                warningLabel.setForeground(warnings.isEmpty() ? GREEN : RED);
                StringBuilder explanation = new StringBuilder("Why this rating:\n");
                for (String reason : analysis.reasons) explanation.append("• ").append(reason).append('\n');
                explanation.append("\nSuggestions:\n");
                for (String tip : analysis.tips) explanation.append("• ").append(tip).append('\n');
                tipsArea.setText(explanation.toString());
            });

            // -------------------- CLEAR BUTTON (LOGIC UNCHANGED) --------------------

            clearButton.addActionListener(e -> {
                passwordField.setText("");

                showPassword.setSelected(false);
                passwordField.setEchoChar('•');

                resultLabel.setText("Not Analyzed");
                resultLabel.setForeground(ORANGE);

                scoreLabel.setText("SCORE  •  0 / 7");
                progressBar.setValue(0);
                progressBar.setForeground(BLUE);

                warningLabel.setText(" ");
                tipsArea.setText(
                        "Analyze a password to see personalized suggestions.");

                for (JLabel label : checklist) {
                    label.setText("○   " + label.getText().substring(3));
                    label.setForeground(MUTED);
                }

                generatedPassword[0] = "";
                generatedLabel.setText("Generate a password to preview it here.");
                copyButton.setEnabled(false);
                clipboardFeedback.setText("");
            });

            frame.setVisible(true);
        });
    }
}
















