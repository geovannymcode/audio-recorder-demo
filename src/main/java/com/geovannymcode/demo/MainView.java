package com.geovannymcode.demo;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.vaadin.addons.audiorecorder.AudioRecorder;

@Route("")
@PageTitle("Audio Recorder Demo — OpenAI Whisper")
public class MainView extends VerticalLayout {

    public MainView(TranscriptionService transcriptionService) {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("700px");
        getStyle().set("margin", "0 auto");

        add(new H2("🎙️ Audio Recorder Demo + OpenAI Whisper"));
        add(new Paragraph(
                "Graba con el micrófono o elige un archivo con Upload. Puedes pausar y reanudar "
                + "la grabación y usar Play para escuchar el último audio. "
                + "Al detener la grabación o cargar un archivo, el audio se envía a OpenAI Whisper."));
        add(new Paragraph("Límites de la demo: 120 segundos por grabación y 25 MB por audio."));

        TextArea transcription = new TextArea("Transcripción (OpenAI Whisper)");
        transcription.setWidthFull();
        transcription.setMinHeight("120px");
        transcription.setReadOnly(true);
        transcription.setPlaceholder("Graba o carga un archivo para ver la transcripción.");

        Span status = new Span("Listo para grabar o cargar un archivo");
        status.getElement().setAttribute("role", "status");
        status.getStyle()
                .set("font-size", "13px")
                .set("color", "var(--lumo-secondary-text-color)");

        AudioRecorder recorder = new AudioRecorder(audioData ->
                transcriptionService.transcribe(audioData.bytes(), audioData.mimeType())
        );
        recorder.setCanvasSize(550, 130);
        recorder.applyDarkTheme();
        recorder.setVisualMode(AudioRecorder.VisualMode.BARS);
        recorder.setMaxDuration(120);

        recorder.addRecordingStartedListener(e -> {
            status.setText("🔴 Grabando...");
            transcription.clear();
        });

        recorder.addRecordingStoppedListener(e ->
                status.setText("⏳ Preparando la grabación (" + e.getDurationSeconds() + " s)…"));

        recorder.addHandlerResultListener(e -> {
            transcription.setValue(e.getResult());
            status.setText("✅ Transcripción completa");
            Notification.show("¡Listo!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        recorder.addRecordingErrorListener(e -> {
            transcription.clear();
            status.setText("❌ " + e.getMessage());
            Notification.show(e.getMessage(), 4000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        ComboBox<AudioRecorder.VisualMode> visualMode = new ComboBox<>("Visualización");
        visualMode.setItems(AudioRecorder.VisualMode.values());
        visualMode.setItemLabelGenerator(mode -> mode == AudioRecorder.VisualMode.BARS ? "Barras" : "Onda");
        visualMode.setValue(AudioRecorder.VisualMode.BARS);
        visualMode.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                recorder.setVisualMode(event.getValue());
            }
        });

        ComboBox<String> theme = new ComboBox<>("Tema");
        theme.setItems("Oscuro", "Claro", "Atardecer");
        theme.setValue("Oscuro");
        theme.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case "Claro" -> recorder.applyLightTheme();
                case "Atardecer" -> recorder.applySunsetTheme();
                case "Oscuro" -> recorder.applyDarkTheme();
                case null, default -> { }
            }
        });

        FlexLayout controls = new FlexLayout(visualMode, theme);
        controls.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        controls.getStyle().set("gap", "var(--lumo-space-m)");

        add(new H3("Graba o carga audio"), controls, recorder, status, transcription);

        add(new H3("El mismo handler para grabaciones y archivos"));
        Paragraph code = new Paragraph(
                """
                AudioRecorder recorder = new AudioRecorder(audio ->
                    transcriptionService.transcribe(audio.bytes(), audio.mimeType()));
                recorder.addHandlerResultListener(e ->
                    transcription.setValue(e.getResult()));
                add(recorder, transcription);
                """);
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
