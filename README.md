# 🎙️ Audio Recorder Demo — OpenAI Whisper

Demo project that **consumes** the [Audio Recorder addon](https://github.com/geovannymcode/vaadin-audio-recorder) as a Maven dependency and connects it to OpenAI Whisper via Spring AI.

## Prerequisites

- Java 21
- Maven 3.9+
- OpenAI API Key (with Whisper access)
- The addon installed in your local Maven repo

## Setup

### Step 1 — Install the addon locally

```bash
# Go to your addon project (separate repo)
cd vaadin-audio-recorder
mvn clean install -DskipTests
```

This puts `audio-recorder-0.0.1.jar` in `~/.m2/repository/`.

### Step 2 — Set your OpenAI API Key

```bash
export OPENAI_API_KEY=sk-your-key-here
```

### Step 3 — Run this demo

```bash
cd audio-recorder-demo
mvn spring-boot:run
```

Open http://localhost:8080

### What you'll see

1. The **AudioRecorder** component with waveform visualization (from the addon)
2. Click **REC** → speak → click **STOP**
3. Audio bytes go to **OpenAI Whisper** via Spring AI
4. Transcription appears in the TextArea

## Project structure

```
audio-recorder-demo/              ← THIS repo (Spring Boot app)
├── pom.xml                       ← depends on the addon + spring-ai-openai
├── src/main/java/com/geovannymcode/demo/
│   ├── AudioRecorderDemoApplication.java
│   ├── TranscriptionService.java  ← calls OpenAI Whisper
│   └── MainView.java             ← Vaadin view using the addon
└── src/main/resources/
    └── application.properties     ← API key + Whisper config

vaadin-audio-recorder/             ← SEPARATE repo (the addon)
├── pom.xml
├── src/main/java/.../AudioRecorder.java
└── src/main/resources/.../audio-recorder-element.ts
```

## Switching AI provider

Only `TranscriptionService.java` changes. The addon doesn't care:

```java
// Gemini
return geminiClient.transcribe(audioBytes);

// Anthropic
return claudeClient.processAudio(audioBytes);

// Deepgram
return deepgram.transcribe(audioBytes);

// No AI — save to file
Files.write(Path.of("recording.webm"), audioBytes);
return "Saved!";
```
