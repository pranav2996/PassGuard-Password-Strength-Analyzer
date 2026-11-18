import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Analysis-only insights view. It never renders the input field or generator. */
final class InsightsPage extends JPanel {
    private final CardLayout layout = new CardLayout();
    private final JPanel states = GlassUI.transparent(layout);
    private final JPanel results = GlassUI.transparent(new BorderLayout(0, 16));

    InsightsPage() {
        setOpaque(false);
        setLayout(new BorderLayout());
        states.add(emptyState(), "empty");
        states.add(results, "results");
        add(states, BorderLayout.CENTER);
        showEmpty();
    }

    void setAnalysis(PasswordSecurity.Analysis analysis) {
        results.removeAll();
        if (analysis == null) {
            showEmpty();
            return;
        }
        results.add(createHeader(), BorderLayout.NORTH);
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        AdaptivePair summary = new AdaptivePair(16, 790,
                createPatternCard(analysis), createExplanationCard(analysis));
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        details.add(summary);
        details.add(Box.createVerticalStrut(16));
        details.add(createRecommendationsCard(analysis));
        JScrollPane scroll = new JScrollPane(details,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        results.add(scroll, BorderLayout.CENTER);
        layout.show(states, "results");
        revalidate();
        repaint();
    }

    private JPanel createHeader() {
        JPanel header = GlassUI.transparent(new BorderLayout(0, 5));
        header.add(GlassUI.title("Security Insights", 27), BorderLayout.NORTH);
        header.add(GlassUI.muted("Pattern checks, score reasoning and practical recommendations.", 14),
                BorderLayout.CENTER);
        return header;
    }

    private GlassCard createPatternCard(PasswordSecurity.Analysis analysis) {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("shield", "Pattern Detection"));
        card.add(Box.createVerticalStrut(5));
        boolean any = analysis.common || analysis.repeated || analysis.sequential;
        JLabel status = new JLabel(any ? "  Review these warnings" : "  No listed pattern detected");
        status.setFont(GlassUI.BODY_BOLD);
        status.setForeground(any ? GlassUI.ORANGE : GlassUI.GREEN);
        status.setBorder(new EmptyBorder(10, 13, 10, 13));
        status.setOpaque(true);
        status.setBackground(any ? new Color(255, 164, 57, 28) : new Color(42, 237, 153, 28));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(status);
        card.add(Box.createVerticalStrut(15));
        addCheck(card, "Common-password match", analysis.common,
                analysis.common ? "Matched the local common-password list." : "No exact match in the local list.");
        addCheck(card, "Repeated characters", analysis.repeated,
                analysis.repeated ? "A run of four or more identical characters was found." : "No run of four identical characters found.");
        addCheck(card, "Sequential pattern", analysis.sequential,
                analysis.sequential ? "A four-character ascending or descending sequence was found." : "No four-character sequence found.");
        return card;
    }

    private void addCheck(JPanel parent, String heading, boolean flagged, String detail) {
        JLabel label = new JLabel((flagged ? "⚠  " : "✓  ") + heading);
        label.setFont(GlassUI.BODY_BOLD);
        label.setForeground(flagged ? GlassUI.ORANGE : GlassUI.GREEN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
        JLabel description = GlassUI.muted(detail, 12);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(Box.createVerticalStrut(3));
        parent.add(description);
        parent.add(Box.createVerticalStrut(12));
    }

    private GlassCard createExplanationCard(PasswordSecurity.Analysis analysis) {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("insights", "Why this rating"));
        card.add(Box.createVerticalStrut(14));
        for (String reason : analysis.reasons) {
            JLabel line = new JLabel("•  " + reason);
            line.setFont(GlassUI.BODY);
            line.setForeground(GlassUI.WHITE);
            line.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(line);
            card.add(Box.createVerticalStrut(11));
        }
        return card;
    }

    private GlassCard createRecommendationsCard(PasswordSecurity.Analysis analysis) {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(GlassUI.iconHeading("generate", "Recommendations"));
        card.add(Box.createVerticalStrut(13));
        for (String tip : analysis.tips) {
            JLabel line = new JLabel("•  " + tip);
            line.setFont(GlassUI.BODY);
            line.setForeground(GlassUI.MUTED);
            line.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(line);
            card.add(Box.createVerticalStrut(8));
        }
        card.add(Box.createVerticalStrut(3));
        card.add(GlassUI.muted("These checks are local heuristics; they do not query breached-password data.", 11));
        return card;
    }

    private GlassCard emptyState() {
        GlassCard card = GlassUI.card(null, null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(42, 36, 42, 36));
        card.add(new JLabel(new GlyphIcon("insights", 48, GlassUI.PURPLE_LIGHT)));
        card.add(Box.createVerticalStrut(17));
        card.add(GlassUI.title("Insights will appear here", 23));
        card.add(Box.createVerticalStrut(8));
        card.add(GlassUI.muted("Go to Analyser and analyse a password to see its pattern checks,", 14));
        card.add(GlassUI.muted("rating explanation and tailored suggestions.", 14));
        return card;
    }

    private void showEmpty() {
        layout.show(states, "empty");
        revalidate();
        repaint();
    }
}
