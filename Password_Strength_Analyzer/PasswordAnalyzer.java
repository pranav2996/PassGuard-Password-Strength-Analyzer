import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    private static final SecureRandom random = new SecureRandom();

    // -------------------- PASSWORD LOGIC (UNCHANGED) --------------------

    public static String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;

        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        for (int i = 4; i < 16; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public static boolean isCommonPassword(String password) {
        Set<String> common = new HashSet<>();
        common.add("password");
        common.add("123456");
        common.add("12345678");
        common.add("qwerty");
        common.add("admin");
        common.add("password123");
        common.add("welcome");
        common.add("iloveyou");
        return common.contains(password.toLowerCase());
    }

    public static boolean hasRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 3; i++) {
            if (password.charAt(i) == password.charAt(i + 1)
                    && password.charAt(i) == password.charAt(i + 2)
                    && password.charAt(i) == password.charAt(i + 3)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSequentialPattern(String password) {
        for (int i = 0; i < password.length() - 3; i++) {
            char a = password.charAt(i);
            char b = password.charAt(i + 1);
            char c = password.charAt(i + 2);
            char d = password.charAt(i + 3);

            if (b == a + 1 && c == b + 1 && d == c + 1) {
                return true;
            }
            if (b == a - 1 && c == b - 1 && d == c - 1) {
                return true;
            }
        }
        return false;
    }

    public static String generateTips(boolean length, boolean upper,
                                       boolean lower, boolean digit,
                                       boolean special, int score) {
        StringBuilder tips = new StringBuilder();

        if (!length) tips.append("• Use at least 8 characters.\n");
        if (!upper) tips.append("• Add an uppercase letter.\n");
        if (!lower) tips.append("• Add a lowercase letter.\n");
        if (!digit) tips.append("• Include a number.\n");
        if (!special) tips.append("• Include a special character.\n");
        if (score < 6) tips.append("• Try using a longer password (12+ characters).\n");

        if (tips.length() == 0) {
            tips.append("Great! Your password meets all the basic checks.");
        }
        return tips.toString();
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
            frame.setMinimumSize(new Dimension(900, 700));
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
            resultCard.add(mutedText("Score is based on length and character variety.", 11));

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
                    "Create a random 16-character password.");

            JLabel generatedLabel = new JLabel(
                    "Your generated password will appear here.");
            generatedLabel.setFont(new Font("Consolas", Font.BOLD, 14));
            generatedLabel.setForeground(GREEN);
            generatedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            generatedLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
            generatedLabel.setOpaque(true);
            generatedLabel.setBackground(CARD_LIGHT);
            generatedLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            generatorCard.add(generatedLabel);
            generatorCard.add(Box.createVerticalStrut(14));

            JPanel generatorButtons = transparentPanel(
                    new FlowLayout(FlowLayout.LEFT, 9, 0));
            generatorButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
            ModernButton generateButton = new ModernButton(
                    "Generate password", GREEN.darker());
            ModernButton copyButton = new ModernButton("Copy password", BLUE);
            generatorButtons.add(generateButton);
            generatorButtons.add(copyButton);
            generatorCard.add(generatorButtons);

            final String[] generatedPassword = {""};

            generateButton.addActionListener(e -> {
                generatedPassword[0] = generatePassword();
                generatedLabel.setText(generatedPassword[0]);
            });

            copyButton.addActionListener(e -> {
                if (!generatedPassword[0].isEmpty()) {
                    StringSelection selection =
                            new StringSelection(generatedPassword[0]);
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(selection, null);
                    JOptionPane.showMessageDialog(
                            frame,
                            "Generated password copied!",
                            "PassGuard",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Generate a password first!",
                            "PassGuard",
                            JOptionPane.INFORMATION_MESSAGE);
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

            // -------------------- ANALYZE BUTTON (LOGIC UNCHANGED) --------------------

            analyzeButton.addActionListener(e -> {
                char[] enteredChars = passwordField.getPassword();

                if (enteredChars.length == 0) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Please enter a password first!",
                            "PassGuard",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String password = new String(enteredChars);
                Arrays.fill(enteredChars, '\0');

                int score = 0;

                boolean length = password.length() >= 8;
                boolean upper = password.matches(".*[A-Z].*");
                boolean lower = password.matches(".*[a-z].*");
                boolean digit = password.matches(".*[0-9].*");
                boolean special = password.matches(".*[^a-zA-Z0-9].*");

                if (length) score++;
                if (upper) score++;
                if (lower) score++;
                if (digit) score++;
                if (special) score++;

                if (password.length() >= 12) score++;
                if (password.length() >= 16) score++;

                boolean common = isCommonPassword(password);
                boolean repeated = hasRepeatedCharacters(password);
                boolean sequential = hasSequentialPattern(password);

                String strength;

                if (common || repeated || sequential) {
                    strength = "Weak";
                } else if (score <= 1) {
                    strength = "Weak";
                } else if (score <= 3) {
                    strength = "Medium";
                } else if (score <= 5) {
                    strength = "Strong";
                } else {
                    strength = "Very Strong";
                }

                resultLabel.setText(strength);
                scoreLabel.setText("SCORE  •  " + score + " / 7");
                progressBar.setValue(score);

                if (strength.equals("Weak")) {
                    resultLabel.setForeground(RED);
                    progressBar.setForeground(RED);
                } else if (strength.equals("Medium")) {
                    resultLabel.setForeground(ORANGE);
                    progressBar.setForeground(ORANGE);
                } else {
                    resultLabel.setForeground(GREEN);
                    progressBar.setForeground(GREEN);
                }

                boolean[] checks = {length, upper, lower, digit, special};

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

                warningLabel.setText(" ");
                if (common) {
                    warningLabel.setText("Warning: This is a common password!");
                } else if (repeated) {
                    warningLabel.setText("Warning: Repeated characters detected!");
                } else if (sequential) {
                    warningLabel.setText("Warning: Sequential pattern detected!");
                }

                tipsArea.setText(generateTips(
                        length, upper, lower, digit, special, score));

                if (common || repeated || sequential) {
                    tipsArea.append(
                            "\n• Avoid common words, repeated characters, and sequences.");
                }
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
                generatedLabel.setText(
                        "Your generated password will appear here.");
            });

            frame.setVisible(true);
        });
    }
}
















