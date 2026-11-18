import java.util.HashSet;
import java.util.Set;

/** Dependency-free automated checks for the password logic. Run with run-tests.ps1. */
public final class PasswordSecurityTest {
    private static int checks;

    public static void main(String[] args) {
        PasswordSecurity.Analysis empty = PasswordSecurity.analyze("");
        equal(0, empty.score, "empty score");
        equal("Weak", empty.strength, "empty classification");
        equal(0, PasswordSecurity.analyze("abc").score, "short lowercase score");
        scoreRange("abcdefghijk", "lowercase-only");
        scoreRange("ABCDEFGHIJK", "uppercase-only");
        scoreRange("12345678901", "number-only");
        scoreRange("!@#$%^&*()_", "symbols-only");
        scoreRange("Abc123!xyZ9", "mixed password");

        PasswordSecurity.Analysis phrase = PasswordSecurity.analyze("correct horse battery staple");
        check(phrase.score >= PasswordSecurity.analyze("Ab1!").score, "long passphrase earns length credit");
        check(phrase.tips.stream().anyMatch(t -> t.contains("unique")), "recommendation available");
        check(PasswordSecurity.analyze("password").common, "common password detected");
        equal(0, PasswordSecurity.analyze("password").score, "common password capped");
        check(PasswordSecurity.hasRepeatedCharacters("abbbbbcd"), "repeated run detected");
        check(!PasswordSecurity.hasRepeatedCharacters("abbbcd"), "short repeated run ignored");
        check(PasswordSecurity.hasSequentialPattern("xxabcdYY"), "ascending sequence detected");
        check(PasswordSecurity.hasSequentialPattern("xx9876YY"), "descending sequence detected");
        check(!PasswordSecurity.hasSequentialPattern("axbycz"), "nonsequential input ignored");

        PasswordSecurity.Analysis[] boundaries = {
                PasswordSecurity.analyze("a"), PasswordSecurity.analyze("abcdefgh"),
                PasswordSecurity.analyze("abcdefghijkl"), PasswordSecurity.analyze("abcdefghijklmnop"),
                PasswordSecurity.analyze("abcdefghijklmnopqrst")};
        for (PasswordSecurity.Analysis a : boundaries) {
            check(a.score >= 0 && a.score <= 7, "score remains in 0..7");
            String expected = a.score <= 2 ? "Weak" : a.score <= 4 ? "Moderate" : a.score <= 6 ? "Strong" : "Very Strong";
            equal(expected, a.strength, "classification boundary");
        }

        String all = PasswordSecurity.generatePassword(32, true, true, true, true);
        equal(32, all.length(), "configured length");
        check(all.chars().anyMatch(Character::isUpperCase), "uppercase included");
        check(all.chars().anyMatch(Character::isLowerCase), "lowercase included");
        check(all.chars().anyMatch(Character::isDigit), "digit included");
        check(all.chars().anyMatch(c -> !Character.isLetterOrDigit(c)), "symbol included");
        String onlyDigits = PasswordSecurity.generatePassword(12, false, false, true, false);
        check(onlyDigits.matches("[0-9]{12}"), "selected category respected");
        Set<String> samples = new HashSet<>();
        for (int i = 0; i < 100; i++) samples.add(PasswordSecurity.generatePassword(16, true, true, true, true));
        check(samples.size() > 95, "generator produces varied samples");
        expectIllegal(() -> PasswordSecurity.generatePassword(3, true, true, true, true), "length fits categories");
        expectIllegal(() -> PasswordSecurity.generatePassword(8, false, false, false, false), "category required");
        System.out.println("PASS: " + checks + " assertions");
    }

    private static void scoreRange(String password, String name) {
        PasswordSecurity.Analysis result = PasswordSecurity.analyze(password);
        check(result.score >= 0 && result.score <= 7, name + " score range");
        check(result.strength != null, name + " classification");
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void equal(Object expected, Object actual, String message) {
        check(expected.equals(actual), message + ": expected " + expected + ", got " + actual);
    }
    private static void expectIllegal(Runnable action, String message) {
        try { action.run(); } catch (IllegalArgumentException expected) { checks++; return; }
        throw new AssertionError(message + ": expected IllegalArgumentException");
    }
}
