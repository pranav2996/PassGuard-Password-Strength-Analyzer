# PassGuard - Password Strength Analyzer

PassGuard is a Java Swing desktop application that evaluates password strength, detects common and predictable patterns, and generates stronger password suggestions.

## Features

- Password strength analysis with a score out of 7.
- Detection of common passwords.
- Detection of repeated characters and sequential patterns.
- Strong password generator using SecureRandom.
- Show and hide password option.
- Copy generated passwords to clipboard.
- Dynamic password improvement tips.
- Clear option to reset the analyzer.
- Local password processing.

## Technologies Used

- Java
- Java Swing
- AWT
- SecureRandom
- Git and GitHub

## How to Run

### Prerequisites

- Java Development Kit (JDK)

### Steps

1. Clone the repository:

   git clone https://github.com/srivarsan1407/Passguard.git

2. Navigate to the project folder:

   cd Passguard/Password_Strength_Analyzer

3. Compile the program:

   javac PasswordAnalyzer.java

4. Run the application:

   java PasswordAnalyzer

## Project Structure

    Passguard/
    |-- .gitignore
    |-- README.md
    |-- Password_Strength_Analyzer/
        |-- PasswordAnalyzer.java

## Privacy and Security

PassGuard analyzes passwords locally. It does not send passwords to an external server or store them.

Note: This is an educational password-strength estimation tool. It does not perform breached-password checks or guarantee password security.

## Author

Developed as a Java desktop application project.
