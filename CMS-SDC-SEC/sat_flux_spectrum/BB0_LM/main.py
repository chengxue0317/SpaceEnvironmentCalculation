#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
# 调用案例
import BB0_LM_point.BB0LM as BB0LM

if __name__ in "__main__":
    dt = ['2011-01-01 07:55:00']
    alt = [817.86]  # km
    lon = [19.3]
    lat = [70.54]
    data = BB0LM.main(dt, alt, lon, lat)
    print(data)
