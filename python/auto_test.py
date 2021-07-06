import requests
import json

SOUND_SPEED = 346.5

parameter_list = [

    # 参数格式
    # fs f_begin f_End chirp_time warm_time sound_speed

    # 信号长度
    (44100, 2000, 6000, 50, 10, SOUND_SPEED),
    (44100, 2000, 6000, 100, 10, SOUND_SPEED),
    (44100, 2000, 6000, 150, 10, SOUND_SPEED),
    (44100, 2000, 6000, 200, 10, SOUND_SPEED),
    (44100, 2000, 6000, 250, 10, SOUND_SPEED),
    # 频段
    # (44100, 2000, 6000, 100, 10, SOUND_SPEED), 前面测过了，直接比较
    (44100, 7000, 11000, 100, 10, SOUND_SPEED),
    (44100, 12000, 16000, 100, 10, SOUND_SPEED),
    (44100, 16000, 20000, 100, 10, SOUND_SPEED),
    # 带宽
    # (44100, 2000, 6000, 100, 10, SOUND_SPEED), 前面测过了，直接比较
    (44100, 2000, 8000, 100, 10, SOUND_SPEED),
    (44100, 2000, 10000, 100, 10, SOUND_SPEED),
    (44100, 2000, 12000, 100, 10, SOUND_SPEED),
    # warm
    # (44100, 2000, 6000, 100, 10, SOUND_SPEED), 前面测过了，直接比较
    (44100, 2000, 6000, 100, 0, SOUND_SPEED),
]

# 每个参数的试验次数
EXPR_N = 5
# 实验输出文件夹
OUTPUT_PATH = "./output/"
# 实验结果文件名（csv格式）
OUTPUT_FILE_NAME = "test.csv"

SERVER_ADDR = "localhost:8844"

HEADER = {"Content-Type": "application/json"}


def print_res(res: requests.models.Response):
    print(res.status_code)
    print(res.encoding)
    print(res.text)
    print(res.content)


def get_device_list():
    url = "http://" + SERVER_ADDR + "/api/manage/device-list"
    res = requests.get(url)
    return json.loads(res.text)["data"]


def get_post_body(parameter):
    device_list = get_device_list()
    post_body = {}
    post_body["deviceList"] = device_list
    parameter_dict = {
        "samplingRate": parameter[0],
        "lowerLimit": parameter[1],
        "upperLimit": parameter[2],
        "chirpTime": parameter[3],
        "prepareTime": parameter[4],
        "soundSpeed": parameter[5]
    }
    post_body["chirpParameters"] = parameter_dict
    return json.dumps(post_body)


def start_with_cond(parameter, N=1):
    create_url = "http://" + SERVER_ADDR + "/api/experiment/create"
    begin_url = "http://" + SERVER_ADDR + "/api/experiment/begin"
    rst = []
    for i in range(N):
        print("    > expr " + str(i + 1) + ": ", end="")
        body = get_post_body(parameter)
        res = requests.post(create_url, data=body, headers=HEADER)
        if res.status_code != 200:
            print("wrong!")
        expr_id = json.loads(res.text)["data"]
        res = requests.post(begin_url,
                            data=json.dumps({"experimentId": expr_id}),
                            headers=HEADER)
        print(json.loads(res.text)["data"]["distance"])
        rst.append((expr_id, json.loads(res.text)["data"]["distance"]))
    return rst


def start_expr():
    csv_file = open(OUTPUT_PATH + OUTPUT_FILE_NAME, "w", encoding="utf-8")
    csv_file.write("parameter" +
                   "".join([", expr " + str(i + 1)
                            for i in range(EXPR_N)]) + "\n")
    csv_file.close()

    for parameter in parameter_list:
        print("parameter: " + str(parameter))
        csv_file = open(OUTPUT_PATH + OUTPUT_FILE_NAME, "a", encoding="utf-8")
        rst = start_with_cond(parameter, EXPR_N)
        csv_file.write(
            "\"" + str(parameter).replace(", ", " | ") + "\"" +
            "".join([", " + str(x).replace(", ", " | ") for x in rst]) + "\n")
        csv_file.close()
        exit(0)


if __name__ == "__main__":
    start_expr()
