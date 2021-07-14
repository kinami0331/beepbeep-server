import os
import json
import time
from beep_http_client import get_device_list, create_expr, expr_begin

# 每个参数的试验次数
EXPR_N = 6
# 实验结果文件名（csv格式）
OUTPUT_FILE_NAME = "0713_expr_27.json"

SOUND_SPEED = 346.5
parameter_list = [

    # 参数格式
    # fs f_begin f_End chirp_time warm_time sound_speed

    # 信号长度
    # (44100, 2000, 8000, 50, 10, SOUND_SPEED),
    # (44100, 2000, 6000, 100, 10, SOUND_SPEED),
    # (44100, 2000, 6000, 150, 10, SOUND_SPEED),
    # (44100, 2000, 6000, 200, 10, SOUND_SPEED),
    # (44100, 2000, 6000, 250, 10, SOUND_SPEED),
    # (44100, 2000, 8000, 100, 10, SOUND_SPEED),
    # (44100, 2000, 8000, 150, 10, SOUND_SPEED),
    # (44100, 2000, 8000, 200, 10, SOUND_SPEED),
    # (44100, 2000, 8000, 250, 10, SOUND_SPEED),
    # 频段
    # (44100, 2000, 6000, 100, 10, SOUND_SPEED), 前面测过了，直接比较
    # (44100, 7000, 11000, 100, 10, SOUND_SPEED),
    # (44100, 12000, 16000, 100, 10, SOUND_SPEED),
    # (44100, 16000, 20000, 100, 10, SOUND_SPEED),
    # # 带宽
    # # (44100, 2000, 6000, 100, 10, SOUND_SPEED), 前面测过了，直接比较
    # (44100, 2000, 8000, 100, 10, SOUND_SPEED),
    # (44100, 2000, 10000, 100, 10, SOUND_SPEED),
    # (44100, 2000, 12000, 100, 10, SOUND_SPEED),
    # 超声波
    (44100, 16000, 20000, 100, 10, SOUND_SPEED), 
    # (44100, 2000, 6000, 100, 0, SOUND_SPEED),
]

# 实验输出文件夹
OUTPUT_PATH = "./output/"


def generate_parameter_dict(parameter: tuple):
    return {
        "samplingRate": parameter[0],
        "lowerLimit": parameter[1],
        "upperLimit": parameter[2],
        "chirpTime": parameter[3],
        "prepareTime": parameter[4],
        "soundSpeed": parameter[5]
    }


def generate_create_expr_body(device_list: list, parameter: tuple):
    parameter_dict = generate_parameter_dict(parameter)
    post_body = {"deviceList": device_list, "chirpParameters": parameter_dict}
    return post_body


def start_with_cond(parameter: tuple, N: int = 1):
    rst = []
    device_list = get_device_list()
    for i in range(N):
        body = generate_create_expr_body(device_list, parameter)
        expr_id = create_expr(body)
        print("    > expr " + str(i + 1) + "[" + str(expr_id) + "]" + ": ",
              end="")
        distance = expr_begin(expr_id)
        print(distance)
        rst.append((expr_id, distance))
    return rst


def start_expr():

    if os.path.exists(OUTPUT_PATH + OUTPUT_FILE_NAME) and os.path.getsize(
            OUTPUT_PATH + OUTPUT_FILE_NAME) > 0:
        print("当前文件不为空！请注意是否覆盖")
        exit(0)
    else:
        f = open(OUTPUT_PATH + OUTPUT_FILE_NAME, "w", encoding="utf-8") 
        f.close()

    for parameter in parameter_list:
        print("parameter: " + str(parameter))
        # 读文件
        with open(OUTPUT_PATH + OUTPUT_FILE_NAME, "r", encoding="utf-8") as f:
            json_content = f.read()
            if len(json_content) == 0:
                cur_list = []
            else:
                cur_list = json.loads(json_content)
        # 实验
        rst = start_with_cond(parameter, EXPR_N)
        id_list = [x[0] for x in rst]
        distance_list = [x[1] for x in rst]
        parameter_dict = generate_parameter_dict(parameter)
        cur_list.append({
            "chirpParameters": parameter_dict,
            "exprIdList": id_list,
            "distanceList": distance_list
        })
        # 写回文件
        with open(OUTPUT_PATH + OUTPUT_FILE_NAME, "w", encoding="utf-8") as f:
            f.write(json.dumps(cur_list, indent=4))


if __name__ == "__main__":
    # time.sleep(10)
    start_expr()
