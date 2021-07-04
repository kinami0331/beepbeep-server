function [distance] = computeDistance(recordFile1, recordFile2, m2s1, m2s2, samplingRate, fBegin, fEnd, chirpTimeMs, soundSpeed)
%COMPUTEDISTANCE 此处显示有关此函数的摘要
%   此处显示详细说明

% 生成标准信号
[originChirpT, originChirpY] = generateChirp(samplingRate,fBegin, fEnd, chirpTimeMs, 0);

% 处理第一个文件
[recordY, recordFs] = audioread(recordFile1);
td1 = computeTimeDifferenceV1(recordY, originChirpY, samplingRate, recordFile1)

% 处理第二个文件
[recordY, recordFs] = audioread(recordFile2);
td2 = computeTimeDifferenceV1(recordY, originChirpY, samplingRate, recordFile2)

% 结果
distance = abs(td1 - td2) * soundSpeed / 2 + (m2s1 + m2s2) / 2;
end

