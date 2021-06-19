function [td] = computeTimeDifference(record, origin, samplingRate, recordFile)

[path, name, ext] = fileparts(recordFile);
outputFigPath = path + "/" + name;

len1 = length(record);
t1 = (0:len1-1)/samplingRate;

plot(t1,record);
title("原始信号");


% 互相关
[c,lags] = xcorr(record, origin);

fig = figure;
stem(lags/samplingRate, c);
title({name, "原始峰值"}, 'Interpreter', 'none')
saveas(fig,strcat(outputFigPath, "_fig0.png"));

% 原始信号数
stdSampleNum = 20000;
% 首先找到一个全局最大值
[maxValue, maxIndex] = max(c);
rangeStart1 = maxIndex - stdSampleNum;
rangeEnd1 = rangeStart1 + stdSampleNum + stdSampleNum - 1;
peak1 = lags(1, maxIndex);

fig = figure;
stem(lags(rangeStart1:rangeEnd1)/samplingRate, c(rangeStart1:rangeEnd1))
title({name, "第一处峰值"}, 'Interpreter', 'none')
xlabel("时延")
saveas(fig,strcat(outputFigPath, "_fig1.png"));

fig = figure;
stem(lags(maxIndex-800:maxIndex+800)/samplingRate, c(maxIndex-800:maxIndex+800))
title({name, "第一处峰值局部放大"}, 'Interpreter', 'none')
xlabel("时延")
saveas(fig,strcat(outputFigPath, "_fig2.png"));
lags(1,maxIndex)/samplingRate

fig = figure;
tmpIndex1 = rangeStart1 - len1;
tmpIndex2 = rangeEnd1-len1;
if tmpIndex1 <= 0
    tmpIndex1 = 1;
end
if tmpIndex2 <= 0
    tmpIndex2 = 1;
end
plot(t1(tmpIndex1:tmpIndex2), record(tmpIndex1:tmpIndex2));
title({name, "第一处峰值附近的接收信号"}, 'Interpreter', 'none');
saveas(fig,strcat(outputFigPath, "_fig3.png"));


% 在其余两段中找另一个全局最大值
tmp = [c(1:rangeStart1 - 1); zeros(stdSampleNum + stdSampleNum, 1); c(rangeEnd1 + 1:length(c))];
[maxValue, maxIndex] = max(tmp);
rangeStart2 = maxIndex - stdSampleNum;
rangeEnd2 = rangeStart2 + stdSampleNum + stdSampleNum - 1;
peak2 = lags(1, maxIndex);

fig = figure;
stem(lags(rangeStart2:rangeEnd2)/samplingRate, c(rangeStart2:rangeEnd2))
title({name, "第二处峰值"}, 'Interpreter', 'none')
xlabel("时延")
saveas(fig,strcat(outputFigPath, "_fig4.png"));

fig = figure;
stem(lags(maxIndex-800:maxIndex+800)/samplingRate, c(maxIndex-800:maxIndex+800))
title({name, "第二处峰值局部放大"}, 'Interpreter', 'none')
xlabel("时延")
saveas(fig,strcat(outputFigPath, "_fig5.png"));
lags(1,maxIndex)/samplingRate

fig = figure;
tmpIndex1 = rangeStart2 - len1;
tmpIndex2 = rangeEnd2 - len1;
if tmpIndex1 <= 0
    tmpIndex1 = 1;
end
if tmpIndex2 <= 0
    tmpIndex2 = 1;
end

plot(t1(tmpIndex1:tmpIndex2), record(tmpIndex1:tmpIndex2));
title({name, "第二处峰值附近的接收信号"}, 'Interpreter', 'none');
saveas(fig,strcat(outputFigPath, "_fig6.png"));



td = abs(peak1 - peak2) / samplingRate;
end

