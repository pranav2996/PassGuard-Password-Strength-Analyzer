const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const core = require("../js/core.js");

test("empty and short passwords are weak and get length suggestions", () => {
  assert.equal(core.analyse("").score, 0);
  assert.equal(core.analyse("abc").strength, "Weak");
  assert.ok(core.analyse("abc").tips.some(tip => tip.includes("12 characters")));
});
test("length and category scoring match the Java rating thresholds", () => {
  assert.equal(core.analyse("abcdefghijklmnopqrst").score, 3);
  assert.equal(core.analyse("aB1!cD2@eF3#gH4$jK5%").score, 7);
  assert.equal(core.analyse("azkqvmpb").score, 1);
  assert.equal(core.analyse("azkqvmpbxrty").score, 2);
  assert.equal(core.analyse("azkqvmpbxrtyfjdl").score, 3);
  assert.equal(core.analyse("azkqvmpbxrtyfjdlwhsg").score, 4);
});
test("checklist detects upper, lower, number, special and Unicode length", () => {
  const result = core.analyse("Abc123!xyZ9");
  assert.equal(result.upper, true); assert.equal(result.lower, true);
  assert.equal(result.digit, true); assert.equal(result.special, true);
  assert.equal(result.length8, true);
  assert.equal(core.analyse("😀😀😀😀").length8, false);
});
test("common passwords, repeated runs and sequences are detected", () => {
  assert.equal(core.analyse("password").common, true);
  assert.equal(core.analyse("password").score, 0);
  assert.equal(core.hasRepeatedCharacters("abbbbbcd"), true);
  assert.equal(core.hasRepeatedCharacters("abbbcd"), false);
  assert.equal(core.hasSequentialPattern("xxabcdYY"), true);
  assert.equal(core.hasSequentialPattern("xx9876YY"), true);
  assert.equal(core.hasSequentialPattern("axbycz"), false);
});
test("all category-constrained generation uses an injected crypto source", () => {
  let seed = 123456789;
  const fakeCrypto = { getRandomValues(array) { seed = (seed * 1664525 + 1013904223) >>> 0; array[0] = seed; return array; } };
  const password = core.generate(32, { upper: true, lower: true, digits: true, symbols: true }, fakeCrypto);
  assert.equal(password.length, 32);
  assert.match(password, /[A-Z]/); assert.match(password, /[a-z]/);
  assert.match(password, /[0-9]/); assert.match(password, /[!@#$%^&*()\-_=+\[\]{}.,?]/);
  assert.match(core.generate(12, { upper: false, lower: false, digits: true, symbols: false }, fakeCrypto), /^[0-9]{12}$/);
});
test("generator validates length, categories, and cryptographic randomness", () => {
  const fakeCrypto = { getRandomValues(array) { array[0] = 0; return array; } };
  assert.throws(() => core.generate(3, { upper: true, lower: true }, fakeCrypto), /between 8 and 128/);
  assert.throws(() => core.generate(8, {}, fakeCrypto), /at least one/);
  assert.throws(() => core.generate(12, { upper: true }, null), /Secure randomness/);
});
test("HTML exposes exactly three exclusive navigation modules", () => {
  const html = fs.readFileSync(path.join(__dirname, "../index.html"), "utf8");
  const navigation = html.match(/<nav\b[\s\S]*?<\/nav>/i)?.[0] || "";
  const labels = [...navigation.matchAll(/data-page="[^"]+"[^>]*>([\s\S]*?)<\/button>/g)]
    .map(match => match[1].replace(/<[^>]+>/g, "").trim());
  assert.deepEqual(labels, ["⌕ Analyser", "▤ Insights", "✣ Generate"]);
  const panel = name => html.match(new RegExp(`<section[^>]*data-page-panel="${name}"[\\s\\S]*?<\\/section>`, "i"))?.[0] || "";
  const analyser = panel("analyser"), insights = panel("insights"), generate = panel("generate");
  assert.match(analyser, /id="password-input"/);
  assert.match(analyser, /id="checklist"/);
  assert.doesNotMatch(insights, /id="password-input"|id="generate-button"|id="checklist"/);
  assert.match(insights, /id="pattern-list"/);
  assert.match(generate, /id="generate-button"/);
  assert.doesNotMatch(generate, /id="password-input"|id="checklist"|id="pattern-list"/);
});
test("browser app does not add password network, storage, or logging paths", () => {
  const app = fs.readFileSync(path.join(__dirname, "../js/app.js"), "utf8");
  const coreSource = fs.readFileSync(path.join(__dirname, "../js/core.js"), "utf8");
  const combined = app + coreSource;
  assert.doesNotMatch(combined, /\bfetch\s*\(|XMLHttpRequest|WebSocket|sendBeacon|localStorage|sessionStorage|console\.log/);
  assert.match(fs.readFileSync(path.join(__dirname, "../../render.yaml"), "utf8"), /connect-src 'none'/);
});
