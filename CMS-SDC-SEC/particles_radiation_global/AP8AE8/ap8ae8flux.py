import dmPython
import sys
import numpy as np
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]) + '/BB0_LM/')
import BB0_LM_point.BB0LM as BB0LM
import random
import os
import datetime
from datetime import timezone
from matplotlib import pyplot as plt
#from mpl_toolkits.basemap import Basemap
from scipy import interpolate
from PIL import Image
import pandas as pd





def ap8ae8(date, altitude, longitude, latitude, whatf, whichm, plot, energy=None):
    filedir = str(Path(__file__).resolve().parents[0])
    # 模式选择
    flux_dict = {'differential': 1, 'range': 2, 'above':3}
    model_dict = {'ae8min':1,'ae8max':2,'ap8min':3,'ap8max':4}
    # 生成随机数
    filenum = str(random.randint(1, 99999999))

    # 调用BB0LM程序
    data = BB0LM.main(date, altitude, longitude, latitude)
    import pandas as pd
    data = pd.DataFrame.from_dict(data)
    while True:
        # 创建文件夹供不同用户使用
        os.system('mkdir ' + filedir + '/' + filenum)
        # 创建该文件下的数据路径
        filepath = filedir + '/' + filenum + '/BB0LM.txt'
        if not os.path.exists(filepath):
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
    os.system('cd ' + filedir + '/' + filenum  + ';' + './ap8ae8')
    
    if plot == False:
        pass
    elif plot == True:
        print(filedir + '/' + filenum + '/flux.png')
        # exit()
        flux = readflux(whichm, energy, filepath)
        
        global_flux_plot(flux, (filedir + '/' + filenum + '/flux.png'))
        single_colorbar(flux, (filedir + '/' + filenum + '/colorbar.png'))
    return filenum,filedir











#date=['2024-12-31 00:00:00','2012-12-31 00:00:00','2019-12-31 00:00:00']
#alt=[900,489,689] #km
#lon=[46.9,78,169]
#lat=[56,79,23]
# ap8ae8(date, alt, lon, lat, whatf='range', whichm='ap8max', plot=True, energy='P1')
#ap8ae8(date, alt, lon, lat, whatf='range', whichm='ap8max', plot=False)
