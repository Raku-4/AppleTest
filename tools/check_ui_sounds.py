import os
import wave

p = r"C:\Users\yoush\AppData\Roaming\.minecraft\Mods\AppleTest\src\resources\assets\fruitGame\sounds"
for f in sorted(os.listdir(p)):
    fp = os.path.join(p, f)
    with wave.open(fp, "rb") as w:
        d = w.getnframes() / w.getframerate()
        print(f"{f}: {d:.3f}s, {w.getframerate()}Hz")

