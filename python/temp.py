from scipy.io import wavfile

fs, data = wavfile.read("44k_2000_6000_100_with_10ms_sinwave.wav")
# print(data.dtype)
# print(data.shape)
# origin = data / (2**15)
# print(origin.dtype)
# print(fs, data)
# n = data.size * 2 - 1
# print(n)
# X = np.fft.fft(origin, n)
# print(X)
# print(type(X))

