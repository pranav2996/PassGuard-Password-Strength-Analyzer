(() => {
  "use strict";
  const $ = id => document.getElementById(id);
  const tabs = [...document.querySelectorAll(".tab")];
  const panels = [...document.querySelectorAll("[data-page-panel]")];
  let lastAnalysis = null;
  let generatedPassword = "";

  function showPage(name) {
    for (const tab of tabs) {
      const active = tab.dataset.page === name;
      tab.classList.toggle("active", active);
      tab.setAttribute("aria-selected", String(active));
      tab.tabIndex = active ? 0 : -1;
    }
    for (const panel of panels) {
      const active = panel.dataset.pagePanel === name;
      panel.hidden = !active;
      panel.classList.toggle("active", active);
    }
  }
  tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => showPage(tab.dataset.page));
    tab.addEventListener("keydown", event => {
      if (!["ArrowLeft", "ArrowRight", "Home", "End"].includes(event.key)) return;
      event.preventDefault();
      const next = event.key === "Home" ? 0 : event.key === "End" ? tabs.length - 1 : (index + (event.key === "ArrowRight" ? 1 : tabs.length - 1)) % tabs.length;
      tabs[next].focus();
      tabs[next].click();
    });
  });
  document.querySelectorAll("[data-go]").forEach(button => button.addEventListener("click", () => showPage(button.dataset.go)));

  function setChecklist(analysis) {
    for (const item of document.querySelectorAll("#checklist li")) {
      const passed = Boolean(analysis && analysis[item.dataset.check]);
      item.classList.toggle("passed", passed);
      item.querySelector(".check-icon").textContent = passed ? "✓" : "○";
    }
  }
  function updateInsights(analysis) {
    $("insights-empty").hidden = Boolean(analysis);
    $("insights-results").hidden = !analysis;
    if (!analysis) return;
    const flagged = analysis.common || analysis.repeated || analysis.sequential;
    const banner = $("pattern-status");
    banner.classList.toggle("warning", flagged);
    banner.textContent = flagged ? "⚠ Review these warnings" : "✓ No listed pattern detected";
    const patterns = [
      ["Common-password match", analysis.common, analysis.common ? "Matched the local common-password list." : "No exact match in the local list."],
      ["Repeated characters", analysis.repeated, analysis.repeated ? "A run of four or more identical characters was found." : "No run of four identical characters found."],
      ["Sequential pattern", analysis.sequential, analysis.sequential ? "A four-character ascending or descending sequence was found." : "No four-character sequence found."]
    ];
    renderList("pattern-list", patterns.map(([name, bad, detail]) => ({ text: `${bad ? "⚠" : "✓"} ${name} — ${detail}`, kind: bad ? "warning" : "good" })));
    renderList("reason-list", analysis.reasons.map(text => ({ text })));
    renderList("tips-list", analysis.tips.map(text => ({ text: `• ${text}` })));
  }
  function renderList(id, entries) {
    const list = $(id);
    list.replaceChildren(...entries.map(entry => {
      const item = document.createElement("li");
      item.textContent = entry.text;
      if (entry.kind) item.className = entry.kind;
      return item;
    }));
  }
  function resetAnalysis() {
    $("password-input").value = "";
    $("show-password").checked = false;
    $("password-input").type = "password";
    $("toggle-visibility").setAttribute("aria-pressed", "false");
    $("toggle-visibility").setAttribute("aria-label", "Show password");
    lastAnalysis = null;
    $("strength-label").textContent = "Not analysed";
    $("strength-label").className = "";
    $("score-label").textContent = "SCORE · 0 / 7";
    $("meter-fill").style.width = "0%";
    $("meter-fill").parentElement.setAttribute("aria-valuenow", "0");
    setChecklist(null);
    updateInsights(null);
  }
  function analyseInput() {
    const password = $("password-input").value;
    if (!password) { resetAnalysis(); return; }
    lastAnalysis = PassGuardCore.analyse(password);
    $("strength-label").textContent = lastAnalysis.strength;
    $("strength-label").className = lastAnalysis.strength.toLowerCase().replaceAll(" ", "-");
    $("score-label").textContent = `SCORE · ${lastAnalysis.score} / 7`;
    $("meter-fill").style.width = `${(lastAnalysis.score / 7) * 100}%`;
    $("meter-fill").parentElement.setAttribute("aria-valuenow", String(lastAnalysis.score));
    setChecklist(lastAnalysis);
    updateInsights(lastAnalysis);
  }
  $("analysis-form").addEventListener("submit", event => { event.preventDefault(); analyseInput(); });
  $("clear-analysis").addEventListener("click", resetAnalysis);
  $("show-password").addEventListener("change", event => {
    $("password-input").type = event.target.checked ? "text" : "password";
    $("toggle-visibility").setAttribute("aria-pressed", String(event.target.checked));
    $("toggle-visibility").setAttribute("aria-label", event.target.checked ? "Hide password" : "Show password");
  });
  $("toggle-visibility").addEventListener("click", () => {
    $("show-password").checked = !$("show-password").checked;
    $("show-password").dispatchEvent(new Event("change"));
  });

  const generatorOptions = { upper: "use-upper", lower: "use-lower", digits: "use-digits", symbols: "use-symbols" };
  function syncChips() {
    for (const [key, id] of Object.entries(generatorOptions)) $(`[data-chip="${key}"]`).hidden = !$(id).checked;
  }
  Object.values(generatorOptions).forEach(id => $(id).addEventListener("change", syncChips));
  function clearGenerator() {
    generatedPassword = "";
    $("generated-password").textContent = "Your generated password will appear here.";
    $("copy-button").disabled = true;
    $("copy-icon").disabled = true;
    $("generator-feedback").textContent = "Generated locally using your browser's secure random number generator.";
    $("generator-feedback").className = "subheading";
  }
  $("generate-button").addEventListener("click", () => {
    try {
      const enabled = Object.fromEntries(Object.entries(generatorOptions).map(([key, id]) => [key, $(id).checked]));
      generatedPassword = PassGuardCore.generate($("length-input").value, enabled);
      $("generated-password").textContent = generatedPassword;
      $("copy-button").disabled = false;
      $("copy-icon").disabled = false;
      $("generator-feedback").textContent = "Generated locally. Copy only where you intend to use it.";
      $("generator-feedback").className = "subheading success-text";
    } catch (error) {
      generatedPassword = "";
      $("generated-password").textContent = error.message;
      $("copy-button").disabled = true;
      $("copy-icon").disabled = true;
      $("generator-feedback").textContent = error.message;
      $("generator-feedback").className = "subheading error-text";
    }
  });
  async function copyPassword() {
    if (!generatedPassword) return;
    try {
      await navigator.clipboard.writeText(generatedPassword);
      $("generator-feedback").textContent = "Copied. The system clipboard may retain it until replaced.";
      $("generator-feedback").className = "subheading success-text";
    } catch {
      $("generator-feedback").textContent = "Clipboard access is unavailable. Select and copy the generated value manually.";
      $("generator-feedback").className = "subheading error-text";
    }
  }
  $("copy-button").addEventListener("click", copyPassword);
  $("copy-icon").addEventListener("click", copyPassword);
  $("clear-generator").addEventListener("click", clearGenerator);
  syncChips();
  showPage("analyser");

  function animateBackground() {
    const canvas = $("ambient");
    const context = canvas.getContext("2d", { alpha: true });
    const reduced = matchMedia("(prefers-reduced-motion: reduce)");
    const particles = Array.from({ length: 46 }, (_, i) => ({ x: ((i * 193 + 31) % 997) / 997, y: ((i * 317 + 67) % 991) / 991, size: 1 + (i % 3) * .65, phase: i * 1.37 }));
    let width = 0, height = 0, phase = 0, frame = 0;
    function resize() {
      const ratio = Math.min(devicePixelRatio || 1, 2);
      width = canvas.clientWidth; height = canvas.clientHeight;
      canvas.width = Math.round(width * ratio); canvas.height = Math.round(height * ratio);
      context.setTransform(ratio, 0, 0, ratio, 0, 0);
    }
    function draw() {
      frame = 0;
      if (!width || !height) return;
      context.clearRect(0, 0, width, height);
      const waves = [
        { base: .29, amp: .075, freq: .008, offset: .4, color: [26, 193, 255] },
        { base: .37, amp: .06, freq: .0065, offset: 2.1, color: [164, 79, 255] },
        { base: .79, amp: .055, freq: .0072, offset: 1.7, color: [46, 122, 255] },
        { base: .86, amp: .072, freq: .0058, offset: 4.1, color: [192, 68, 255] }
      ];
      for (const wave of waves) {
        context.beginPath();
        for (let x = 0; x <= width + 8; x += 8) {
          const y = height * wave.base + Math.sin(x * wave.freq + phase + wave.offset) * height * wave.amp + Math.sin(x * wave.freq * .43 + phase * .65 + wave.offset) * height * wave.amp * .24;
          if (x === 0) context.moveTo(x, y); else context.lineTo(x, y);
        }
        const gradient = context.createLinearGradient(0, height * (wave.base - wave.amp), 0, height);
        gradient.addColorStop(0, `rgba(${wave.color.join(",")},0)`);
        gradient.addColorStop(1, `rgba(${wave.color.join(",")},.055)`);
        context.lineTo(width, height); context.lineTo(0, height); context.closePath();
        context.fillStyle = gradient; context.fill();
        context.strokeStyle = `rgba(${wave.color.join(",")},.34)`; context.lineWidth = 1.3; context.stroke();
      }
      for (const particle of particles) {
        const twinkle = .4 + .6 * Math.abs(Math.sin(phase * .8 + particle.phase));
        context.beginPath();
        context.arc(particle.x * width + Math.sin(phase + particle.phase) * 3, particle.y * height, particle.size * twinkle, 0, Math.PI * 2);
        context.fillStyle = `rgba(146,190,255,${.15 + twinkle * .35})`;
        context.shadowBlur = 10; context.shadowColor = "#ac70ff"; context.fill();
      }
      context.shadowBlur = 0;
      if (!reduced.matches && !document.hidden) { phase += .012; frame = requestAnimationFrame(draw); }
    }
    function schedule() { if (!frame && !reduced.matches && !document.hidden) frame = requestAnimationFrame(draw); }
    new ResizeObserver(resize).observe(canvas);
    reduced.addEventListener("change", () => { if (reduced.matches && frame) cancelAnimationFrame(frame); frame = 0; if (reduced.matches) draw(); else schedule(); });
    document.addEventListener("visibilitychange", () => { if (document.hidden && frame) cancelAnimationFrame(frame); frame = 0; schedule(); });
    resize(); draw();
  }
  animateBackground();
})();
