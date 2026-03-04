package com.geovannymcode.demo;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// ═══ THIS IS THE ADDON — imported as a Maven dependency ═══
import org.vaadin.addons.audiorecorder.AudioRecorder;

/**
 * Demo view: records audio → sends to OpenAI Whisper → shows transcription.
 * <p>
 * This project is SEPARATE from the addon. It consumes it via:
 * {@code <dependency>org.vaadin.addons.audiorecorder:audio-recorder:0.0.1</dependency>}
 */
@Route("")
@PageTitle("Audio Recorder Demo — OpenAI Whisper")
public class MainView extends VerticalLayout {

    public MainView(TranscriptionService transcriptionService) {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("700px");
        getStyle().set("margin", "0 auto");

        // ── Header ──────────────────────────────────────────────────
        add(new H2("🎙️ Audio Recorder Demo + OpenAI Whisper"));
        add(new Paragraph(
                "Este proyecto consume el addon audio-recorder como dependencia Maven. "
                + "Graba tu voz → el addon captura los bytes → Spring AI los envía "
                + "a OpenAI Whisper → la transcripción aparece abajo."));

        // ── Transcription area ──────────────────────────────────────
        TextArea transcription = new TextArea("Transcripción (OpenAI Whisper)");
        transcription.setWidthFull();
        transcription.setMinHeight("120px");
        transcription.setReadOnly(true);
        transcription.setPlaceholder("Graba algo y aquí aparecerá la transcripción...");

        Span status = new Span("Listo para grabar");
        status.getStyle()
                .set("font-size", "13px")
                .set("color", "var(--lumo-secondary-text-color)");

        // ── AudioRecorder (from addon) + AudioHandler → Whisper ─────
        AudioRecorder recorder = new AudioRecorder(audioData ->
                transcriptionService.transcribe(audioData.bytes(), audioData.mimeType())
        );
        recorder.setCanvasSize(550, 130);
        recorder.applyDarkTheme();
        recorder.setVisualMode(AudioRecorder.VisualMode.BARS);
        recorder.setMaxDuration(120);

        // ── Events ──────────────────────────────────────────────────
        recorder.addRecordingStartedListener(e -> {
            status.setText("🔴 Grabando...");
            transcription.clear();
        });

        recorder.addRecordingStoppedListener(e ->
                status.setText("⏳ Enviando a OpenAI Whisper (" + e.getDurationSeconds() + "s)..."));

        recorder.addHandlerResultListener(e -> {
            transcription.setValue(e.getResult());
            status.setText("✅ Transcripción completa");
            Notification.show("Listo!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        recorder.addRecordingErrorListener(e -> {
            status.setText("❌ " + e.getMessage());
            Notification.show(e.getMessage(), 4000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        // ── Layout ──────────────────────────────────────────────────
        add(new H3("Graba y transcribe"));
        add(recorder, status, transcription);

        // ── Code preview ────────────────────────────────────────────
        add(new H3("Código de esta view"));
        Paragraph code = new Paragraph(
                "AudioRecorder recorder = new AudioRecorder(audioData -> "
                + "transcriptionService.transcribe(audioData.bytes(), audioData.mimeType())); "
                + "recorder.addHandlerResultListener(e -> textArea.setValue(e.getResult())); "
                + "add(recorder, textArea);");
        code.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("padding", "12px")
                .set("border-radius", "8px")
                .set("font-family", "monospace")
                .set("font-size", "12px")
                .set("white-space", "pre-wrap");
        add(code);
    }
}
