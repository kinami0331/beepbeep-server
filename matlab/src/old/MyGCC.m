function [GCC,Lags] = MyGCC(y,x,Fs,Fc,B)
%MYGCC 此处显示有关此函数的摘要
%   此处显示详细说明

%%%基本互相关
N=length(y);
nfft=length(y)*2-1;
Y=fft(y,nfft);
X=fft(x,nfft);
CSpectrum=Y.*conj(X);
% CSpectrum=SW.*conj(RW);
Lags=(-N+1:N-1);%时间序列

%给定频段白化
CSpectrum=CSpectrum./max(abs(CSpectrum)); %先归一化
f = Fs*(-nfft/2:nfft/2-1)/nfft;
ShiftedCSpectrum=fftshift(CSpectrum);
StartIndexP=find(f==max(f(f<Fc)));
EndIndexP=find(f==min(f(f>Fc+B)));
StartIndexN=find(f==max(f(f<-(Fc+B))));
EndIndexN=find(f==min(f(f>-Fc)));
ShiftedCSpectrum(StartIndexP:EndIndexP)=ShiftedCSpectrum(StartIndexP:EndIndexP)./abs(ShiftedCSpectrum(StartIndexP:EndIndexP));
ShiftedCSpectrum(StartIndexN:EndIndexN)=ShiftedCSpectrum(StartIndexN:EndIndexN)./abs(ShiftedCSpectrum(StartIndexN:EndIndexN));
CSpectrum=ifftshift(ShiftedCSpectrum);
GCC=fftshift(real(ifft(CSpectrum)));

% startIndex=find(Lags==-MaxDelaySamples);
% endIndex=find(Lags==MaxDelaySamples);
% subGCC=GCC(startIndex:endIndex);
% subLags=Lags(startIndex:endIndex);
% subGCC=subGCC./(N-abs(subLags'));
end

