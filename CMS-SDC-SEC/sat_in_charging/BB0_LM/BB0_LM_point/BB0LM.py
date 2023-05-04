#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
import os
import sys


def main(dt, alt, lat, lon):
    cwd = os.getcwd()
    os.chdir(os.path.abspath(os.path.dirname(__file__)))
    sys.path.insert(0, os.path.abspath(os.path.dirname(__file__)))
    # import chaos_BB0.BB0_point as BB0
    # bb0 = BB0.main(dt.copy(), alt, lat, lon)
    import IRBEM_LM.Lm_point as LM
    lm, blocal, bmin = LM.main(dt, alt, lon, lat)
    bb0 = [b / b0 for b, b0 in zip(blocal, bmin)]
    data_out = {'bb0': bb0, 'LM': lm, 'lat': lat, 'lon': lon, 'date': dt}
    os.chdir(cwd)
    return data_out