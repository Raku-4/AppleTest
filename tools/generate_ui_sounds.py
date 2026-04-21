import math
import os
import struct
import wave

ROOT = r"C:\Users\yoush\AppData\Roaming\.minecraft\Mods\AppleTest"
OUT_DIR = os.path.join(ROOT, "src", "resources", "assets", "fruitGame", "sounds")
os.makedirs(OUT_DIR, exist_ok=True)

SAMPLE_RATE = 44100
AMP = 0.28


def write_tone(path: str, freqs: list[float], duration: float) -> None:
    n = int(SAMPLE_RATE * duration)
    frames = bytearray()
    fade = int(SAMPLE_RATE * 0.01)

    for i in range(n):
        t = i / SAMPLE_RATE
        s = 0.0
        for f in freqs:
            s += math.sin(2.0 * math.pi * f * t)
        s /= max(1, len(freqs))

        # quick fade-in/out to avoid click noise
        g = 1.0
        if i < fade:
            g = i / fade
        elif i > n - fade:
            g = max(0.0, (n - i) / fade)

        v = int(max(-1.0, min(1.0, s * AMP * g)) * 32767)
        frames.extend(struct.pack("<h", v))

    with wave.open(path, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SAMPLE_RATE)
        w.writeframes(bytes(frames))


def write_sequence(path: str, tones: list[tuple[list[float], float]], gap: float = 0.02) -> None:
    temp = []
    gap_n = int(SAMPLE_RATE * gap)
    silence = struct.pack("<h", 0) * gap_n
    for freqs, d in tones:
        n = int(SAMPLE_RATE * d)
        frames = bytearray()
        fade = int(SAMPLE_RATE * 0.01)
        for i in range(n):
            t = i / SAMPLE_RATE
            s = sum(math.sin(2.0 * math.pi * f * t) for f in freqs) / max(1, len(freqs))
            g = 1.0
            if i < fade:
                g = i / fade
            elif i > n - fade:
                g = max(0.0, (n - i) / fade)
            v = int(max(-1.0, min(1.0, s * AMP * g)) * 32767)
            frames.extend(struct.pack("<h", v))
        temp.append(bytes(frames))
    all_data = silence.join(temp)
    with wave.open(path, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SAMPLE_RATE)
        w.writeframes(all_data)


write_tone(os.path.join(OUT_DIR, "click.wav"), [880.0], 0.06)
write_sequence(os.path.join(OUT_DIR, "open.wav"), [([660.0], 0.06), ([990.0], 0.08)])
write_sequence(os.path.join(OUT_DIR, "close.wav"), [([990.0], 0.06), ([660.0], 0.08)])
write_tone(os.path.join(OUT_DIR, "select.wav"), [1200.0], 0.05)
write_sequence(os.path.join(OUT_DIR, "page.wav"), [([740.0], 0.04), ([880.0], 0.05)])

print("generated:", OUT_DIR)

