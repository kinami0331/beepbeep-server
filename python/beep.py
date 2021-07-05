import numpy as np
import matplotlib.pyplot as plt
from scipy.io import wavfile
import time

fs, data = wavfile.read("44k_2000_6000_100_with_10ms_sinwave.wav")


def m_audioread(filename):
    fs, data = wavfile.read(filename)
    data = data / 2**15
    return data, fs


def correlation_gcc(received: np.ndarray, origin: np.ndarray, Fs, f_begin,
                    f_end):
    N = received.size
    nfft = N * 2 - 1
    Y = np.fft.fft(received, nfft)
    X = np.fft.fft(origin, nfft)

    # 归一化
    # CSpectrum=Y.*conj(X)
    # CSpectrum=CSpectrum./max(abs(CSpectrum)) %先归一化
    CSpectrum = Y * np.conj(X)
    CSpectrum = CSpectrum / np.max(np.abs(CSpectrum))

    # f = Fs*(-nfft/2:nfft/2-1)/nfft
    f = Fs * np.arange(-nfft / 2, nfft / 2, 1) / nfft

    ShiftedCSpectrum = np.fft.fftshift(CSpectrum)

    StartIndexP = np.where(f < f_begin)[-1][-1]
    EndIndexP = np.where(f > f_end)[0][0]
    StartIndexN = np.where(f < -f_end)[-1][-1]
    EndIndexN = np.where(f > -f_begin)[0][0]

    ShiftedCSpectrum[StartIndexP:EndIndexP +
                     1] = ShiftedCSpectrum[StartIndexP:EndIndexP + 1] / np.abs(
                         ShiftedCSpectrum[StartIndexP:EndIndexP + 1])
    ShiftedCSpectrum[StartIndexN:EndIndexN +
                     1] = ShiftedCSpectrum[StartIndexN:EndIndexN + 1] / np.abs(
                         ShiftedCSpectrum[StartIndexN:EndIndexN + 1])

    CSpectrum = np.fft.ifftshift(ShiftedCSpectrum)
    GCC = np.fft.fftshift(np.real(np.fft.ifft(CSpectrum)))
    # 时间序列
    lags = np.arange(-N + 1, N, 1) / Fs

    return (GCC, lags)


def find_local_peaks(data, radius):
    beginIdx = radius
    endIdx = data.size - 1 - radius
    x = np.zeros((2 * radius + 1, data.size - 2 * radius))
    s = endIdx - beginIdx + 1
    for i in range(2 * radius + 1):
        x[i] = data[i:i + s]

    locs = np.where(
        (data[beginIdx:endIdx + 1] == np.max(x, axis=0)))[0] + beginIdx
    pks = data[locs]
    # print(pks, locs)
    return (pks, locs)


def choose_peak_v5(c, lags):
    # 在全局峰值周围半径为800个样本的区间进行搜索
    peakRadius = 800
    # 计算锐度的窗口半径
    windowRadius = 50
    # 锐度临界
    threshold = 0.85

    maxIndex = np.argmax(c)
    maxValue = c[maxIndex]

    gammaMax = maxValue / np.mean(
        np.abs(
            c[maxIndex - windowRadius:maxIndex + windowRadius + 1])) - maxValue
    print("全局最大锐度：", gammaMax)
    peaks, locs = find_local_peaks(
        c[maxIndex - peakRadius:maxIndex + peakRadius + 1], 25)
    gammas = np.zeros(locs.size)
    filteredPeaksIdx = []
    for i in range(locs.size):
        tPeakLoc = maxIndex - peakRadius + locs[i]
        tBegin = tPeakLoc - windowRadius
        tEnd = tPeakLoc + windowRadius
        gammas[i] = peaks[i] / np.mean(np.abs(c[tBegin:tEnd + 1])) - peaks[i]

        if (gammas[i] > gammaMax * threshold) and (c[tPeakLoc] >
                                                   (maxValue / 2)):
            filteredPeaksIdx.append((lags[tPeakLoc], gammas[i]))

    lag = filteredPeaksIdx[0][0]
    return lag


def calc_time_of_arrival(received, origin, Fs, f_begin, f_end):
    c, lags = correlation_gcc(received, origin, Fs, f_begin, f_end)
    t = choose_peak_v5(c, lags)
    return t


def cut_received_signal(received: np.ndarray, origin: np.ndarray, Fs):
    received_len = received.size
    received_t = np.arange(0, received_len) / Fs
    # plt.plot(received_t, received, linewidth=0.3)
    # plt.title("233")
    # plt.xlabel("23")
    # plt.savefig("./t.png")

    radius = 20000

    t_origin = np.append(origin, np.zeros(received.size - origin.size))

    c = np.correlate(received, t_origin, "full")
    maxIndex1 = np.argmax(c)
    rangeStart1 = maxIndex1 - radius
    rangeEnd1 = maxIndex1 + radius
    tmp = np.concatenate(
        (c[0:rangeStart1], np.zeros(radius * 2 + 1), c[rangeEnd1 + 1:]))
    # tmp = np.append(c[0:rangeStart1], np.zeros(radius * 2 + 1))
    # tmp = np.append(tmp, c[rangeEnd1 + 1:])
    maxIndex2 = np.argmax(tmp)
    # cutpoint是第二段的开始
    if maxIndex1 < maxIndex2:
        cut_point = maxIndex2 - radius
    else:
        cut_point = maxIndex1 - radius

    cut_point = cut_point - received_len
    s1 = received[0:cut_point]
    s2 = received[cut_point:]
    return (s1, s2, cut_point)


def calc_toa_difference(received, origin, Fs, f_begin, f_end):
    s1, s2, cut = cut_received_signal(received, origin, Fs)
    t1 = calc_time_of_arrival(s1, origin, Fs, f_begin, f_end)
    t2 = calc_time_of_arrival(s2, origin, Fs, f_begin, f_end) + cut / Fs
    td = t2 - t1
    return td


def calc_distance(received_file1, received_file2, origin_file, m2s1, m2s2, Fs,
                  f_begin, f_end, sound_speed):
    origin, _ = m_audioread(origin_file)

    received, _ = m_audioread(received_file1)
    received = received[3999:]
    td1 = calc_toa_difference(received, origin, Fs, f_begin, f_end)

    received, _ = m_audioread(received_file2)
    received = received[3999:]
    td2 = calc_toa_difference(received, origin, Fs, f_begin, f_end)

    d = np.abs(td1 - td2) * sound_speed / 2 + (m2s1 + m2s2) / 2

    return d


if __name__ == "__main__":
    experimentPath = "../matlab/data/type1/67/"
    Fs = 44100
    fBegin = 2000
    fEnd = 6000
    m2s1 = 0.00
    m2s2 = 0.00
    soundSpeed = 347
    starttime = time.time()
    d = calc_distance(experimentPath + "AUM-AL20_to_H60-L01.wav",
                      experimentPath + "H60-L01_to_AUM-AL20.wav",
                      experimentPath + "chirp.wav", m2s1, m2s2, Fs, fBegin,
                      fEnd, soundSpeed)
    print(d)
    endtime = time.time()
    print('总共的时间为:', round(endtime - starttime, 2), 'secs')
