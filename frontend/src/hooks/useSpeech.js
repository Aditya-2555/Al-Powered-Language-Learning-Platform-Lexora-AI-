import { useState, useCallback } from 'react';

export const useSpeech = () => {
    const [isListening, setIsListening] = useState(false);
    const [transcription, setTranscription] = useState('');
    const [error, setError] = useState(null);

    const speak = useCallback((text, langCode) => {
        if (!('speechSynthesis' in window)) {
            setError("Text-to-speech not supported in this browser.");
            return;
        }
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(text);
        
        // Map app lang codes to valid BCP47 codes roughly
        const locMap = { 'en': 'en-US', 'es': 'es-ES', 'fr': 'fr-FR', 'de': 'de-DE' };
        utterance.lang = locMap[langCode] || langCode;
        
        window.speechSynthesis.speak(utterance);
    }, []);

    const listen = useCallback((langCode, onResult) => {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            setError("Microphone dictation not supported in this browser. Please use Chrome/Edge.");
            onResult(null, "Browser not supported.");
            return;
        }

        const recognition = new SpeechRecognition();
        const locMap = { 'en': 'en-US', 'es': 'es-ES', 'fr': 'fr-FR', 'de': 'de-DE' };
        recognition.lang = locMap[langCode] || langCode;
        recognition.interimResults = false;
        recognition.maxAlternatives = 1;

        recognition.onstart = () => {
            setIsListening(true);
            setTranscription('');
            setError(null);
        };

        recognition.onresult = (event) => {
            const resultText = event.results[0][0].transcript;
            setTranscription(resultText);
            setIsListening(false);
            if (onResult) onResult(resultText, null);
        };

        recognition.onerror = (event) => {
            setIsListening(false);
            setError(event.error);
            if (onResult) onResult(null, event.error);
        };
        
        recognition.onend = () => {
            setIsListening(false);
        };

        try {
            recognition.start();
        } catch(e) {
            console.error(e);
            setIsListening(false);
        }
    }, []);

    return { speak, listen, isListening, transcription, error };
};
