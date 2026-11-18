import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/** Dedicated password generation view with persistent local page state. */
final class GeneratorPage extends JPanel {
    private final JSpinner length = new JSpinner(new SpinnerNumberModel(16, 8, 128, 1));
    private final JCheckBox uppercase = new JCheckBox("Uppercase A–Z", true);
    private final JCheckBox lowercase = new JCheckBox("Lowercase a–z", true);
    private final JCheckBox numbers = new JCheckBox("Numbers 0–9", true);
    private final JCheckBox symbols = new JCheckBox("Symbols", true);
    private final JLabel[] groupChips = new JLabel[4];
    private final JTextField passwordOutput = new JTextField("Your generated password will appear here.");
    private final JLabel feedback = GlassUI.muted("Generation happens locally on this device.", 12);
    private final GlassButton copyButton = new GlassButton("Copy Password", false);
    private String generatedPassword = "";

    GeneratorPage() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        add(buildCard(), new GridBagConstraints());
    }

    private GlassCard buildCard() {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("key", "Strong Password Generator"));
        card.add(Box.createVerticalStrut(5));
        card.add(GlassUI.muted("Choose the length and character groups for your random password.", 12));
        card.add(Box.createVerticalStrut(25));

        JPanel options = GlassUI.transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0; c.insets = new Insets(4, 5, 4, 16); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; options.add(GlassUI.title("Length", 13), c);
        c.gridx = 1; options.add(length, c);
        c.gridy = 1; c.gridx = 0; c.gridwidth = 1;
        addOption(options, c, uppercase, 0, 1);
        addOption(options, c, lowercase, 1, 1);
        addOption(options, c, numbers, 2, 1);
        addOption(options, c, symbols, 3, 1);
        options.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(options);
        card.add(Box.createVerticalStrut(18));

        JPanel mix = GlassUI.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        mix.setAlignmentX(Component.LEFT_ALIGNMENT);
        mix.add(GlassUI.muted("Selected groups:", 11));
        String[] chipNames = {"A–Z", "a–z", "0–9", "Symbols"};
        for (int i = 0; i < chipNames.length; i++) {
            JLabel chip = new JLabel(chipNames[i]);
            chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
            chip.setForeground(new Color(226, 213, 255));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(177, 145, 255, 90)),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)));
            chip.setOpaque(true);
            chip.setBackground(new Color(135, 95, 236, 45));
            groupChips[i] = chip;
            mix.add(chip);
        }
        syncChips();
        for (JCheckBox option : new JCheckBox[]{uppercase, lowercase, numbers, symbols}) {
            option.addActionListener(event -> syncChips());
        }
        card.add(mix);
        card.add(Box.createVerticalStrut(19));

        GlassUI.styleField(passwordOutput);
        passwordOutput.setFont(new Font("Consolas", Font.BOLD, 19));
        passwordOutput.setForeground(GlassUI.GREEN);
        passwordOutput.setEditable(false);
        passwordOutput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        passwordOutput.setPreferredSize(new Dimension(400, 58));
        passwordOutput.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordOutput);
        card.add(Box.createVerticalStrut(16));

        JPanel buttons = GlassUI.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        GlassButton generateButton = new GlassButton("Generate Password", true);
        generateButton.setIcon(new GlyphIcon("generate", 18, GlassUI.WHITE));
        copyButton.setIcon(new GlyphIcon("checklist", 18, GlassUI.WHITE));
        copyButton.setEnabled(false);
        GlassButton clearButton = new GlassButton("Clear", false);
        generateButton.addActionListener(event -> generate());
        copyButton.addActionListener(event -> copy());
        clearButton.addActionListener(event -> clear());
        buttons.add(generateButton);
        buttons.add(copyButton);
        buttons.add(clearButton);
        card.add(buttons);
        card.add(Box.createVerticalStrut(11));
        card.add(feedback);
        card.setPreferredSize(new Dimension(790, 380));
        card.setMaximumSize(new Dimension(1100, 430));
        return card;
    }

    private void addOption(JPanel parent, GridBagConstraints constraints,
                           JCheckBox option, int index, int y) {
        GlassUI.styleCheckBox(option);
        constraints.gridy = y + index / 2;
        constraints.gridx = index % 2;
        constraints.insets = new Insets(6, 5, 6, 22);
        parent.add(option, constraints);
    }

    private void syncChips() {
        if (groupChips[0] == null) return;
        boolean[] selected = {uppercase.isSelected(), lowercase.isSelected(),
                numbers.isSelected(), symbols.isSelected()};
        for (int i = 0; i < selected.length; i++) groupChips[i].setVisible(selected[i]);
    }

    private void generate() {
        try {
            generatedPassword = PasswordSecurity.generatePassword((Integer) length.getValue(),
                    uppercase.isSelected(), lowercase.isSelected(), numbers.isSelected(), symbols.isSelected());
            passwordOutput.setText(generatedPassword);
            copyButton.setEnabled(true);
            feedback.setForeground(GlassUI.GREEN);
            feedback.setText("Generated locally. Copy only where you intend to use it.");
        } catch (IllegalArgumentException error) {
            generatedPassword = "";
            passwordOutput.setText("Select at least one character group.");
            copyButton.setEnabled(false);
            feedback.setForeground(GlassUI.ORANGE);
            feedback.setText(error.getMessage());
        }
    }

    private void copy() {
        if (generatedPassword.isEmpty()) {
            feedback.setForeground(GlassUI.ORANGE);
            feedback.setText("Generate a password first.");
            return;
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(generatedPassword), null);
            feedback.setForeground(GlassUI.GREEN);
            feedback.setText("Copied. The system clipboard may retain it until replaced.");
        } catch (HeadlessException | IllegalStateException error) {
            feedback.setForeground(GlassUI.ORANGE);
            feedback.setText("Clipboard is unavailable or busy.");
        } catch (SecurityException error) {
            feedback.setForeground(GlassUI.ORANGE);
            feedback.setText("Clipboard access was denied by the environment.");
        }
    }

    private void clear() {
        generatedPassword = "";
        passwordOutput.setText("Your generated password will appear here.");
        copyButton.setEnabled(false);
        feedback.setForeground(GlassUI.MUTED);
        feedback.setText("Generation happens locally on this device.");
    }
}
