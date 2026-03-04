package com.geovannymcode.demo;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

/**
 * Service that sends audio to OpenAI Whisper via Spring AI.
 * <p>
 * To switch provider: replace this class with Gemini, Anthropic, Deepgram, etc.
 * The addon doesn't care — it just delivers byte[].
 */
@Service
public class TranscriptionService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public TranscriptionService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(byte[] audioBytes, String mimeType) {
        var options = OpenAiAudioTranscriptionOptions.builder()
                .language("es")
                .responseFormat(TranscriptResponseFormat.TEXT)
                .build();

        var resource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "recording." + extensionFrom(mimeType);
            }
        };

        var prompt = new AudioTranscriptionPrompt(resource, options);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }

    private String extensionFrom(String mimeType) {
        if (mimeType == null) return "webm";
        return switch (mimeType.split(";")[0].trim().toLowerCase()) {
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
