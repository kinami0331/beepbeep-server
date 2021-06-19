
function [t, y] = generateChirp(frequencyOfSample, fBegin, fEnd, chirpTimeMs, prepareTimeMs)

chirpSample = floor(chirpTimeMs * frequencyOfSample / 1000);
prepareSample = floor(prepareTimeMs * frequencyOfSample / 1000);

t1 = (0:prepareSample - 1) / frequencyOfSample;
y1 = sin(2 * pi * fBegin * t1);

t2 = (0:chirpSample) / frequencyOfSample;
k = (fEnd - fBegin) * 1000 / chirpTimeMs;
y2 = sin(2 * pi * (fBegin * t2 + 1/2 * k * t2 .* t2));

t = [t1, t2 + prepareSample / frequencyOfSample];
y = [y1, y2];
end

