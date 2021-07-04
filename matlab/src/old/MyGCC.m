function [GCC,Lags] = MyGCC(y,x,Fs,Fc,B)
%MYGCC �˴���ʾ�йش˺�����ժҪ
%   �˴���ʾ��ϸ˵��

%%%���������
N=length(y);
nfft=length(y)*2-1;
Y=fft(y,nfft);
X=fft(x,nfft);
CSpectrum=Y.*conj(X);
% CSpectrum=SW.*conj(RW);
Lags=(-N+1:N-1);%ʱ������

%����Ƶ�ΰ׻�
CSpectrum=CSpectrum./max(abs(CSpectrum)); %�ȹ�һ��
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

