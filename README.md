# 🎙️ Audio Recorder Demo — OpenAI Whisper

A Vaadin Flow app that uses [Audio Recorder 0.2.0](https://github.com/geovannymcode/vaadin-audio-recorder/tree/7539578)
with Vaadin 25.3.5, Spring Boot 4.1 and Spring AI 2.0.

Record with your microphone or select an existing audio file with **Upload**.
Both paths send the audio to Whisper and display the transcription. Use **Play**
to listen to the last audio, and try switching between bars and waveform or
choosing one of the three recorder themes.

## Run locally

You'll need JDK 25, Maven 3.9+, Node.js 24+ with npm 11.3+, and an OpenAI API key
with access to `whisper-1`. Use a current browser and grant microphone permission
to record on localhost.

Install the add-on into your local Maven repository if it isn't already available.
The upstream project documents 0.2.0 as a source build:

```bash
git clone https://github.com/geovannymcode/vaadin-audio-recorder.git
git -C vaadin-audio-recorder checkout 7539578
mvn -f vaadin-audio-recorder/pom.xml clean install
```

From this demo's directory:

```bash
export OPENAI_API_KEY=your-api-key
mvn clean spring-boot:run
```

Open [localhost:8081](http://localhost:8081). Try recording a short phrase in
Spanish, then upload a small WAV, MP3 or WebM file. Recordings stop after 120
seconds, and the demo accepts audio up to 25 MB for transcription.

## Build for production

```bash
mvn -Pproduction clean verify
java -jar target/audio-recorder-demo-1.0.0.jar
```

The production profile bundles the add-on's frontend with the application.
The default Maven profile enables Vaadin's development server.

`MainView.java` wires the component's `AudioHandler` to `TranscriptionService.java`.
The service passes the audio to Spring AI with a filename matching its MIME type.
The transcription model, language and response format are configured in
`application.properties`.
