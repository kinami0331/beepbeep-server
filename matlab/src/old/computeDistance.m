function [distance] = computeDistance(recordFile1, recordFile2, m2s1, m2s2, samplingRate, fBegin, fEnd, chirpTimeMs, soundSpeed)
%COMPUTEDISTANCE �˴���ʾ�йش˺�����ժҪ
%   �˴���ʾ��ϸ˵��

% ���ɱ�׼�ź�
[originChirpT, originChirpY] = generateChirp(samplingRate,fBegin, fEnd, chirpTimeMs, 0);

% �����һ���ļ�
[recordY, recordFs] = audioread(recordFile1);
td1 = computeTimeDifferenceV1(recordY, originChirpY, samplingRate, recordFile1)

% ����ڶ����ļ�
[recordY, recordFs] = audioread(recordFile2);
td2 = computeTimeDifferenceV1(recordY, originChirpY, samplingRate, recordFile2)

% ���
distance = abs(td1 - td2) * soundSpeed / 2 + (m2s1 + m2s2) / 2;
end

