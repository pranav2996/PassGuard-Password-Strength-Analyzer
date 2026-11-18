import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** PassGuard desktop application frame and top-level navigation. */
public final class PasswordAnalyzer {
    private PasswordAnalyzer() { }

    /** Compatibility entry point for the original default generator. */
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordAnalyzer::createAndShowWindow);
    }

    private static void createAndShowWindow() {
        JFrame frame = new JFrame("PassGuard | Password Strength Analyser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(720, 620));
        frame.setSize(1420, 920);
        frame.setLocationRelativeTo(null);

        AnimatedBackground background = new AnimatedBackground();
        background.setBorder(new EmptyBorder(0, 22, 0, 22));
        frame.setContentPane(background);

        FadingPageHost pages = new FadingPageHost();
        pages.setPreferredSize(new Dimension(1200, 630));
        pages.setMinimumSize(new Dimension(0, 0));

        InsightsPage insightsPage = new InsightsPage();
        AnalyzerPage analyserPage = new AnalyzerPage(insightsPage::setAnalysis);
        GeneratorPage generatorPage = new GeneratorPage();
        pages.addPage(analyserPage, NavigationBar.ANALYSER);
        pages.addPage(insightsPage, NavigationBar.INSIGHTS);
        pages.addPage(generatorPage, NavigationBar.GENERATE);
        pages.showPage(NavigationBar.ANALYSER);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(12, 10, 8, 10));
        header.add(createTopBar(pages));
        header.add(Box.createVerticalStrut(7));
        header.add(createHero());

        JScrollPane pageScroll = new JScrollPane(pages,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setBorder(null);
        pageScroll.setOpaque(false);
        pageScroll.getViewport().setOpaque(false);
        pageScroll.getVerticalScrollBar().setUnitIncrement(20);
        pageScroll.getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        pageScroll.getVerticalScrollBar().setBackground(new Color(9, 15, 39, 80));
        pageScroll.getVerticalScrollBar().setForeground(new Color(172, 139, 255, 190));

        background.add(header, BorderLayout.NORTH);
        background.add(pageScroll, BorderLayout.CENTER);
        background.add(createPrivacyNote(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static JPanel createTopBar(FadingPageHost pages) {
        JPanel row = new JPanel(new BorderLayout(18, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(1000, 65));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel brand = GlassUI.transparent(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel mark = new JLabel(new GlyphIcon("shield", 42, GlassUI.PURPLE_LIGHT));
        JPanel name = GlassUI.transparent(new GridLayout(2, 1, 0, 0));
        name.add(GlassUI.title("PassGuard", 19));
        name.add(GlassUI.muted("PASSWORD SECURITY CENTER", 9));
        brand.add(mark);
        brand.add(name);

        JPanel navHolder = GlassUI.transparent(new FlowLayout(FlowLayout.CENTER, 0, 5));
        navHolder.add(new NavigationBar(event -> pages.showPage(event.getActionCommand())));

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 9));
        badge.setOpaque(true);
        badge.setBackground(new Color(20, 99, 79, 92));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 232, 169, 80)),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        JLabel shield = new JLabel(new GlyphIcon("shield", 17, GlassUI.GREEN));
        JLabel localText = new JLabel("Local Analysis");
        localText.setFont(GlassUI.BODY_BOLD);
        localText.setForeground(GlassUI.GREEN);
        badge.add(shield);
        badge.add(localText);

        JPanel badgeHolder = GlassUI.transparent(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        badgeHolder.add(badge);
        row.add(brand, BorderLayout.WEST);
        row.add(navHolder, BorderLayout.CENTER);
        row.add(badgeHolder, BorderLayout.EAST);
        return row;
    }

    private static JPanel createHero() {
        JPanel hero = GlassUI.transparent(new GridBagLayout());
        JPanel text = GlassUI.transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(0, 0, 0, 13);
        text.add(new JLabel(new GlyphIcon("shield", 42, GlassUI.PURPLE_LIGHT)), c);
        c.gridx = 1; c.anchor = GridBagConstraints.WEST; c.insets = new Insets(0, 0, 0, 0);
        text.add(GlassUI.title("Password Strength Analyser", 30), c);
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.insets = new Insets(6, 0, 0, 0);
        JLabel subtitle = GlassUI.muted("Evaluate password security and create stronger alternatives.", 14);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        text.add(subtitle, c);
        hero.add(text);
        return hero;
    }

    private static JPanel createPrivacyNote() {
        JPanel note = GlassUI.transparent(new FlowLayout(FlowLayout.LEFT, 8, 6));
        note.setBorder(new EmptyBorder(0, 10, 4, 10));
        JLabel dot = new JLabel("●");
        dot.setForeground(GlassUI.GREEN);
        note.add(dot);
        note.add(GlassUI.muted("Privacy first: analysis and generation stay on this device.", 11));
        return note;
    }
}
