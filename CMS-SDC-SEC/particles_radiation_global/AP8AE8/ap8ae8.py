"""
@File    ：ap8ae8.py
@Author  ：Wangrry
@Date    ：2022/12/26 16:44 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import sys
import pandas as pd
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]) + '/BB0_LM/')
import random
import os
import BB0_LM_point.BB0LM as BB0LM

filedir = str(Path(__file__).resolve().parents[0])

def ap8ae8(date, altitude, longitude, latitude, whatf, whichm):
    # 模式选择
    flux_dict = {'differential': 1, 'range': 2, 'above': 3}
    model_dict = {'ae8min': 1, 'ae8max': 2, 'ap8min': 3, 'ap8max': 4}
    while True:
        # 生成随机数
        filenum = str(random.randint(1, 99999999))
        if not os.path.exists(filedir + '/' + filenum):
            # 创建文件夹供不同用户使用
            os.system('mkdir ' + filedir + '/' + filenum)
            # 调用BB0LM程序
            data = BB0LM.main(date, altitude, longitude, latitude)
            data = pd.DataFrame.from_dict(data)
            # print(data)
            # exit()
            data.loc[data["LM"] < 1.14, "LM"] = 0
            data.loc[data["LM"] > 6.6, "LM"] = 0
            # print(data)
            # exit()
            # 创建该文件下的数据路径
            filepath = filedir + '/' + filenum + '/BB0LM.txt'
            with open(filepath, mode='w') as f:
                # 写入行数，模式，积分形式参数
                f.write(str(data.shape[0]) + '\n')
                f.write(str(flux_dict[whatf]) + '\n')
                f.write(str(model_dict[whichm]) + '\n')
            # 写入数据
            data.to_csv(filepath, mode='a+')
            break

    # 调用Fortran程序
    os.system('cp ' + filedir + '/ap8ae8 ' + filedir + '/' + filenum)
    os.system('cp ' + filedir + '/libirbem.so ' + filedir + '/' + filenum)
    os.system('cd ' + filedir + '/' + filenum + ';' + './ap8ae8')

    return filenum,filedir
