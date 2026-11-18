# Security and privacy notes

## Local processing and data flow

Password analysis and generation run in the desktop process. PassGuard does not transmit passwords, persist them to a file, or use a backend or database. The code has no network client. Copying a generated candidate is the sole feature that deliberately places a password on an external system surface: the operating system clipboard.

## Random generation

The generator uses Java `SecureRandom`, requires at least one selected character group, inserts one random character from each selected group, fills the remaining length from their union, and shuffles positions. The UI allows 8–128 characters and defaults all groups on. Generation randomness is intended for candidate creation; this project has not undergone cryptographic review or formal validation against a security standard.

## Password handling in memory

The application reads a `char[]` from `JPasswordField` and overwrites that returned array after analysis. The analysis API currently accepts a `String`, and Swing's field and intermediate strings are immutable or managed by the UI toolkit, so reliable memory erasure cannot be promised. PassGuard does not log passwords. Do not use a real account password for demonstrations when a sample will do.

## Clipboard

Clipboard content can be read by other applications, clipboard history, synchronization services or a clipboard manager, depending on the operating system and settings. PassGuard reports successful copy but does not clear the system clipboard later, because doing so could overwrite newer content the user copied. Avoid copying a generated value on a shared device.

## Analysis limitations

The common-password check uses a small embedded list and case-insensitive exact matching. Repetition and sequential checks only catch simple four-character runs. There is no breach lookup, password reuse check, personal-information detection, password storage, or online attack model. A “Very Strong” label means only that the local heuristic awarded all seven points; it is not a promise of security. The application's composition preferences are optional generator controls and should not be interpreted as a recommendation that every real service must impose character-class rules.

## Reporting

This repository contains an educational tool, not a credential manager or server-side password verifier. Report suspected defects through the repository's issue process without including real passwords or other secrets.
