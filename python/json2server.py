import json
from beep_http_client import create_expr_group, add_expr_to_group
from beep_http_client import get_device_list

json_file_name = "./output/0713_expr_27.json"
expr_abstract = "比刚才靠近了点"
real_distance = 2.117

experimentGroupId = create_expr_group(get_device_list(), expr_abstract,
                                      real_distance)

with open(json_file_name, "r", encoding="utf-8") as f:
    expr_list = json.loads(f.read())
    for item in expr_list:
        add_expr_to_group(experimentGroupId, item["chirpParameters"],
                          item["exprIdList"])
