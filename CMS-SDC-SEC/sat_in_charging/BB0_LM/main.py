#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
# 调用案例
import BB0_LM_point.BB0LM as BB0LM

if __name__ in "__main__":
    dt = ['2020-12-11 12:12:12']
    alt = [1500]  # km
    lat = [5]
    lon = [-180]
    data = BB0LM.main(dt, alt, lat, lon)
    print(type(data))
