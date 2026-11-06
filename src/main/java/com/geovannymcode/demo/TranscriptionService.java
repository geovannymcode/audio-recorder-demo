package com.geovannymcode.demo;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service that sends audio to OpenAI Whisper via Spring AI.
 * <p>
 * To switch provider: replace this class with Gemini, Anthropic, Deepgram, etc.
 * The addon doesn't care — it just delivers byte[].
 */
@Service
public class TranscriptionService {

    private static final int MAX_AUDIO_BYTES = 25_000_000;

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public TranscriptionService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(byte[] audioBytes, String mimeType) {
        if (audioBytes.length == 0 || audioBytes.length > MAX_AUDIO_BYTES) {
            throw new IllegalArgumentException("El audio debe tener contenido y pesar como máximo 25 MB.");
        }

        var resource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "recording." + extensionFrom(mimeType);
            }
        };

        var prompt = new AudioTranscriptionPrompt(resource);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }

    private String extensionFrom(String mimeType) {
        if (mimeType == null) return "webm";
        return switch (mimeType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT)) {
            case "audio/webm"              -> "webm";
            case "audio/ogg"               -> "ogg";
            case "audio/wav", "audio/x-wav" -> "wav";
            case "audio/mp4"               -> "mp4";
            case "audio/mpeg"              -> "mp3";
            case "audio/flac"              -> "flac";
            case "audio/aac"               -> "aac";
            default                         -> "webm";
        };
    }
}
