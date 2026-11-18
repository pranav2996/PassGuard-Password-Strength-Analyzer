import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Local password estimation and generation helpers. This is not a breach check. */
public final class PasswordSecurity {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}.,?";
    private static final Set<String> COMMON = Set.of("password", "123456", "12345678",
            "123456789", "1234567890", "qwerty", "qwerty123", "admin", "admin123",
            "password123", "welcome", "welcome1", "iloveyou", "letmein", "monkey",
            "dragon", "abc123", "111111", "000000", "passw0rd", "user", "guest");

    private PasswordSecurity() { }

    public static final class Analysis {
        public final int score;
        public final String strength;
        public final boolean length8, length12, length16, length20;
        public final boolean upper, lower, digit, special;
        public final boolean common, repeated, sequential;
        public final List<String> reasons;
        public final List<String> tips;

        private Analysis(String password) {
            int length = password.codePointCount(0, password.length());
            length8 = length >= 8; length12 = length >= 12;
            length16 = length >= 16; length20 = length >= 20;
            boolean u = false, l = false, d = false, s = false;
            for (int i = 0; i < password.length();) {
                int cp = password.codePointAt(i); i += Character.charCount(cp);
                u |= Character.isUpperCase(cp); l |= Character.isLowerCase(cp);
                d |= Character.isDigit(cp); s |= !Character.isLetterOrDigit(cp);
            }
            upper = u; lower = l; digit = d; special = s;
            String normalized = password.toLowerCase(Locale.ROOT);
            common = COMMON.contains(normalized);
            repeated = hasRepeatedCharacters(password);
            sequential = hasSequentialPattern(password);

            int points = length >= 20 ? 4 : length >= 16 ? 3 : length >= 12 ? 2 : length >= 8 ? 1 : 0;
            int varieties = (upper ? 1 : 0) + (lower ? 1 : 0) + (digit ? 1 : 0) + (special ? 1 : 0);
            points += Math.min(3, Math.max(0, varieties - 1));
            if (common) points = 0;
            else {
                if (repeated) points--;
                if (sequential) points--;
            }
            score = Math.max(0, Math.min(7, points));
            strength = score <= 2 ? "Weak" : score <= 4 ? "Moderate" : score <= 6 ? "Strong" : "Very Strong";

            List<String> why = new ArrayList<>();
            why.add(length >= 20 ? "Length contributes 4 points (20+ characters)." : length >= 16 ? "Length contributes 3 points (16–19 characters)." : length >= 12 ? "Length contributes 2 points (12–15 characters)." : length >= 8 ? "Length contributes 1 point (8–11 characters)." : "Under 8 characters earns no length points.");
            why.add("Character variety contributes " + Math.min(3, Math.max(0, varieties - 1)) + " of 3 possible points.");
            if (common) why.add("A common-password match sets the score to 0.");
            else {
                if (repeated) why.add("A run of four or more identical characters deducts 1 point.");
                if (sequential) why.add("A run of four or more sequential characters deducts 1 point.");
            }
            reasons = Collections.unmodifiableList(why);
            List<String> suggestions = new ArrayList<>();
            if (!length12) suggestions.add("Use at least 12 characters; a longer unique passphrase can be easier to remember.");
            if (varieties < 2) suggestions.add("Add variety where useful, while prioritizing length and uniqueness.");
            if (common) suggestions.add("Replace this common password with a unique phrase not used on another account.");
            if (repeated || sequential) suggestions.add("Avoid long repeated or sequential runs.");
            if (suggestions.isEmpty()) suggestions.add("Use a unique password for every account; this estimate cannot verify reuse or breaches.");
            tips = Collections.unmodifiableList(suggestions);
        }
    }

    public static Analysis analyze(String password) {
        if (password == null) throw new IllegalArgumentException("Password cannot be null");
        return new Analysis(password);
    }

    public static boolean isCommonPassword(String password) {
        return password != null && COMMON.contains(password.toLowerCase(Locale.ROOT));
    }

    public static boolean hasRepeatedCharacters(String password) {
        int run = 1;
        for (int i = 1; i < password.length(); i++) {
            run = password.charAt(i) == password.charAt(i - 1) ? run + 1 : 1;
            if (run >= 4) return true;
        }
        return false;
    }

    public static boolean hasSequentialPattern(String password) {
        for (int i = 0; i + 3 < password.length(); i++) {
            int direction = Character.toLowerCase(password.charAt(i + 1)) - Character.toLowerCase(password.charAt(i));
            if (Math.abs(direction) != 1) continue;
            boolean sequence = true;
            for (int j = 2; j < 4; j++) {
                int delta = Character.toLowerCase(password.charAt(i + j)) - Character.toLowerCase(password.charAt(i + j - 1));
                if (delta != direction) { sequence = false; break; }
            }
            if (sequence && Character.isLetterOrDigit(password.charAt(i))
                    && Character.isLetterOrDigit(password.charAt(i + 3))) return true;
        }
        return false;
    }

    public static String generatePassword(int length, boolean useUpper, boolean useLower,
                                          boolean useDigits, boolean useSpecial) {
        if (length < 1 || length > 128) throw new IllegalArgumentException("Length must be 1–128");
        StringBuilder alphabet = new StringBuilder();
        List<Character> chars = new ArrayList<>();
        if (useUpper) { alphabet.append(UPPER); chars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length()))); }
        if (useLower) { alphabet.append(LOWER); chars.add(LOWER.charAt(RANDOM.nextInt(LOWER.length()))); }
        if (useDigits) { alphabet.append(DIGITS); chars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length()))); }
        if (useSpecial) { alphabet.append(SPECIAL); chars.add(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()))); }
        if (alphabet.length() == 0) throw new IllegalArgumentException("Select at least one character category");
        if (length < chars.size()) throw new IllegalArgumentException("Length must fit each selected category");
        while (chars.size() < length) chars.add(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        Collections.shuffle(chars, RANDOM);
        StringBuilder result = new StringBuilder(length);
        for (char c : chars) result.append(c);
        return result.toString();
    }
}
