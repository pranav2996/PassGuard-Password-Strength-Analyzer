$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$output = Join-Path $root 'target\test-classes'
New-Item -ItemType Directory -Force -Path $output | Out-Null
javac --release 17 -encoding UTF-8 -d $output `
    (Join-Path $root 'src\main\java\PasswordSecurity.java') `
    (Join-Path $root 'src\main\java\PasswordAnalyzer.java') `
    (Join-Path $root 'src\test\java\PasswordSecurityTest.java')
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
java -cp $output PasswordSecurityTest
if ($LASTEXITCODE -ne 0) { throw 'Automated checks failed.' }
