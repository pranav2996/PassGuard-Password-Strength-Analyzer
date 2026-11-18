(function (root, factory) {
  const api = factory();
  if (typeof module === "object" && module.exports) module.exports = api;
  if (root) root.PassGuardCore = api;
})(typeof globalThis === "undefined" ? this : globalThis, function () {
  "use strict";
  const COMMON = new Set(["password", "123456", "12345678", "123456789", "1234567890", "qwerty", "qwerty123", "admin", "admin123", "password123", "welcome", "welcome1", "iloveyou", "letmein", "monkey", "dragon", "abc123", "111111", "000000", "passw0rd", "user", "guest"]);
  const GROUPS = {
    upper: "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    lower: "abcdefghijklmnopqrstuvwxyz",
    digits: "0123456789",
    symbols: "!@#$%^&*()-_=+[]{}.,?"
  };

  function hasRepeatedCharacters(password) {
    let run = 1;
    for (let i = 1; i < password.length; i++) {
      run = password[i] === password[i - 1] ? run + 1 : 1;
      if (run >= 4) return true;
    }
    return false;
  }

  function isLetterOrDigit(character) {
    return /[\p{L}\p{Nd}]/u.test(character);
  }

  function hasSequentialPattern(password) {
    for (let i = 0; i + 3 < password.length; i++) {
      const lowerAt = index => password[index].toLowerCase().codePointAt(0);
      const direction = lowerAt(i + 1) - lowerAt(i);
      if (Math.abs(direction) !== 1) continue;
      let sequence = true;
      for (let j = 2; j < 4; j++) {
        if (lowerAt(i + j) - lowerAt(i + j - 1) !== direction) { sequence = false; break; }
      }
      if (sequence && isLetterOrDigit(password[i]) && isLetterOrDigit(password[i + 3])) return true;
    }
    return false;
  }

  function analyse(password) {
    const characters = Array.from(password);
    const flags = {
      length8: characters.length >= 8,
      length12: characters.length >= 12,
      length16: characters.length >= 16,
      length20: characters.length >= 20,
      upper: /\p{Uppercase}/u.test(password),
      lower: /\p{Lowercase}/u.test(password),
      digit: /\p{Nd}/u.test(password),
      special: /[^\p{L}\p{Nd}]/u.test(password)
    };
    const common = COMMON.has(password.toLowerCase());
    const repeated = hasRepeatedCharacters(password);
    const sequential = hasSequentialPattern(password);
    const variety = [flags.upper, flags.lower, flags.digit, flags.special].filter(Boolean).length;
    let score = characters.length >= 20 ? 4 : characters.length >= 16 ? 3 : characters.length >= 12 ? 2 : characters.length >= 8 ? 1 : 0;
    score += Math.min(3, Math.max(0, variety - 1));
    if (common) score = 0;
    else {
      if (repeated) score--;
      if (sequential) score--;
    }
    score = Math.max(0, Math.min(7, score));
    const strength = score <= 2 ? "Weak" : score <= 4 ? "Moderate" : score <= 6 ? "Strong" : "Very Strong";
    const lengthReason = characters.length >= 20 ? "Length contributes 4 points (20+ characters)." : characters.length >= 16 ? "Length contributes 3 points (16–19 characters)." : characters.length >= 12 ? "Length contributes 2 points (12–15 characters)." : characters.length >= 8 ? "Length contributes 1 point (8–11 characters)." : "Under 8 characters earns no length points.";
    const reasons = [lengthReason, `Character variety contributes ${Math.min(3, Math.max(0, variety - 1))} of 3 possible points.`];
    if (common) reasons.push("A common-password match sets the score to 0.");
    else {
      if (repeated) reasons.push("A run of four or more identical characters deducts 1 point.");
      if (sequential) reasons.push("A run of four or more sequential characters deducts 1 point.");
    }
    const tips = [];
    if (!flags.length12) tips.push("Use at least 12 characters; a longer unique passphrase can be easier to remember.");
    if (variety < 2) tips.push("Add variety where useful, while prioritizing length and uniqueness.");
    if (common) tips.push("Replace this common password with a unique phrase not used on another account.");
    if (repeated || sequential) tips.push("Avoid long repeated or sequential runs.");
    if (!tips.length) tips.push("Use a unique password for every account; this estimate cannot verify reuse or breaches.");
    return { score, strength, ...flags, common, repeated, sequential, reasons, tips };
  }

  function randomIndex(max, cryptoSource = globalThis.crypto) {
    if (!Number.isInteger(max) || max < 1 || max > 0x100000000) throw new RangeError("Invalid random range");
    if (!cryptoSource || typeof cryptoSource.getRandomValues !== "function") throw new Error("Secure randomness is unavailable in this browser context.");
    const limit = Math.floor(0x100000000 / max) * max;
    const sample = new Uint32Array(1);
    do { cryptoSource.getRandomValues(sample); } while (sample[0] >= limit);
    return sample[0] % max;
  }

  function generate(length, enabled, cryptoSource = globalThis.crypto) {
    const size = Number(length);
    if (!Number.isInteger(size) || size < 8 || size > 128) throw new RangeError("Length must be between 8 and 128.");
    const chosen = Object.keys(GROUPS).filter(key => Boolean(enabled[key]));
    if (!chosen.length) throw new RangeError("Select at least one character category.");
    let alphabet = "";
    const result = [];
    for (const key of chosen) {
      const group = GROUPS[key];
      if (!group) throw new RangeError("Unknown character category.");
      alphabet += group;
      result.push(group[randomIndex(group.length, cryptoSource)]);
    }
    while (result.length < size) result.push(alphabet[randomIndex(alphabet.length, cryptoSource)]);
    for (let i = result.length - 1; i > 0; i--) {
      const j = randomIndex(i + 1, cryptoSource);
      [result[i], result[j]] = [result[j], result[i]];
    }
    return result.join("");
  }

  return { analyse, generate, hasRepeatedCharacters, hasSequentialPattern, GROUPS };
});
