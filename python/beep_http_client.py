import requests
import json

SERVER_ADDR = "localhost:8844"

HEADER = {"Content-Type": "application/json"}


def post(url, body):
    res = requests.post(url, data=json.dumps(body), headers=HEADER)
    if res.status_code != 200:
        raise Exception("网络请求错误")
    elif (int(json.loads(res.text)["code"]) != 200):
        raise Exception(
            "服务器错误",
            str(json.loads(res.text)["code"]) + ", " +
            json.loads(res.text)["message"])
    return json.loads(res.text)["data"]


def get_device_list() -> list:
    url = "http://" + SERVER_ADDR + "/api/manage/device-list"
    res = requests.get(url)
    if res.status_code != 200:
        raise Exception("网络请求错误")
    elif (int(json.loads(res.text)["code"]) != 200):
        raise Exception(
            "服务器错误",
            json.loads(res.text)["code"] + ", " +
            json.loads(res.text)["message"])
    return json.loads(res.text)["data"]


def create_expr(data: dict) -> int:
    create_url = "http://" + SERVER_ADDR + "/api/experiment/create"
    expr_id = int(post(create_url, data))
    return expr_id


def expr_begin(expr_id: int) -> float:
    begin_url = "http://" + SERVER_ADDR + "/api/experiment/begin"
    return float(post(begin_url, {"experimentId": expr_id})["distance"])


def create_expr_group(device_list: list, expr_abstract: str,
                      real_distance: float):
    url = "http://" + SERVER_ADDR + "/api/experiment/group/create"
    post_body = {
        "deviceList": device_list,
        "exprAbstract": expr_abstract,
        "realDistance": real_distance
    }
    return int(post(url, post_body))


def add_expr_to_group(group_id: int, chirp_parameters: dict,
                      expr_id_list: list):
    url = "http://" + SERVER_ADDR + "/api/experiment/group/add"
    post_body = {
        "experimentGroupId": group_id,
        "beepMultiExpr": {
            "chirpParameters": chirp_parameters,
            "exprIdList": expr_id_list
        }
    }
    return post(url, post_body)


def print_res(res: requests.models.Response):
    print(res.status_code)
    print(res.encoding)
    print(res.text)
    print(res.content)
