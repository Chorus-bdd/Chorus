# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Chorus is a BDD (Behaviour Driven Development) test interpreter for testing distributed/microservice architectures. Unlike conventional BDD tools, Chorus allows components to **publish step definitions over the network**, so the interpreter can connect to running components and execute their steps remotely. It supports both Java (JVM) and JavaScript components.

## Build Commands

```bash
./gradlew build                    # Build and test all modules
./gradlew clean build              # Clean build
./gradlew assemble                 # Build without running tests
./gradlew distZip                  # Create distribution zip
./gradlew javadoc                  # Generate Javadoc
```

## Running Tests

```bash
./gradlew test                                 # All tests across all modules
./gradlew :chorus:test                         # Core interpreter tests only
./gradlew :integrationtest:test                # Integration tests (requires Derby DB)
./gradlew :chorus:test --tests "*CommandLineParserTest*"  # Single test class
```

**Important**: The `:chorus` module has "selftests" that validate the interpreter's exact stdout output at INFO log level. If you add any `INFO`-level (or higher) log statements in the interpreter, existing selftest assertions will fail — update them to match the new output.

## License Headers

All `.java` files must include the MIT license header. Use the license plugin to fix or verify:

```bash
./gradlew licenseCheck
./gradlew licenseFormat
```

## Project Structure

```
interpreter/chorus/        # Core interpreter — depends only on JDK (compileOnly: Ant, JUnit, Commons Logging)
extensions/
  chorus-selenium/         # Selenium WebDriver integration
  chorus-spring/           # Spring Framework / dependency injection support
  chorus-sql/              # SQL/database testing
  chorus-websockets/       # WebSocket protocol support
services/
  chorus-xml/              # XML utilities
  chorus-mocksuite/        # Mock utilities for testing Chorus itself
  chorus-webagent/         # Web UI for remote test result reporting
integrationtest/           # Integration tests (Derby, Spring, Selenium)
site/chorus-website/       # Project website
```

Extensions are individually published to Maven Central. Services are internal utilities — not published.

## Core Architecture

### Interpreter Entry Points
- **CLI**: `org.chorusbdd.chorus.Chorus` (main class); `org.chorusbdd.chorus.Main` (backwards-compat alias)
- **JUnit**: `org.chorusbdd.chorus.ChorusSuite` extends `ParentRunner` — creates one JUnit test per feature

### Key Abstractions
- **`@Handler`** — marks a class as a step-definition provider; discovered via classpath scanning
- **`@Step`** — marks a method as a step, with a regex or literal pattern to match feature file steps
- **Remoting** — handlers can be hosted in separate processes/components and discovered over the network at runtime
- **`ExecutionToken` / `FeatureToken`** — results model passed to `ExecutionListener` implementations
- **Subsystems** — pluggable internal services (e.g., process management, remoting) managed via `@SubsystemConfig`

### Built-in Handlers (in `org.chorusbdd.chorus.handlers`)
Process management, Chorus context, remoting (network step definitions), timers, utilities.

### Layered Architecture
ArchUnit tests in `interpreter/chorus/src/test/java/org/chorusbdd/chorus/archunit/` enforce package layering — check these if you see architecture-violation test failures after adding new dependencies between packages.

### Output / Logging
The interpreter has two distinct output streams:
1. **Test results** — written via `ChorusOut` (standard output, structured result tokens)
2. **Diagnostic logging** — Commons Logging / Log4j (default level WARN; selftests run at INFO)

Remote listeners can receive serialized result tokens as the suite runs (used by chorus-webagent).
