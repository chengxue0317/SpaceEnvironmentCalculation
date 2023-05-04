# -*- coding: utf-8 -*-
import joblib as jl
import numpy as np
import math
import sys
def model_predict(model_fullpath):

    # 加载 model
    # model = jl.load('E:\Code\Python\机器学习\专家系统实战\model/model_one_dense.pkl')
    model = jl.load(model_fullpath)
    return model



def data_transform(list):
    X_data = np.array(list).reshape(1,len(list))
    return X_data

def max_locate(list_group):
    max_index = 0
    list_index = 0
    for num in list_group:
        if num > list_group[max_index]:
            max_index = list_index
        list_index += 1
    return max_index,list_group[max_index]



# def calculate_percentile(array,percentile):
#     size = len(array)
#     return sorted(array)[int(math.ceil((size * percentile) / 100)) - 1]

def percentile_cal(array):
    size = len(array)
    percentile_25 =sorted(array)[int(math.ceil((size * 25) / 100)) - 1]
    percentile_50 = sorted(array)[int(math.ceil((size * 50) / 100)) - 1]
    percentile_75 = sorted(array)[int(math.ceil((size * 75) / 100)) - 1]

    list_percentile = [percentile_25,percentile_50,percentile_75]
    return list_percentile


def reclassify(list_Y_pred,list_percentile):
    list_classify = []
    for num in list_Y_pred:
        if num< list_percentile[0]:
            list_classify.append("Very Low Probability")
            # print("很低")
        elif num<list_percentile[1]:
            list_classify.append("Relatively Low Probability")
            # print("较低")
        elif num<list_percentile[2]:
            list_classify.append("Relatively Hight Probability")
            # print("较高")
        else:
            list_classify.append("Very High Probability")
            # print("很高")
    # print(list_classify)
    return list_classify

def dict_creation(list_classify):
    dict_final = {}

    for i in range(len(list_classify)):
        dict_final.update({i:list_classify[i]})

    return dict_final


def str_to_list(str_data):
    list_data = str_data[1:(len(str_data) - 1)].split(',')

    list_data = list(map(float, list_data))
    return list_data

if __name__ == '__main__':
    # 模型调用，模型预测
    # model_path = "E:\Code\Python\机器学习\专家系统实战\故障诊断多参数 _202302223\model\Fault_Diagnose_20230301174534.pkl"
    model_path=sys.argv[1]
    model = model_predict(model_path)

    # 输入特征量 argv传参的时候列表会自动转换为字符串，需要通过str_to_list再转回来。
    # str_data = [0.8,200,5,200,-300,200]
    str_data =sys.argv[2]
    # list_data = [0.8, 200, 5, 200, -300, 200]
    # print(str_data)
    list_data = str_to_list(str_data)


    list_X_data = data_transform(list_data)
    # 输出目标量
    list_Y_pred = model.predict(list_X_data)[0]

    list_percentile=percentile_cal(list_Y_pred)

    list_classify = reclassify(list_Y_pred, list_percentile)

    dict_final = dict_creation(list_classify)

    print("###",dict_final,"###")
