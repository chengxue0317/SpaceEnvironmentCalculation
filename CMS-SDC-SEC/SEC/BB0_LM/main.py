#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
# 调用案例
import BB0_LM_point.BB0LM as BB0LM

if __name__ in "__main__":
    dt = ['2024-12-31 00:00:00', '2012-12-31 00:00:00', '2019-12-31 00:00:00']
    alt = [900, 489, 689]  # km
    lon = [46.9, 78, 169]
    lat = [56, 79, 23]
    data = BB0LM.main(dt, alt, lon, lat)
    print(data)
